// File: order-admin.js
// Quản lý đơn hàng trong trang admin

document.addEventListener('DOMContentLoaded', function() {
    // Kiểm tra trước nếu đang ở trang quản lý đơn hàng
    if (document.querySelector('.order-table')) {
        // Kiểm tra kết nối backend trước
        checkBackendConnection();
        
        loadOrders();
        loadOrderStatistics();
        displaySuccessfulOrders();
    }
    
    // Thêm event listener cho các nút hành động trong bảng
    document.addEventListener('click', function(event) {
        // Xử lý nút hủy đơn hàng
        if (event.target.classList.contains('cancel')) {
            const orderId = event.target.getAttribute('data-id');
            if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
                updateOrderStatus(orderId, 5); // 5 = Đã hủy
            }
        }
        
        // Xử lý thay đổi trạng thái đơn hàng trong trang chi tiết
        if (event.target.classList.contains('status-dropdown')) {
            const orderId = event.target.getAttribute('data-id');
            const newStatusId = event.target.value;
            updateOrderStatus(orderId, newStatusId);
        }
    });
});

// Hàm kiểm tra kết nối backend
async function checkBackendConnection() {
    const statusElement = document.getElementById('connection-status');
    
    if (!statusElement) return;
    
    // Hiển thị đang kiểm tra
    statusElement.innerHTML = '<i class="fas fa-circle" style="color: #ffc107; font-size: 0.5em;"></i> Đang kiểm tra...';
    statusElement.style.background = '#fff3cd';
    statusElement.style.color = '#856404';
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000); // Timeout 5s
        
        const response = await fetch('http://localhost:8080/api/checkout/admin/orders', {
            method: 'HEAD', // Chỉ kiểm tra header, không tải dữ liệu
            headers: {
                'Content-Type': 'application/json',
            },
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        if (response.ok || response.status === 200) {
            statusElement.innerHTML = '<i class="fas fa-circle" style="color: #28a745; font-size: 0.5em;"></i> Kết nối thành công';
            statusElement.style.background = '#d4edda';
            statusElement.style.color = '#155724';
        } else {
            throw new Error(`HTTP ${response.status}`);
        }
        
    } catch (error) {
        let errorMsg = 'Không thể kết nối';
        
        if (error.name === 'AbortError') {
            errorMsg = 'Timeout - Backend không phản hồi';
        } else if (error.message.includes('fetch')) {
                errorMsg = 'Backend không chạy (localhost:8080)';
            } else {
            errorMsg = `Lỗi: ${error.message}`;
        }
        
        statusElement.innerHTML = `<i class="fas fa-circle" style="color: #dc3545; font-size: 0.5em;"></i> ${errorMsg}`;
        statusElement.style.background = '#f8d7da';
        statusElement.style.color = '#721c24';
        
        // Hiển thị hướng dẫn khắc phục
        if (!document.getElementById('connection-help')) {
            const helpDiv = document.createElement('div');
            helpDiv.id = 'connection-help';
            helpDiv.style.cssText = `
                margin-top: 10px;
                padding: 10px;
                background: #fff3cd;
                border: 1px solid #ffeaa7;
                border-radius: 4px;
                font-size: 0.8em;
                color: #856404;
            `;
            helpDiv.innerHTML = `
                <strong>💡 Hướng dẫn khắc phục:</strong><br>
                1. Đảm bảo Spring Boot backend đang chạy tại <code>localhost:8080</code><br>
                2. Kiểm tra CORS configuration trong backend<br>
                3. Mở Developer Tools (F12) → Console để xem log chi tiết<br>
                4. Thử truy cập trực tiếp: <a href="http://localhost:8080/api/checkout/admin/orders" target="_blank">http://localhost:8080/api/checkout/admin/orders</a>
            `;
            
            document.getElementById('debug-panel').appendChild(helpDiv);
            
            // Tự động xóa sau 15 giây
            setTimeout(() => {
                if (helpDiv.parentNode) {
                    helpDiv.remove();
                }
            }, 15000);
        }
    }
}

