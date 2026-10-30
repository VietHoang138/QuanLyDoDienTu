package com.example.demo.controller;

import com.example.demo.dto.ChatRequestDTO;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    /** Khách bấm "Chat với tư vấn viên" (REST, song song với WS START_SESSION). */
    @PostMapping("/sessions")
    public ResponseEntity<ResponseDto<?>> startSession(@RequestBody ChatRequestDTO request) {
        try {
            return ResponseEntity.ok(ResponseDto.success("Đã tạo phiên chat",
                    chatService.startOrResumeSession(request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    /** Admin: danh sách hội thoại. ?trangThai=WAITING|OPEN|CLOSED */
    @GetMapping("/sessions")
    public ResponseEntity<ResponseDto<?>> listSessions(
            @RequestParam(required = false) String trangThai) {
        return ResponseEntity.ok(ResponseDto.success("OK", chatService.listSessions(trangThai)));
    }

    /** Lịch sử 1 phiên (tra cứu sau). */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ResponseDto<?>> getSession(@PathVariable Integer sessionId) {
        try {
            return ResponseEntity.ok(ResponseDto.success("OK", chatService.getSessionDetail(sessionId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/khach-hang/{idKH}/sessions")
    public ResponseEntity<ResponseDto<?>> listByCustomer(@PathVariable Integer idKH) {
        return ResponseEntity.ok(ResponseDto.success("OK", chatService.listSessionsByCustomer(idKH)));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ResponseDto<?>> sendMessage(
            @PathVariable Integer sessionId,
            @RequestBody ChatRequestDTO request) {
        try {
            request.setSessionId(sessionId);
            if (request.getType() == null) {
                request.setType("MESSAGE");
            }
            return ResponseEntity.ok(ResponseDto.success("Đã gửi", chatService.sendMessage(request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PostMapping("/sessions/{sessionId}/join")
    public ResponseEntity<ResponseDto<?>> join(
            @PathVariable Integer sessionId,
            @RequestBody(required = false) ChatRequestDTO request) {
        try {
            String admin = request != null ? request.getAdminTenDangNhap() : null;
            return ResponseEntity.ok(ResponseDto.success("Đã tham gia",
                    chatService.joinSession(sessionId, admin)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PutMapping("/sessions/{sessionId}/close")
    public ResponseEntity<ResponseDto<?>> close(
            @PathVariable Integer sessionId,
            @RequestBody(required = false) ChatRequestDTO request) {
        try {
            String nguoiDong = null;
            if (request != null) {
                nguoiDong = request.getTenNguoiGui() != null
                        ? request.getTenNguoiGui()
                        : request.getAdminTenDangNhap();
            }
            return ResponseEntity.ok(ResponseDto.success("Đã đóng phiên chat",
                    chatService.closeSession(sessionId, nguoiDong)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error(e.getMessage()));
        }
    }

    @PutMapping("/sessions/{sessionId}/read")
    public ResponseEntity<ResponseDto<?>> markRead(@PathVariable Integer sessionId) {
        chatService.markCustomerMessagesRead(sessionId);
        return ResponseEntity.ok(ResponseDto.success("Đã đánh dấu đã đọc"));
    }
}
