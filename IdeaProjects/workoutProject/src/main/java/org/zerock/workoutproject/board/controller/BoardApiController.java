package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.board.dto.BoardDTO;
import org.zerock.workoutproject.board.dto.ViewCountDTO;
import org.zerock.workoutproject.board.service.BoardService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 게시판 관련 API를 처리하는 컨트롤러
 * 
 * 주요 기능:
 * - 조회수 증가 및 조회
 * - 현재 사용자 정보 조회
 * 
 * 참고: 댓글 관련 API는 ReplyController(/board/api/replies/*)에서 처리
 */
@RestController
@RequestMapping("/board/api")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    // 조회수 증가 API
    @PostMapping("/view/{bno}")
    public ResponseEntity<Integer> increaseViewCount(@PathVariable Long bno) {
        int newViewCount = boardService.increaseViewCount(bno);
        return ResponseEntity.ok(newViewCount);
    }

    // 조회수 조회 API
    @GetMapping("/view-counts")
    public ResponseEntity<List<ViewCountDTO>> getViewCounts() {
        List<ViewCountDTO> viewCounts = boardService.getAllViewCounts();
        return ResponseEntity.ok(viewCounts);
    }
    
    // 현재 사용자 정보 조회 API
    @GetMapping("/user/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userInfo);
    }
}