// Hàm tải danh sách đơn hàng từ API
async function loadOrders() {
    const orderTable = document.querySelector('.order-table tbody');
    
    try {
        // Xóa dữ liệu cũ và hiển thị loading
        orderTable.innerHTML = '<tr><td colspan="6" style="text-align: center;"><i class="fas fa-spinner fa-spin"></i> Đang tải dữ liệu...</td></tr>';
        
        // Kiểm tra kết nối mạng
        if (!navigator.onLine) {
            throw new Error('Không có kết nối mạng');
        }
        
        // Gọi API để lấy dữ liệu đơn hàng
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 10000); // Timeout 10s
        
        const response = await fetch('http://localhost:8080/api/checkout/admin/orders', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('API endpoint không tồn tại');
            } else if (response.status === 500) {
                throw new Error('Lỗi server nội bộ');
            } else if (response.status === 0 || !response.status) {
                throw new Error('Không thể kết nối đến server. Vui lòng kiểm tra xem backend có đang chạy không.');
            } else {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
        }
        
        const orders = await response.json();
        
        // Xóa dữ liệu loading
        orderTable.innerHTML = '';
        
        if (!Array.isArray(orders)) {
            throw new Error('Dữ liệu trả về không đúng định dạng');
        }
        
        if (orders.length === 0) {
            // Hiển thị thông báo không có đơn hàng
            orderTable.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; padding: 40px;">
                        <i class="fas fa-inbox" style="font-size: 3em; color: #ccc; margin-bottom: 10px;"></i>
                        <br>
                        <span style="color: #666; font-size: 1.1em;">Chưa có đơn hàng nào</span>
                        <br>
                        <small style="color: #999;">Đơn hàng sẽ xuất hiện ở đây khi khách hàng đặt hàng</small>
                    </td>
                </tr>
            `;
            return;
        }
        
        // Hiển thị đơn hàng vào bảng
        orders.forEach((order, index) => {
            try {
                const statusClass = getStatusClass(order.trangThai?.tenTrangThai || 'Không xác định');
                
                const orderRow = `
                    <tr style="animation: fadeInUp 0.3s ease-out ${index * 0.1}s both;">
                        <td><strong>DH${order.idDonHang ? order.idDonHang.toString().padStart(3, '0') : '000'}</strong></td>
                        <td style="max-width: 250px; word-wrap: break-word;">
                            ${order.thongTinKhachHang || 'Thông tin không có'}
                        </td>
                        <td>${formatDate(order.ngayDat)}</td>
                        <td>
                            <span class="status ${statusClass}">
                                ${order.trangThai?.tenTrangThai || 'Không xác định'}
                            </span>
                        </td>
                        <td style="font-weight: bold; color: #2e7d32;">
                            ${formatCurrency(order.tongThanhToan)}
                        </td>
                        <td class="actions">
                            <a href="AdDH-detail.html?id=${order.idDonHang}" style="text-decoration: none;">
                                <button class="btn detail" data-id="${order.idDonHang}">
                                    <i class="fas fa-eye"></i> Chi tiết
                                </button>
                            </a>
                            ${(order.trangThai?.idTtdh !== 5 && order.trangThai?.idTtdh !== 4) ? 
                                `<button class="btn cancel" data-id="${order.idDonHang}">
                                    <i class="fas fa-times"></i> Hủy
                                </button>` : ''
                            }
                        </td>
                    </tr>
                `;
                
                orderTable.innerHTML += orderRow;
            } catch (rowError) {
                console.error('Lỗi khi hiển thị đơn hàng:', rowError, order);
            }
        });
        
    } catch (error) {
        console.error('Error loading orders:', error);
        
        let errorMessage = 'Có lỗi xảy ra khi tải dữ liệu';
        let suggestion = '';
        
        if (error.name === 'AbortError') {
            errorMessage = 'Kết nối bị timeout';
            suggestion = 'Vui lòng thử lại sau';
        } else if (error.message.includes('fetch')) {
            errorMessage = 'Không thể kết nối đến server';
            suggestion = 'Vui lòng kiểm tra:<br>• Backend có đang chạy tại localhost:8080<br>• Kết nối mạng của bạn';
        } else if (error.message.includes('CORS')) {
            errorMessage = 'Lỗi CORS';
            suggestion = 'Vui lòng kiểm tra cấu hình CORS trong backend';
        } else {
            errorMessage = error.message;
        }
        
        orderTable.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 40px; color: #dc3545;">
                    <i class="fas fa-exclamation-triangle" style="font-size: 3em; color: #dc3545; margin-bottom: 15px;"></i>
                    <br>
                    <strong style="font-size: 1.1em;">${errorMessage}</strong>
                    <br>
                    <div style="margin-top: 10px; font-size: 0.9em; color: #666;">
                        ${suggestion}
                    </div>
                    <br>
                    <button onclick="loadOrders(); loadOrderStatistics(); displaySuccessfulOrders();" 
                            class="btn" style="background-color: #007bff; color: white; margin-top: 15px;">
                        <i class="fas fa-redo"></i> Thử lại
                    </button>
                </td>
            </tr>
        `;
    }
}

