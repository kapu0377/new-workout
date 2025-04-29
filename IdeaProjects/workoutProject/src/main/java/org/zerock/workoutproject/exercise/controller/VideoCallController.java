package org.zerock.workoutproject.exercise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.exercise.service.RoomService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/video")
public class VideoCallController {

    @Autowired
    private RoomService roomService;

    // 방 생성 폼 (트레이너만)
    @GetMapping("/create")
    public String createRoomForm(HttpSession session) {
        if (!isTrainer(session)) return "redirect:/";
        return "video/roomCreate";
    }

    // 방 생성 처리 (트레이너만)
    @PostMapping("/create")
    public String createRoom(@RequestParam String roomName,
                             @RequestParam String roomPassword,
                             HttpSession session, Model model) {
        if (!isTrainer(session)) return "redirect:/";
        Long trainerId = (Long) session.getAttribute("userId");
        Long roomId = roomService.createRoom(roomName, roomPassword, trainerId);
        return "redirect:/video/room?roomId=" + roomId;
    }

    @GetMapping("/room")
    public String enterRoom(@RequestParam Long roomId, HttpSession session, Model model) {
        if (isTrainer(session)) {
            model.addAttribute("roomId", roomId);
            model.addAttribute("isTrainer", true);
            return "video/room";
        }
        model.addAttribute("roomId", roomId);
        return "video/roomPassword";
    }

    @PostMapping("/room")
    public String checkRoomPassword(@RequestParam Long roomId,
                                    @RequestParam String roomPassword,
                                    HttpSession session, Model model) {
        if (roomService.checkPassword(roomId, roomPassword)) {
            model.addAttribute("roomId", roomId);
            model.addAttribute("isTrainer", false);
            return "video/room";
        } else {
            model.addAttribute("roomId", roomId);
            model.addAttribute("error", "비밀번호가 틀렸습니다.");
            return "video/roomPassword";
        }
    }

    // 권한 체크 (Spring Security 인증 객체에서 ROLE_ADMIN 권한 확인)
    private boolean isTrainer(HttpSession session) {
        // 세션에서 직접 체크하는 방식 (기존)
        // Object role = session.getAttribute("role");
        // return role != null && role.toString().equals("1");
        
        // Spring Security에서 권한 체크하는 방식 (변경)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // ROLE_ADMIN 권한이 있으면 트레이너로 인식
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
} 