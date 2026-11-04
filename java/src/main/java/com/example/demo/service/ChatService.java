package com.example.demo.service;

import com.example.demo.dto.ChatMessageDTO;
import com.example.demo.dto.ChatRequestDTO;
import com.example.demo.dto.ChatSessionDTO;
import com.example.demo.entity.ChatSession;
import com.example.demo.entity.ChatTinNhan;
import com.example.demo.entity.KhachHang;
import com.example.demo.repository.ChatSessionRepository;
import com.example.demo.repository.ChatTinNhanRepository;
import com.example.demo.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Set<String> ACTIVE_STATUSES = Set.of("WAITING", "OPEN");

    private final ChatSessionRepository chatSessionRepository;
    private final ChatTinNhanRepository chatTinNhanRepository;
    private final KhachHangRepository khachHangRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public ChatSessionDTO startOrResumeSession(ChatRequestDTO request) {
        ChatSession existing = findExistingActiveSession(request);
        ChatSession session = existing != null ? existing : createSession(request);

        if (existing == null) {
            ChatTinNhan system = saveMessage(session, "SYSTEM", "Hệ thống",
                    "Phiên chat đã được tạo. Vui lòng chờ tư vấn viên.");
            ChatMessageDTO payload = toMessageDto(system, "SESSION_CREATED", session);
            webSocketService.notifyChatMessage(payload);
            webSocketService.notifyChatAdmin(payload);
        }
        return toSessionDto(session, true);
    }

    @Transactional
    public ChatMessageDTO sendMessage(ChatRequestDTO request) {
        if (request.getNoiDung() == null || request.getNoiDung().isBlank()) {
            throw new IllegalArgumentException("Nội dung tin nhắn không được để trống");
        }

        ChatSession session = resolveOrCreateSession(request);
        if ("CLOSED".equalsIgnoreCase(session.getTrangThai())) {
            throw new IllegalStateException("Phiên chat đã đóng");
        }

        String vaiTro = normalizeSender(request.getNguoiGui());
        String tenNguoiGui = request.getTenNguoiGui();
        if (tenNguoiGui == null || tenNguoiGui.isBlank()) {
            tenNguoiGui = "CUSTOMER".equals(vaiTro)
                    ? defaultCustomerName(session)
                    : (session.getAdminTenDangNhap() != null ? session.getAdminTenDangNhap() : "Tư vấn viên");
        }

        if ("ADMIN".equals(vaiTro) && "WAITING".equalsIgnoreCase(session.getTrangThai())) {
            session.setTrangThai("OPEN");
            if (request.getAdminTenDangNhap() != null && !request.getAdminTenDangNhap().isBlank()) {
                session.setAdminTenDangNhap(request.getAdminTenDangNhap());
            }
        }

        ChatTinNhan saved = saveMessage(session, vaiTro, tenNguoiGui, request.getNoiDung().trim());
        ChatMessageDTO payload = toMessageDto(saved, "MESSAGE", session);
        webSocketService.notifyChatMessage(payload);
        webSocketService.notifyChatAdmin(payload);
        return payload;
    }

    @Transactional
    public ChatMessageDTO joinSession(Integer sessionId, String adminTenDangNhap) {
        ChatSession session = getSessionOrThrow(sessionId);
        if ("CLOSED".equalsIgnoreCase(session.getTrangThai())) {
            throw new IllegalStateException("Phiên chat đã đóng");
        }

        session.setTrangThai("OPEN");
        if (adminTenDangNhap != null && !adminTenDangNhap.isBlank()) {
            session.setAdminTenDangNhap(adminTenDangNhap);
        }
        session.setNgayCapNhat(new Date());
        chatSessionRepository.save(session);

        String tenAdmin = session.getAdminTenDangNhap() != null ? session.getAdminTenDangNhap() : "Tư vấn viên";
        ChatTinNhan system = saveMessage(session, "SYSTEM", "Hệ thống",
                tenAdmin + " đã tham gia hỗ trợ.");

        ChatMessageDTO payload = toMessageDto(system, "JOINED", session);
        webSocketService.notifyChatMessage(payload);
        webSocketService.notifyChatAdmin(payload);
        return payload;
    }

    @Transactional
    public ChatMessageDTO closeSession(Integer sessionId, String nguoiDong) {
        ChatSession session = getSessionOrThrow(sessionId);
        session.setTrangThai("CLOSED");
        session.setNgayCapNhat(new Date());
        chatSessionRepository.save(session);

        String ten = (nguoiDong != null && !nguoiDong.isBlank()) ? nguoiDong : "Hệ thống";
        ChatTinNhan system = saveMessage(session, "SYSTEM", "Hệ thống",
                "Phiên chat đã được đóng bởi " + ten + ".");

        ChatMessageDTO payload = toMessageDto(system, "CLOSED", session);
        webSocketService.notifyChatMessage(payload);
        webSocketService.notifyChatAdmin(payload);
        return payload;
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDTO> listSessions(String trangThai) {
        List<ChatSession> sessions = (trangThai == null || trangThai.isBlank())
                ? chatSessionRepository.findAllByOrderByNgayCapNhatDesc()
                : chatSessionRepository.findByTrangThaiOrderByNgayCapNhatDesc(trangThai.trim().toUpperCase(Locale.ROOT));
        return sessions.stream().map(s -> toSessionDto(s, false)).toList();
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDTO> listSessionsByCustomer(Integer idKH) {
        return chatSessionRepository.findByIdKHOrderByNgayCapNhatDesc(idKH)
                .stream()
                .map(s -> toSessionDto(s, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatSessionDTO getSessionDetail(Integer sessionId) {
        return toSessionDto(getSessionOrThrow(sessionId), true);
    }

    @Transactional
    public void markCustomerMessagesRead(Integer sessionId) {
        chatTinNhanRepository.markAsRead(sessionId, "CUSTOMER");
    }

    private ChatSession resolveOrCreateSession(ChatRequestDTO request) {
        ChatSession existing = findExistingActiveSession(request);
        return existing != null ? existing : createSession(request);
    }

    private ChatSession findExistingActiveSession(ChatRequestDTO request) {
        if (request.getSessionId() != null) {
            return getSessionOrThrow(request.getSessionId());
        }
        // Chỉ tìm theo idKH nếu idKH tồn tại trong DB
        if (request.getIdKH() != null && khachHangRepository.existsById(request.getIdKH())) {
            return chatSessionRepository
                    .findFirstByIdKHAndTrangThaiInOrderByNgayTaoDesc(request.getIdKH(), ACTIVE_STATUSES)
                    .orElse(null);
        }
        return null;
    }

    private ChatSession createSession(ChatRequestDTO request) {
        ChatSession session = new ChatSession();

        // Validate idKH — chỉ set nếu thực sự tồn tại trong DB
        if (request.getIdKH() != null) {
            boolean exists = khachHangRepository.existsById(request.getIdKH());
            if (exists) {
                session.setIdKH(request.getIdKH());
            } else {
                // idKH không tồn tại (ví dụ: tài khoản admin fake, hoặc dữ liệu cũ)
                // → chat như khách vãng lai, không set idKH
                System.out.println("⚠️ Chat: idKH=" + request.getIdKH() + " không tồn tại, tạo session vãng lai");
            }
        }

        session.setTenKhach(resolveCustomerName(request));
        session.setTrangThai("WAITING");
        session.setNgayTao(new Date());
        session.setNgayCapNhat(new Date());
        return chatSessionRepository.save(session);
    }

    private ChatTinNhan saveMessage(ChatSession session, String nguoiGui, String tenNguoiGui, String noiDung) {
        ChatTinNhan tinNhan = new ChatTinNhan();
        tinNhan.setChatSession(session);
        tinNhan.setNguoiGui(nguoiGui);
        tinNhan.setTenNguoiGui(tenNguoiGui);
        tinNhan.setNoiDung(noiDung);
        tinNhan.setNgayGui(new Date());
        tinNhan.setDaDoc(false);

        session.setNgayCapNhat(new Date());
        chatSessionRepository.save(session);
        return chatTinNhanRepository.save(tinNhan);
    }

    private ChatSession getSessionOrThrow(Integer sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên chat #" + sessionId));
    }

    private String resolveCustomerName(ChatRequestDTO request) {
        if (request.getTenKhach() != null && !request.getTenKhach().isBlank()) {
            return request.getTenKhach().trim();
        }
        if (request.getTenNguoiGui() != null && !request.getTenNguoiGui().isBlank()
                && !"ADMIN".equalsIgnoreCase(request.getNguoiGui())) {
            return request.getTenNguoiGui().trim();
        }
        if (request.getIdKH() != null) {
            return khachHangRepository.findById(request.getIdKH())
                    .map(KhachHang::getHoTen)
                    .orElse("Khách hàng #" + request.getIdKH());
        }
        return "Khách vãng lai";
    }

    private String defaultCustomerName(ChatSession session) {
        if (session.getTenKhach() != null && !session.getTenKhach().isBlank()) {
            return session.getTenKhach();
        }
        if (session.getKhachHang() != null && session.getKhachHang().getHoTen() != null) {
            return session.getKhachHang().getHoTen();
        }
        return "Khách hàng";
    }

    private String normalizeSender(String nguoiGui) {
        if (nguoiGui == null || nguoiGui.isBlank()) {
            return "CUSTOMER";
        }
        String value = nguoiGui.trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(value) || "CUSTOMER".equals(value) || "SYSTEM".equals(value)) {
            return value;
        }
        throw new IllegalArgumentException("nguoiGui không hợp lệ: " + nguoiGui);
    }

    private ChatSessionDTO toSessionDto(ChatSession session, boolean includeMessages) {
        List<ChatTinNhan> messages = chatTinNhanRepository
                .findByChatSession_IdSessionOrderByNgayGuiAsc(session.getIdSession());

        String lastContent = messages.isEmpty() ? null : messages.get(messages.size() - 1).getNoiDung();
        long unread = chatTinNhanRepository
                .countByChatSession_IdSessionAndNguoiGuiAndDaDocFalse(session.getIdSession(), "CUSTOMER");

        ChatSessionDTO.ChatSessionDTOBuilder builder = ChatSessionDTO.builder()
                .idSession(session.getIdSession())
                .idKH(session.getIdKH())
                .tenKhach(session.getTenKhach() != null ? session.getTenKhach() : defaultCustomerName(session))
                .adminTenDangNhap(session.getAdminTenDangNhap())
                .trangThai(session.getTrangThai())
                .ngayTao(session.getNgayTao())
                .ngayCapNhat(session.getNgayCapNhat())
                .soTinChuaDoc(unread)
                .tinNhanCuoi(lastContent);

        if (includeMessages) {
            builder.tinNhans(messages.stream()
                    .map(m -> toMessageDto(m, "MESSAGE", session))
                    .toList());
        }
        return builder.build();
    }

    private ChatMessageDTO toMessageDto(ChatTinNhan tinNhan, String type, ChatSession session) {
        return ChatMessageDTO.builder()
                .type(type)
                .idTinNhan(tinNhan.getIdTinNhan())
                .sessionId(session.getIdSession())
                .idKH(session.getIdKH())
                .tenKhach(session.getTenKhach() != null ? session.getTenKhach() : defaultCustomerName(session))
                .adminTenDangNhap(session.getAdminTenDangNhap())
                .trangThai(session.getTrangThai())
                .nguoiGui(tinNhan.getNguoiGui())
                .tenNguoiGui(tinNhan.getTenNguoiGui())
                .noiDung(tinNhan.getNoiDung())
                .ngayGui(tinNhan.getNgayGui())
                .daDoc(tinNhan.getDaDoc())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