// Hàm lấy đơn hàng từ API
async function getOrdersFromAPI() {
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 8000); // Timeout 8s
        
        const response = await fetch('http://localhost:8080/api/checkout/admin/orders', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        if (!response.ok) {
            if (response.status === 0 || !response.status) {
                throw new Error('Không thể kết nối đến server');
            }
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const orders = await response.json();
        return Array.isArray(orders) ? orders : [];
    } catch (error) {
        console.error('Error fetching orders:', error);
        return [];
    }
}

// Cập nhật trạng thái đơn hàng qua API
async function updateOrderStatus(orderId, newStatusId) {
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 8000);
        
        const response = await fetch(
            `http://localhost:8080/api/checkout/admin/orders/${orderId}/status?trangThaiId=${newStatusId}`, 
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                signal: controller.signal
            }
        );
        
        clearTimeout(timeoutId);
        
        if (!response.ok) {
            if (response.status === 0 || !response.status) {
                throw new Error('Không thể kết nối đến server');
            }
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        // Cập nhật lại giao diện
        await Promise.all([
            loadOrders(),
            loadOrderStatistics(),
            displaySuccessfulOrders()
        ]);
        
        // Hiển thị thông báo thành công
        const statusNames = {
            1: 'Chờ xử lý',
            2: 'Đã xác nhận', 
            3: 'Đang giao',
            4: 'Hoàn tất',
            5: 'Đã hủy'
        };
        
        // Tạo toast notification thay vì alert
        showToast(`Đã cập nhật trạng thái đơn hàng ${orderId} thành "${statusNames[newStatusId]}"`, 'success');
        
    } catch (error) {
        console.error('Error updating order status:', error);
        
        let errorMessage = 'Lỗi khi cập nhật trạng thái đơn hàng';
        if (error.name === 'AbortError') {
            errorMessage = 'Kết nối bị timeout khi cập nhật trạng thái';
        } else if (error.message.includes('server')) {
            errorMessage = 'Không thể kết nối đến server để cập nhật trạng thái';
        }
        
        showToast(errorMessage, 'error');
    }
}

