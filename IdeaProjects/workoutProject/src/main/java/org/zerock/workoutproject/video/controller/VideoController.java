package org.zerock.workoutproject.video.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.workoutproject.video.dto.RoomInfo;
import org.zerock.workoutproject.video.service.VideoService;

import java.util.List;

@Controller
@RequestMapping("/video-rooms")
@Log4j2
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createRoomForm() {
        log.info("GET /video-rooms/create - Showing create room form (Admin only)");
        return "video/roomCreate";
    }

    /**
     * 방 생성 요청을 처리하는 POST 핸들러 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createRoomPost(@RequestParam String roomName, @RequestParam String roomPassword) {
        log.info("POST /video-rooms/create - Attempting to create room: name={}", roomName);
        try {
            // 입력값 유효성 검사
            if (roomName == null || roomName.trim().isEmpty()) {
                log.warn("Room name is empty or null");
                return "redirect:/video-rooms/create?error=invalid_name";
            }
            if (roomPassword == null || roomPassword.trim().isEmpty()) {
                log.warn("Room password is empty or null");
                return "redirect:/video-rooms/create?error=invalid_password";
            }
            
            // 서비스 호출하여 방 생성 (비밀번호는 서비스에서 암호화)
            RoomInfo createdRoom = videoService.createRoom(roomName, roomPassword);
            log.info("Room created successfully: id={}, name={}", createdRoom.getRoomId(), createdRoom.getRoomName());
            return "redirect:/video-rooms/list"; // 방 목록 페이지로 리다이렉션
        } catch (Exception e) {
            log.error("Error creating room", e);
            return "redirect:/video-rooms/create?error=creation_failed";
        }
    }

    @GetMapping("/list")
    public String listRooms(Model model) {
        log.info("GET /video-rooms/list - Listing available rooms");
        try {
            List<RoomInfo> rooms = videoService.getAllRooms();
            log.info("Retrieved {} rooms from service", rooms.size());
            
            // 디버깅: 방 목록 로그 출력
            if (rooms.isEmpty()) {
                log.warn("Room list is empty");
            } else {
                for (RoomInfo room : rooms) {
                    log.info("Room in list: id={}, name={}", room.getRoomId(), room.getRoomName());
                }
            }
            
            model.addAttribute("rooms", rooms);
            return "video/roomList";
        } catch (Exception e) {
            log.error("Error retrieving room list", e);
            model.addAttribute("errorMessage", "방 목록을 불러오는 중 오류가 발생했습니다.");
            return "video/roomList";
        }
    }

    @GetMapping("/room/{roomId}")
    public String room(@PathVariable String roomId, Model model) {
        log.info("GET /video-rooms/room/{} - Entering room", roomId);
        RoomInfo room = videoService.getRoomById(roomId);
        
        if (room == null) {
            log.warn("Room with ID {} not found", roomId);
            return "redirect:/video-rooms/list";
        }
        
        // Spring Security를 통해 현재 사용자가 ROLE_ADMIN 권한을 가지고 있는지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", room.getRoomName());
        model.addAttribute("isTrainer", isAdmin); // 관리자만 트레이너로 설정
        return "video/room";
    }

    @GetMapping("/roomPassword")
    public String roomPasswordForm(@RequestParam String roomId, Model model) {
        log.info("GET /video-rooms/roomPassword - Showing password form for room {}", roomId);
        RoomInfo room = videoService.getRoomById(roomId);
        
        if (room == null) {
            log.warn("Room not found, redirecting to list");
            return "redirect:/video-rooms/list";
        }
        
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", room.getRoomName());
        return "video/roomPassword";
    }
    

    @PostMapping("/verify-password")
    public String verifyPassword(@RequestParam String roomId, 
                                 @RequestParam String password, 
                                 RedirectAttributes redirectAttrs) {
        log.info("POST /video-rooms/verify-password - Verifying password for room {}", roomId);
        
        if (videoService.verifyRoomPassword(roomId, password)) {
            log.info("Password verification successful for room {}", roomId);
            return "redirect:/video-rooms/room/" + roomId;
        } else {
            log.warn("Password verification failed for room {}", roomId);
            redirectAttrs.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/video-rooms/roomPassword?roomId=" + roomId;
        }
    }
    

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteRoom(@RequestParam String roomId) {
        log.info("POST /video-rooms/delete - Deleting room {}", roomId);
        try {
            videoService.deleteRoom(roomId);
            log.info("Room deleted successfully: {}", roomId);
            return "redirect:/video-rooms/list";
        } catch (Exception e) {
            log.error("Error deleting room: {}", roomId, e);
            return "redirect:/video-rooms/list?error=deletion_failed";
        }
    }
} 