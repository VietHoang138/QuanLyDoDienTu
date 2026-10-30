/**
 * TechNo Customer Live Chat Widget (WebSocket STOMP + REST)
 * Tự động tạo nút "Chat với tư vấn viên" nổi ở góc phải màn hình
 * Hỗ trợ lưu phiên chat, realtime WebSocket /topic/chat/{sessionId}
 */
(function() {
    // Tránh inject 2 lần
    if (window.__technoChatInitialized) return;
    window.__technoChatInitialized = true;

    // Helper nạp thư viện nếu chưa có
    function ensureScript(src, checkVar, callback) {
        if (window[checkVar]) {
            callback();
            return;
        }
        var s = document.createElement('script');
        s.src = src;
        s.onload = callback;
        document.head.appendChild(s);
    }

    ensureScript('https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js', 'axios', function() {
        ensureScript('https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js', 'SockJS', function() {
            ensureScript('https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js', 'StompJs', function() {
                initCustomerChat();
            });
        });
    });

    function initCustomerChat() {
        // Inject CSS
        var style = document.createElement('style');
        style.innerHTML = `
            /* ── Floating Chat Button & Container ── */
            #techno-chat-widget {
                position: fixed;
                bottom: 24px;
                right: 24px;
                z-index: 99999;
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            }

            .chat-floating-btn {
                background: linear-gradient(135deg, #2563eb, #1d4ed8);
                color: #ffffff;
                border: none;
                border-radius: 50px;
                padding: 12px 20px;
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 0.95rem;
                font-weight: 600;
                cursor: pointer;
                box-shadow: 0 8px 24px rgba(37, 99, 235, 0.35);
                transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
            }

            .chat-floating-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 12px 30px rgba(37, 99, 235, 0.45);
            }

            .chat-btn-icon {
                font-size: 1.2rem;
                display: flex;
                align-items: center;
            }

            .chat-btn-badge {
                position: absolute;
                top: -4px;
                right: -4px;
                background: #ef4444;
                color: #fff;
                font-size: 0.7rem;
                font-weight: 700;
                min-width: 18px;
                height: 18px;
                border-radius: 9px;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 0 4px;
                box-shadow: 0 0 0 2px #fff;
                animation: pulseChatBadge 1.8s infinite;
            }

            @keyframes pulseChatBadge {
                0%, 100% { transform: scale(1); }
                50% { transform: scale(1.15); }
            }

            /* ── Chat Window Box ── */
            .chat-window {
                position: fixed;
                bottom: 84px;
                right: 24px;
                width: 375px;
                max-width: calc(100vw - 32px);
                height: 540px;
                max-height: calc(100vh - 110px);
                background: #ffffff;
                border-radius: 18px;
                box-shadow: 0 16px 40px rgba(15, 23, 42, 0.2), 0 0 0 1px rgba(0,0,0,0.06);
                display: none;
                flex-direction: column;
                overflow: hidden;
                z-index: 100000;
                animation: chatSlideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
            }

            .chat-window.open {
                display: flex;
            }

            @keyframes chatSlideUp {
                from { opacity: 0; transform: translateY(20px) scale(0.96); }
                to { opacity: 1; transform: translateY(0) scale(1); }
            }

            /* Header */
            .chat-header {
                background: linear-gradient(135deg, #1e293b, #0f172a);
                color: #ffffff;
                padding: 14px 18px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }

            .chat-header-user {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .chat-header-avatar {
                width: 38px;
                height: 38px;
                border-radius: 50%;
                background: linear-gradient(135deg, #2563eb, #6366f1);
                color: #fff;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.1rem;
                box-shadow: 0 2px 6px rgba(0,0,0,0.2);
            }

            .chat-header-info h4 {
                margin: 0;
                font-size: 0.95rem;
                font-weight: 700;
                letter-spacing: -0.2px;
            }

            .chat-header-status {
                font-size: 0.74rem;
                color: #94a3b8;
                display: flex;
                align-items: center;
                gap: 5px;
                margin-top: 2px;
            }

            .chat-status-dot {
                width: 7px;
                height: 7px;
                border-radius: 50%;
                background: #10b981;
                box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.3);
            }

            .chat-header-actions {
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .chat-btn-action {
                background: rgba(255, 255, 255, 0.1);
                border: none;
                color: #cbd5e1;
                width: 28px;
                height: 28px;
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: all 0.2s;
                font-size: 0.9rem;
            }

            .chat-btn-action:hover {
                background: rgba(255, 255, 255, 0.2);
                color: #ffffff;
            }

            /* Body: Messages */
            .chat-body {
                flex: 1;
                padding: 16px;
                overflow-y: auto;
                background: #f8fafc;
                display: flex;
                flex-direction: column;
                gap: 12px;
            }

            /* Welcome card when starting */
            .chat-welcome-card {
                background: #ffffff;
                border: 1px solid #e2e8f0;
                border-radius: 14px;
                padding: 18px;
                text-align: center;
                box-shadow: 0 2px 6px rgba(0,0,0,0.03);
                margin: auto 0;
            }

            .chat-welcome-card i {
                font-size: 2rem;
                color: #2563eb;
                margin-bottom: 8px;
            }

            .chat-welcome-card h3 {
                font-size: 1.05rem;
                font-weight: 700;
                color: #0f172a;
                margin-bottom: 6px;
            }

            .chat-welcome-card p {
                font-size: 0.82rem;
                color: #64748b;
                margin-bottom: 14px;
                line-height: 1.4;
            }

            .chat-welcome-input {
                width: 100%;
                padding: 9px 12px;
                border: 1px solid #cbd5e1;
                border-radius: 8px;
                font-size: 0.88rem;
                margin-bottom: 10px;
                box-sizing: border-box;
                outline: none;
            }

            .chat-welcome-input:focus {
                border-color: #2563eb;
            }

            .btn-start-chat {
                width: 100%;
                background: #2563eb;
                color: #fff;
                border: none;
                padding: 10px;
                border-radius: 8px;
                font-size: 0.9rem;
                font-weight: 600;
                cursor: pointer;
                transition: background 0.2s;
            }

            .btn-start-chat:hover {
                background: #1d4ed8;
            }

            /* Message Bubbles */
            .chat-msg {
                display: flex;
                flex-direction: column;
                max-width: 80%;
                animation: msgFade 0.2s ease;
            }

            @keyframes msgFade {
                from { opacity: 0; transform: translateY(6px); }
                to { opacity: 1; transform: translateY(0); }
            }

            /* Message Customer (Right) */
            .chat-msg.customer {
                align-self: flex-end;
                align-items: flex-end;
            }

            .chat-msg.customer .chat-bubble {
                background: linear-gradient(135deg, #2563eb, #1d4ed8);
                color: #ffffff;
                border-radius: 16px 16px 4px 16px;
            }

            /* Message Admin (Left) */
            .chat-msg.admin {
                align-self: flex-start;
                align-items: flex-start;
            }

            .chat-msg.admin .chat-sender-name {
                font-size: 0.72rem;
                font-weight: 600;
                color: #64748b;
                margin-bottom: 3px;
                margin-left: 4px;
            }

            .chat-msg.admin .chat-bubble {
                background: #ffffff;
                color: #0f172a;
                border: 1px solid #e2e8f0;
                border-radius: 16px 16px 16px 4px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.04);
            }

            .chat-bubble {
                padding: 10px 14px;
                font-size: 0.88rem;
                line-height: 1.45;
                word-break: break-word;
            }

            .chat-time {
                font-size: 0.68rem;
                color: #94a3b8;
                margin-top: 3px;
                padding: 0 4px;
            }

            .chat-msg.customer .chat-time {
                text-align: right;
            }

            /* Message System (Center) */
            .chat-msg.system {
                align-self: center;
                text-align: center;
                max-width: 90%;
            }

            .chat-msg.system .chat-bubble {
                background: #f1f5f9;
                color: #64748b;
                font-size: 0.78rem;
                padding: 6px 12px;
                border-radius: 20px;
                border: 1px solid #e2e8f0;
            }

            /* Footer: Input Bar */
            .chat-footer {
                padding: 12px;
                background: #ffffff;
                border-top: 1px solid #e2e8f0;
            }

            .chat-quick-replies {
                display: flex;
                gap: 6px;
                overflow-x: auto;
                padding-bottom: 8px;
                margin-bottom: 6px;
                scrollbar-width: none;
            }

            .chat-quick-replies::-webkit-scrollbar {
                display: none;
            }

            .quick-pill {
                white-space: nowrap;
                background: #f1f5f9;
                border: 1px solid #e2e8f0;
                color: #334155;
                font-size: 0.75rem;
                padding: 4px 10px;
                border-radius: 14px;
                cursor: pointer;
                transition: all 0.15s;
            }

            .quick-pill:hover {
                background: #e2e8f0;
                color: #0f172a;
            }

            .chat-input-form {
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .chat-input {
                flex: 1;
                border: 1px solid #cbd5e1;
                border-radius: 24px;
                padding: 9px 16px;
                font-size: 0.88rem;
                outline: none;
                transition: border-color 0.2s;
            }

            .chat-input:focus {
                border-color: #2563eb;
            }

            .chat-btn-send {
                width: 38px;
                height: 38px;
                border-radius: 50%;
                background: #2563eb;
                color: #ffffff;
                border: none;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: background 0.2s;
                flex-shrink: 0;
            }

            .chat-btn-send:hover {
                background: #1d4ed8;
            }

            .chat-btn-send:disabled {
                opacity: 0.5;
                cursor: not-allowed;
            }

            /* Closed state banner */
            .chat-closed-notice {
                padding: 8px 12px;
                background: #fff1f2;
                border-top: 1px solid #fecdd3;
                color: #e11d48;
                font-size: 0.8rem;
                text-align: center;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .btn-restart-chat {
                background: #e11d48;
                color: #fff;
                border: none;
                border-radius: 6px;
                padding: 4px 10px;
                font-size: 0.75rem;
                font-weight: 600;
                cursor: pointer;
            }
        `;
        document.head.appendChild(style);

        // Inject Widget HTML
        var widgetDiv = document.createElement('div');
        widgetDiv.id = 'techno-chat-widget';
        widgetDiv.innerHTML = `
            <!-- Floating Button -->
            <button class="chat-floating-btn" id="techno-chat-toggle-btn" title="Chat với tư vấn viên">
                <span class="chat-btn-icon"><i class="fas fa-headset"></i></span>
                <span>Chat hỗ trợ</span>
                <span class="chat-btn-badge" id="techno-chat-badge" style="display:none;">1</span>
            </button>

            <!-- Chat Window -->
            <div class="chat-window" id="techno-chat-window">
                <!-- Header -->
                <div class="chat-header">
                    <div class="chat-header-user">
                        <div class="chat-header-avatar"><i class="fas fa-headset"></i></div>
                        <div class="chat-header-info">
                            <h4>Hỗ trợ TechNo</h4>
                            <div class="chat-header-status">
                                <span class="chat-status-dot" id="techno-chat-status-dot"></span>
                                <span id="techno-chat-status-text">Trực tuyến</span>
                            </div>
                        </div>
                    </div>
                    <div class="chat-header-actions">
                        <button class="chat-btn-action" id="techno-chat-end-btn" title="Kết thúc phiên chat"><i class="fas fa-power-off"></i></button>
                        <button class="chat-btn-action" id="techno-chat-close-btn" title="Thu nhỏ"><i class="fas fa-xmark"></i></button>
                    </div>
                </div>

                <!-- Messages Body -->
                <div class="chat-body" id="techno-chat-messages">
                    <!-- Sẽ render qua JS -->
                </div>

                <!-- Closed Notice -->
                <div class="chat-closed-notice" id="techno-chat-closed-bar" style="display:none;">
                    <span>Phiên chat đã kết thúc</span>
                    <button class="btn-restart-chat" id="techno-chat-restart-btn">Bắt đầu lại</button>
                </div>

                <!-- Input Footer -->
                <div class="chat-footer" id="techno-chat-footer">
                    <div class="chat-quick-replies" id="techno-quick-replies">
                        <span class="quick-pill" onclick="window.technoChatSendQuick('Tôi cần tư vấn mua điện thoại')">📱 Tư vấn điện thoại</span>
                        <span class="quick-pill" onclick="window.technoChatSendQuick('Tôi muốn kiểm tra đơn hàng')">📦 Kiểm tra đơn hàng</span>
                        <span class="quick-pill" onclick="window.technoChatSendQuick('Chính sách bảo hành như thế nào?')">🛡️ Bảo hành</span>
                    </div>
                    <form class="chat-input-form" id="techno-chat-form">
                        <input type="text" class="chat-input" id="techno-chat-input" placeholder="Nhập tin nhắn..." autocomplete="off">
                        <button type="submit" class="chat-btn-send" id="techno-chat-send-btn" title="Gửi tin nhắn">
                            <i class="fas fa-paper-plane"></i>
                        </button>
                    </form>
                </div>
            </div>
        `;
        document.body.appendChild(widgetDiv);

        // Biến trạng thái chat
        var stompClient = null;
        var chatSessionId = null;
        var currentSub = null;
        var unreadCount = 0;
        var isChatOpen = false;
        var sessionStatus = 'NONE'; // NONE | WAITING | OPEN | CLOSED
        var customerName = localStorage.getItem('hoTen') || 'Khách hàng';
        var customerId = localStorage.getItem('idKH') ? parseInt(localStorage.getItem('idKH')) : null;

        var toggleBtn = document.getElementById('techno-chat-toggle-btn');
        var chatWindow = document.getElementById('techno-chat-window');
        var closeBtn = document.getElementById('techno-chat-close-btn');
        var endBtn = document.getElementById('techno-chat-end-btn');
        var restartBtn = document.getElementById('techno-chat-restart-btn');
        var messagesBox = document.getElementById('techno-chat-messages');
        var chatForm = document.getElementById('techno-chat-form');
        var chatInput = document.getElementById('techno-chat-input');
        var badge = document.getElementById('techno-chat-badge');
        var statusDot = document.getElementById('techno-chat-status-dot');
        var statusText = document.getElementById('techno-chat-status-text');
        var closedBar = document.getElementById('techno-chat-closed-bar');

        // Toggle mở / đóng chat
        toggleBtn.addEventListener('click', function() {
            if (isChatOpen) {
                closeChat();
            } else {
                openChat();
            }
        });

        closeBtn.addEventListener('click', function() {
            closeChat();
        });

        function openChat() {
            chatWindow.classList.add('open');
            isChatOpen = true;
            unreadCount = 0;
            badge.style.display = 'none';

            // Nếu chưa có session, kiểm tra session đã lưu trong sessionStorage
            var savedId = sessionStorage.getItem('techno_chat_session_id');
            if (savedId && !chatSessionId) {
                resumeSession(parseInt(savedId));
            } else if (!chatSessionId) {
                renderWelcomeScreen();
            } else {
                scrollToBottom();
            }
        }

        function closeChat() {
            chatWindow.classList.remove('open');
            isChatOpen = false;
        }

        // Bắt đầu phiên chat (Gọi REST POST /api/chat/sessions hoặc STOMP /app/chat)
        window.technoStartSession = function(customName) {
            if (customName && customName.trim()) {
                customerName = customName.trim();
            }

            statusText.textContent = 'Đang kết nối...';
            statusDot.style.background = '#f59e0b';

            var payload = {
                type: 'START_SESSION',
                idKH: customerId,
                tenKhach: customerName,
                tenNguoiGui: customerName,
                nguoiGui: 'CUSTOMER'
            };

            axios.post('http://localhost:8080/api/chat/sessions', payload)
                .then(function(res) {
                    var data = res.data?.data || res.data;
                    chatSessionId = data.idSession;
                    sessionStatus = data.trangThai || 'WAITING';
                    sessionStorage.setItem('techno_chat_session_id', chatSessionId);

                    connectWebSocket(function() {
                        subscribeChatSession(chatSessionId);
                    });

                    // Render tin nhắn sẵn có
                    renderMessageList(data.tinNhans || []);
                    updateStatusUI();
                })
                .catch(function(err) {
                    console.error('Lỗi tạo phiên chat:', err);
                    alert('Không thể kết nối đến máy chủ hỗ trợ. Vui lòng thử lại!');
                });
        };

        function resumeSession(id) {
            chatSessionId = id;
            axios.get('http://localhost:8080/api/chat/sessions/' + id)
                .then(function(res) {
                    var data = res.data?.data || res.data;
                    sessionStatus = data.trangThai;
                    connectWebSocket(function() {
                        subscribeChatSession(chatSessionId);
                    });
                    renderMessageList(data.tinNhans || []);
                    updateStatusUI();
                })
                .catch(function() {
                    sessionStorage.removeItem('techno_chat_session_id');
                    chatSessionId = null;
                    renderWelcomeScreen();
                });
        }

        // Kết nối WebSocket
        function connectWebSocket(onConnected) {
            if (stompClient && stompClient.connected) {
                if (onConnected) onConnected();
                return;
            }

            try {
                var socket = new SockJS('http://localhost:8080/ws');
                stompClient = new StompJs.Client({
                    webSocketFactory: function() { return socket; },
                    reconnectDelay: 5000,
                    onConnect: function() {
                        statusDot.style.background = '#10b981';
                        statusText.textContent = 'Trực tuyến';
                        if (onConnected) onConnected();
                    },
                    onDisconnect: function() {
                        statusDot.style.background = '#ef4444';
                        statusText.textContent = 'Mất kết nối';
                    },
                    onStompError: function() {
                        statusDot.style.background = '#ef4444';
                        statusText.textContent = 'Mất kết nối';
                    }
                });
                stompClient.activate();
            } catch (e) {
                console.error('WS Error:', e);
            }
        }

        // Subscribe /topic/chat/{sessionId}
        function subscribeChatSession(sessionId) {
            if (!stompClient || !stompClient.connected) return;
            if (currentSub) currentSub.unsubscribe();

            currentSub = stompClient.subscribe('/topic/chat/' + sessionId, function(msg) {
                var body = JSON.parse(msg.body);
                handleIncomingMessage(body);
            });
        }

        function handleIncomingMessage(msg) {
            // Cập nhật trạng thái nếu có
            if (msg.trangThai) {
                sessionStatus = msg.trangThai;
                updateStatusUI();
            }

            appendMessage(msg);

            // Nếu admin gửi tin và cửa sổ chat đang thu nhỏ
            if (msg.nguoiGui === 'ADMIN' && !isChatOpen) {
                unreadCount++;
                badge.textContent = unreadCount > 9 ? '9+' : unreadCount;
                badge.style.display = 'flex';
                playChimeSound();
            }
        }

        // Gửi tin nhắn
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            var text = chatInput.value.trim();
            if (!text || !chatSessionId) return;

            if (sessionStatus === 'CLOSED') {
                alert('Phiên chat này đã kết thúc. Vui lòng bấm "Bắt đầu lại" để tạo phiên mới.');
                return;
            }

            var payload = {
                type: 'MESSAGE',
                sessionId: chatSessionId,
                idKH: customerId,
                nguoiGui: 'CUSTOMER',
                tenNguoiGui: customerName,
                noiDung: text
            };

            // Ưu tiên gửi qua WebSocket /app/chat
            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: '/app/chat',
                    body: JSON.stringify(payload)
                });
            } else {
                // Fallback REST
                axios.post('http://localhost:8080/api/chat/sessions/' + chatSessionId + '/messages', payload);
            }

            chatInput.value = '';
            chatInput.focus();
        });

        // Gửi câu trả lời nhanh
        window.technoChatSendQuick = function(text) {
            if (!chatSessionId) {
                window.technoStartSession(customerName);
                setTimeout(function() {
                    chatInput.value = text;
                    chatForm.dispatchEvent(new Event('submit'));
                }, 600);
            } else {
                chatInput.value = text;
                chatForm.dispatchEvent(new Event('submit'));
            }
        };

        // Đóng phiên chat
        endBtn.addEventListener('click', function() {
            if (!chatSessionId) return;
            if (!confirm('Bạn có muốn kết thúc phiên hỗ trợ này?')) return;

            var payload = {
                type: 'CLOSE',
                sessionId: chatSessionId,
                tenNguoiGui: customerName
            };

            if (stompClient && stompClient.connected) {
                stompClient.publish({
                    destination: '/app/chat',
                    body: JSON.stringify(payload)
                });
            } else {
                axios.put('http://localhost:8080/api/chat/sessions/' + chatSessionId + '/close', payload);
            }
        });

        restartBtn.addEventListener('click', function() {
            sessionStorage.removeItem('techno_chat_session_id');
            chatSessionId = null;
            renderWelcomeScreen();
        });

        // UI Helpers
        function renderWelcomeScreen() {
            sessionStatus = 'NONE';
            updateStatusUI();
            messagesBox.innerHTML = `
                <div class="chat-welcome-card">
                    <i class="fas fa-comments"></i>
                    <h3>Chào mừng đến với TechNo!</h3>
                    <p>Đội ngũ tư vấn viên TechNo sẵn sàng giải đáp mọi thắc mắc của bạn về sản phẩm, đơn hàng và ưu đãi.</p>
                    <input type="text" class="chat-welcome-input" id="techno-welcome-name" value="${escapeHtml(customerName)}" placeholder="Nhập tên của bạn...">
                    <button class="btn-start-chat" onclick="window.technoStartSession(document.getElementById('techno-welcome-name').value)">
                        <i class="fas fa-paper-plane" style="font-size:0.85rem;margin-right:6px;"></i>
                        Bắt đầu chat với tư vấn viên
                    </button>
                </div>
            `;
        }

        function renderMessageList(messages) {
            messagesBox.innerHTML = '';
            if (messages.length === 0) {
                messagesBox.innerHTML = `
                    <div class="chat-msg system">
                        <div class="chat-bubble">Phiên chat đã được tạo. Vui lòng gửi tin nhắn để gặp tư vấn viên.</div>
                    </div>
                `;
            } else {
                messages.forEach(function(m) {
                    appendMessage(m, false);
                });
            }
            scrollToBottom();
        }

        function appendMessage(msg, doScroll) {
            var msgDiv = document.createElement('div');
            var role = (msg.nguoiGui || 'SYSTEM').toLowerCase();

            if (role === 'customer') {
                msgDiv.className = 'chat-msg customer';
                msgDiv.innerHTML = `
                    <div class="chat-bubble">${escapeHtml(msg.noiDung)}</div>
                    <div class="chat-time">${formatTime(msg.ngayGui || msg.timestamp)}</div>
                `;
            } else if (role === 'admin') {
                msgDiv.className = 'chat-msg admin';
                var sender = msg.tenNguoiGui || 'Quản trị viên';
                msgDiv.innerHTML = `
                    <div class="chat-sender-name"><i class="fas fa-headset" style="font-size:0.65rem;margin-right:3px;"></i>${escapeHtml(sender)}</div>
                    <div class="chat-bubble">${escapeHtml(msg.noiDung)}</div>
                    <div class="chat-time">${formatTime(msg.ngayGui || msg.timestamp)}</div>
                `;
            } else {
                msgDiv.className = 'chat-msg system';
                msgDiv.innerHTML = `
                    <div class="chat-bubble">${escapeHtml(msg.noiDung)}</div>
                `;
            }

            messagesBox.appendChild(msgDiv);
            if (doScroll !== false) {
                scrollToBottom();
            }
        }

        function updateStatusUI() {
            if (sessionStatus === 'CLOSED') {
                closedBar.style.display = 'flex';
                document.getElementById('techno-quick-replies').style.display = 'none';
                endBtn.style.display = 'none';
                statusDot.style.background = '#94a3b8';
                statusText.textContent = 'Đã kết thúc';
            } else {
                closedBar.style.display = 'none';
                document.getElementById('techno-quick-replies').style.display = 'flex';
                endBtn.style.display = 'flex';
                if (sessionStatus === 'WAITING') {
                    statusDot.style.background = '#f59e0b';
                    statusText.textContent = 'Chờ tư vấn viên...';
                } else if (sessionStatus === 'OPEN') {
                    statusDot.style.background = '#10b981';
                    statusText.textContent = 'Đang hỗ trợ';
                }
            }
        }

        function scrollToBottom() {
            setTimeout(function() {
                messagesBox.scrollTop = messagesBox.scrollHeight;
            }, 60);
        }

        function formatTime(d) {
            if (!d) return '';
            var dt = new Date(d);
            if (isNaN(dt.getTime())) return '';
            return ('0' + dt.getHours()).slice(-2) + ':' + ('0' + dt.getMinutes()).slice(-2);
        }

        function escapeHtml(str) {
            if (!str) return '';
            return str.replace(/&/g, '&amp;')
                      .replace(/</g, '&lt;')
                      .replace(/>/g, '&gt;')
                      .replace(/"/g, '&quot;')
                      .replace(/'/g, '&#039;');
        }

        function playChimeSound() {
            try {
                var ctx = new (window.AudioContext || window.webkitAudioContext)();
                var osc = ctx.createOscillator();
                var gain = ctx.createGain();
                osc.connect(gain);
                gain.connect(ctx.destination);
                osc.type = 'sine';
                osc.frequency.setValueAtTime(587.33, ctx.currentTime);
                osc.frequency.setValueAtTime(880, ctx.currentTime + 0.1);
                gain.gain.setValueAtTime(0.2, ctx.currentTime);
                gain.gain.linearRampToValueAtTime(0, ctx.currentTime + 0.3);
                osc.start(ctx.currentTime);
                osc.stop(ctx.currentTime + 0.3);
            } catch (_) {}
        }
    }
})();