// Hàm tải thống kê đơn hàng
async function loadOrderStatistics() {
    try {
        const orders = await getOrdersFromAPI();
        
        // Đếm theo trạng thái
        const stats = {
            total: orders.length,
            success: orders.filter(order => order.trangThai.idTtdh === 4).length, // 4 = Hoàn tất
            pending: orders.filter(order => [1, 2, 3].includes(order.trangThai.idTtdh)).length, // 1,2,3 = Chờ xử lý, Đã xác nhận, Đang giao
            canceled: orders.filter(order => order.trangThai.idTtdh === 5).length // 5 = Đã hủy
        };
        
        // Cập nhật giao diện
        document.getElementById('total-orders').textContent = stats.total;
        document.getElementById('success-orders').textContent = stats.success;
        document.getElementById('pending-orders').textContent = stats.pending;
        document.getElementById('canceled-orders').textContent = stats.canceled;
        
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Hàm hiển thị thông tin chi tiết đơn hàng thành công
async function displaySuccessfulOrders() {
    try {
        const orders = await getOrdersFromAPI();
        const successfulOrders = orders.filter(order => order.trangThai.idTtdh === 4); // 4 = Hoàn tất
        const successfulOrdersList = document.getElementById('successful-orders-list');
        
        if (successfulOrders.length === 0) {
            successfulOrdersList.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #666; background: #f8f9fa; border-radius: 8px;">
                    <i class="fas fa-shopping-cart" style="font-size: 3em; margin-bottom: 15px; opacity: 0.3;"></i>
                    <p style="font-size: 1.1em;">Chưa có đơn hàng nào hoàn tất</p>
                </div>
            `;
            return;
        }
        
        successfulOrdersList.innerHTML = '';
        
        successfulOrders.forEach(order => {
            const orderCard = document.createElement('div');
            orderCard.className = 'order-success-card';
            orderCard.style.cssText = `
                background: white;
                border: 2px solid #4caf50;
                border-radius: 12px;
                padding: 25px;
                box-shadow: 0 4px 12px rgba(76, 175, 80, 0.1);
                transition: transform 0.2s ease;
            `;
            
            // Tách thông tin khách hàng
            const customerInfo = parseCustomerInfo(order.thongTinKhachHang);
            const addressInfo = parseAddressInfo(order.ghiChuKhachHang);
            
            orderCard.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px;">
                    <div>
                        <h4 style="margin: 0; color: #2e7d32; font-size: 1.2em;">
                            <i class="fas fa-check-circle" style="color: #4caf50; margin-right: 8px;"></i>
                            Đơn hàng #DH${order.idDonHang.toString().padStart(3, '0')}
                        </h4>
                        <p style="margin: 5px 0; color: #666; font-size: 0.9em;">
                            <i class="far fa-calendar-alt" style="margin-right: 5px;"></i>
                            ${formatDate(order.ngayDat)}
                        </p>
                    </div>
                    <span class="status completed" style="background-color: #d4edda; color: #155724; padding: 6px 12px; border-radius: 20px; font-size: 0.85em; font-weight: 500;">
                        ${order.trangThai.tenTrangThai}
                    </span>
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px;">
                    <div class="customer-info">
                        <h5 style="margin: 0 0 10px 0; color: #333; border-bottom: 2px solid #e0e0e0; padding-bottom: 5px;">
                            <i class="fas fa-user" style="margin-right: 8px; color: #4caf50;"></i>
                            Thông tin khách hàng
                        </h5>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Tên:</strong> ${customerInfo.name}
                        </p>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>SĐT:</strong> ${customerInfo.phone}
                        </p>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Email:</strong> ${customerInfo.email}
                        </p>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Địa chỉ:</strong> ${addressInfo}
                        </p>
                    </div>
                    
                    <div class="order-summary">
                        <h5 style="margin: 0 0 10px 0; color: #333; border-bottom: 2px solid #e0e0e0; padding-bottom: 5px;">
                            <i class="fas fa-receipt" style="margin-right: 8px; color: #4caf50;"></i>
                            Tóm tắt đơn hàng
                        </h5>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Tổng sản phẩm:</strong> ${formatCurrency(order.tongTienSanPham)}
                        </p>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Phí vận chuyển:</strong> ${formatCurrency(order.phiVanChuyen)}
                        </p>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Giảm giá:</strong> ${formatCurrency(order.tongTienGiamGia)}
                        </p>
                        <p style="margin: 8px 0; color: #555;">
                            <strong>Phương thức thanh toán:</strong> ${order.phuongThucThanhToan?.tenPhuongThuc || 'N/A'}
                        </p>
                        <p style="margin: 8px 0; color: #2e7d32; font-weight: bold; font-size: 1.1em;">
                            <strong>Tổng tiền:</strong> ${formatCurrency(order.tongThanhToan)}
                        </p>
                    </div>
                </div>
                
                <div style="margin-top: 15px;">
                    <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; border-left: 4px solid #4caf50;">
                        <h6 style="margin: 0 0 8px 0; color: #2e7d32;">
                            <i class="fas fa-sticky-note" style="margin-right: 8px;"></i>
                            Ghi chú đơn hàng
                        </h6>
                        <p style="margin: 0; color: #555; font-size: 0.9em; line-height: 1.4;">
                            ${order.ghiChuKhachHang || 'Không có ghi chú'}
                        </p>
                    </div>
                </div>
                
                <div style="margin-top: 15px; text-align: right;">
                    <button class="btn detail" style="background-color: #4caf50; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-size: 0.9em;" onclick="window.open('AdDH-detail.html?id=${order.idDonHang}', '_blank')">
                        <i class="fas fa-eye" style="margin-right: 5px;"></i>
                        Xem chi tiết
                    </button>
                </div>
            `;
            
            // Thêm hiệu ứng hover
            orderCard.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-3px)';
            });
            
            orderCard.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
            
            successfulOrdersList.appendChild(orderCard);
        });
        
    } catch (error) {
        console.error('Error displaying successful orders:', error);
        const successfulOrdersList = document.getElementById('successful-orders-list');
        successfulOrdersList.innerHTML = `
            <div style="text-align: center; padding: 40px; color: red; background: #f8f9fa; border-radius: 8px;">
                <p>Lỗi khi tải dữ liệu đơn hàng thành công: ${error.message}</p>
            </div>
        `;
    }
}

