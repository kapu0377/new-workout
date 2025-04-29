package org.zerock.workoutproject.qna.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.workoutproject.qna.domain.Qna;
import org.zerock.workoutproject.qna.domain.QnaReply;
import org.zerock.workoutproject.qna.dto.*;
import org.zerock.workoutproject.qna.service.QnaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/qna/api")
@RequiredArgsConstructor
public class QnaApiController {

    private final QnaService qnaService;

    @GetMapping("/list")
    public Page<QnaListDTO> getPagedQnaList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<QnaListDTO> qnaPage = qnaService.getQnaList(page, size);
        return qnaPage;
    }

    @GetMapping("/images/{ino}")
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable Long ino) {
        try {
            org.springframework.core.io.Resource imageResource = qnaService.getImageAsResource(ino);

            String contentType = Files.probeContentType(Paths.get(imageResource.getURI()));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{qno}")
    public ResponseEntity<?> getQnaDetail(@PathVariable Long qno) {
        try {
            Qna qna = qnaService.getQna(qno);

            List<QnaReply> replies = qna.getReplies();
            if (replies == null || replies.isEmpty()) {
                replies = Collections.emptyList();
            }

            List<QnaReplyDTO> replyDTOs = replies.stream()
                    .map(reply -> QnaReplyDTO.builder()
                            .rno(reply.getRno())
                            .qno(reply.getQna().getQno())
                            .replyText(reply.getReplyText())
                            .writer(reply.getWriter())
                            .admin(reply.isAdmin())
                            .replyDate(reply.getReplyDate())
                            .build())
                    .collect(Collectors.toList());

            List<QnaImageDTO> imageDTOs = Optional.ofNullable(qna.getImages())
                    .map(images -> images.stream()
                            .map(image -> {
                                log.debug("이미지 변환: ino={}, name={}", image.getIno(), image.getImageName());
                                return QnaImageDTO.builder()
                                        .ino(image.getIno())
                                        .imageName(image.getImageName())
                                        .build();
                            })
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());

            QnaDTO dto = QnaDTO.builder()
                    .qno(qna.getQno())
                    .title(qna.getTitle())
                    .questionText(qna.getQuestionText())
                    .writer(qna.getWriter())
                    .regDate(qna.getRegDate())
                    .completed(qna.isCompleted())
                    .replies(replyDTOs)
                    .images(imageDTOs)
                    .build();

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            log.warn("게시글을 찾을 수 없음: qno={}", qno, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("게시글을 찾을 수 없습니다: " + qno);
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생: qno={}", qno, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{qno}/replies")
    public ResponseEntity<QnaReplyListDTO> getReplies(
            @PathVariable Long qno,
            @RequestParam(required = false) Long lastRno
    ) {
        log.info("Getting replies for qno: {}, lastRno: {}", qno, lastRno);
        return ResponseEntity.ok(qnaService.getRepliesWithPaging(qno, lastRno));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerQna(
            @ModelAttribute QnaDTO qnaDTO,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            @RequestParam(value = "content", required = false) String contentParam,
            HttpServletResponse response
    ) {
        try {
            log.info("QnaDTO 시작 정보: {}", qnaDTO);
            log.info("QnaDTO 상세 정보 - title: '{}', questionText: '{}', writer: '{}'", 
                qnaDTO.getTitle(), qnaDTO.getQuestionText(), qnaDTO.getWriter());
            log.info("별도 content 파라미터: '{}'", contentParam);
            log.info("QnaDTO title이 null인지?: {}", qnaDTO.getTitle() == null);
            log.info("QnaDTO questionText가 null인지?: {}", qnaDTO.getQuestionText() == null);
            
            // contentParam이 있고 questionText가 비어있으면 대체
            if ((qnaDTO.getQuestionText() == null || qnaDTO.getQuestionText().trim().isEmpty()) 
                    && contentParam != null && !contentParam.trim().isEmpty()) {
                log.info("content 파라미터로 questionText 대체");
                qnaDTO.setQuestionText(contentParam);
            }
            
            if (qnaDTO.getTitle() == null || qnaDTO.getTitle().trim().isEmpty()) {
                log.warn("제목이 비어 있습니다.");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "제목이 비어 있습니다.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (qnaDTO.getQuestionText() == null || qnaDTO.getQuestionText().trim().isEmpty()) {
                log.warn("내용이 비어 있습니다.");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "내용이 비어 있습니다.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (imageFiles != null) {
                log.info("Received {} image files", imageFiles.length);
                for (MultipartFile file : imageFiles) {
                    log.info("File name: {}, size: {}", file.getOriginalFilename(), file.getSize());
                }
            }

            QnaDTO savedDto = qnaService.register(qnaDTO, imageFiles);
            log.info("QnA 등록 성공: qno={} ", savedDto.getQno());

            return ResponseEntity.ok(savedDto);
        } catch (Exception e) {
            log.error("QnA 등록 중 오류 발생", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "질문 등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/{qno}/reply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReply(
            @PathVariable Long qno,
            @Valid @RequestBody QnaReplyDTO replyDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Qna qna = qnaService.getQna(qno);

            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isWriter = qna.getWriter().equals(userDetails.getUsername());

            if (!isAdmin && !isWriter) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("관리자 또는 글 작성자만 답변을 작성할 수 있습니다.");
            }

            replyDTO.setQno(qno);
            replyDTO.setWriter(userDetails.getUsername());
            replyDTO.setAdmin(isAdmin);

            QnaReplyDTO savedReply = qnaService.createReply(replyDTO);

            return ResponseEntity.ok(savedReply);

        } catch (EntityNotFoundException e) {
            log.error("답변 등록 실패: 질문을 찾을 수 없음 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 질문을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("답변 등록 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body("답변 등록 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{qno}")
    public ResponseEntity<?> deleteQna(@PathVariable Long qno) {
        try {
            qnaService.remove(qno);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{qno}/completed")
    public ResponseEntity<?> completeQna(
            @PathVariable Long qno,
            @RequestBody Map<String, Boolean> requestBody,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            boolean completed = requestBody.get("completed");
            Qna qna = qnaService.getQna(qno);
            
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인이 필요합니다.");
            }

            boolean isWriter = qna.getWriter().equals(userDetails.getUsername());
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!(isWriter || isAdmin)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("접근 권한이 없습니다.");
            }

            Qna updatedQna = qnaService.answeredQna(qno, completed);

            QnaDTO dto = QnaDTO.builder()
                    .qno(updatedQna.getQno())
                    .title(updatedQna.getTitle())
                    .writer(updatedQna.getWriter())
                    .completed(updatedQna.isCompleted())
                    .build();

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }

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