// Utility functions
function parseCustomerInfo(thongTinKhachHang) {
    // Format: "Tên Khách Hàng, SĐT: 0123456789, Email: email@example.com"
    const parts = thongTinKhachHang.split(', ');
    return {
        name: parts[0] || 'N/A',
        phone: parts[1] ? parts[1].replace('SĐT: ', '') : 'N/A',
        email: parts[2] ? parts[2].replace('Email: ', '') : 'N/A'
    };
}

function parseAddressInfo(ghiChuKhachHang) {
    // Format: "Địa chỉ: xxx, yyy, zzz, ttt. Ghi chú: ..."
    if (!ghiChuKhachHang) return 'N/A';
    const addressMatch = ghiChuKhachHang.match(/Địa chỉ:\s*([^.]+)/);
    return addressMatch ? addressMatch[1] : 'N/A';
}

function extractPhoneEmail(thongTinKhachHang) {
    const info = parseCustomerInfo(thongTinKhachHang);
    return `${info.phone}<br>${info.email}`;
}

// Định dạng tiền tệ
function formatCurrency(amount) {
    if (!amount) return '0₫';
    return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND',
        maximumFractionDigits: 0
    }).format(amount).replace('₫', 'đ');
}

// Định dạng ngày tháng
function formatDate(dateString) {
    const date = new Date(dateString);
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
}

// Lấy class CSS cho trạng thái đơn hàng
function getStatusClass(statusName) {
    switch (statusName) {
        case 'Chờ xử lý':
            return 'pending';
        case 'Đã xác nhận':
            return 'confirmed';
        case 'Đang giao':
            return 'shipping';
        case 'Hoàn tất':
            return 'completed';
        case 'Đã hủy':
            return 'canceled';
        default:
            return 'pending';
    }
}

// Hàm tải chi tiết đơn hàng trong trang chi tiết
async function loadOrderDetail() {
    // Lấy id đơn hàng từ URL
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('id');
    
    if (!orderId) {
        alert('Không tìm thấy thông tin đơn hàng');
        window.location.href = 'AdDH.html';
        return;
    }
    
    try {
        // Gọi API để lấy chi tiết đơn hàng
        const response = await fetch(`http://localhost:8080/api/checkout/order/${orderId}`);
        if (!response.ok) {
            throw new Error('Không thể tải chi tiết đơn hàng');
        }
        
        const order = await response.json();
        
        // Tách thông tin khách hàng và địa chỉ
        const customerInfo = parseCustomerInfo(order.thongTinKhachHang);
        const addressInfo = parseAddressInfo(order.ghiChuKhachHang);
        
        // Hiển thị thông tin khách hàng
        const customerSection = document.querySelector('.order-section:nth-of-type(1)');
        if (customerSection) {
            customerSection.innerHTML = `
                <h3>Thông tin khách hàng</h3>
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px;">
                    <p><strong>Thông tin:</strong> ${order.thongTinKhachHang}</p>
                    <p><strong>Địa chỉ giao hàng:</strong> ${addressInfo}</p>
                    <p><strong>Ghi chú:</strong> ${order.ghiChuKhachHang || 'Không có ghi chú'}</p>
                </div>
            `;
        }
        
        // Hiển thị thông tin thanh toán
        const paymentSection = document.querySelector('.order-section:nth-of-type(3)');
        if (paymentSection) {
            paymentSection.innerHTML = `
                <h3>Thông tin thanh toán</h3>
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px;">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                        <div>
                            <p><strong>Tổng tiền sản phẩm:</strong> ${formatCurrency(order.tongTienSanPham)}</p>
                            <p><strong>Phí vận chuyển:</strong> ${formatCurrency(order.phiVanChuyen)}</p>
                        </div>
                        <div>
                            <p><strong>Tổng giảm giá:</strong> ${formatCurrency(order.tongTienGiamGia)}</p>
                            <p><strong>Phương thức thanh toán:</strong> ${order.phuongThucThanhToan?.tenPhuongThuc || 'N/A'}</p>
                        </div>
                    </div>
                    <hr style="margin: 15px 0; border: 1px solid #dee2e6;">
                    <p style="font-size: 1.2em; font-weight: bold; color: #2e7d32; text-align: center;">
                        <strong>Tổng thanh toán: ${formatCurrency(order.tongThanhToan)}</strong>
                    </p>
                </div>
            `;
        }
        
        // Cập nhật trạng thái đơn hàng
        const statusDropdown = document.querySelector('.status-dropdown');
        if (statusDropdown) {
            statusDropdown.value = order.trangThai.idTtdh;
            statusDropdown.setAttribute('data-id', orderId);
        }
        
    } catch (error) {
        console.error('Error loading order detail:', error);
        alert('Lỗi khi tải chi tiết đơn hàng: ' + error.message);
        window.location.href = 'AdDH.html';
    }
}

// Hàm hiển thị toast notification
function showToast(message, type = 'info') {
    // Xóa toast cũ nếu có
    const existingToast = document.querySelector('.toast-notification');
    if (existingToast) {
        existingToast.remove();
    }
    
    // Tạo toast mới
    const toast = document.createElement('div');
    toast.className = `toast-notification toast-${type}`;
    toast.innerHTML = `
        <div style="display: flex; align-items: center; gap: 10px;">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle'}"></i>
            <span>${message}</span>
        </div>
    `;
    
    // Thêm CSS cho toast
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 6px;
        color: white;
        font-weight: 500;
        z-index: 9999;
        max-width: 400px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        transform: translateX(400px);
        transition: transform 0.3s ease;
        ${type === 'success' ? 'background: linear-gradient(135deg, #28a745, #20c997);' : ''}
        ${type === 'error' ? 'background: linear-gradient(135deg, #dc3545, #e83e8c);' : ''}
        ${type === 'info' ? 'background: linear-gradient(135deg, #17a2b8, #6f42c1);' : ''}
    `;
    
    document.body.appendChild(toast);
    
    // Animate in
    setTimeout(() => {
        toast.style.transform = 'translateX(0)';
    }, 100);
    
    // Auto remove after 4 seconds
    setTimeout(() => {
        toast.style.transform = 'translateX(400px)';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.remove();
            }
        }, 300);
    }, 4000);
} 