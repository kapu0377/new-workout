package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.board.dto.PageRequestDTO;
import org.zerock.workoutproject.board.dto.PageResponseDTO;
import org.zerock.workoutproject.board.dto.ReplyDTO;
import org.zerock.workoutproject.board.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

/**
 * 게시판 댓글 관련 API를 처리하는 컨트롤러
 * 
 * 주요 기능:
 * - 댓글 목록 조회
 * - 댓글 등록
 * - 댓글 수정
 * - 댓글 삭제
 * 
 * URL 경로: /board/api/replies/*
 * 
 * 참고: 게시판 기본 API는 BoardApiController(/board/api/*)에서 처리
 */
@RestController
@RequestMapping("/board/api/replies")
@RequiredArgsConstructor
@Log4j2
public class ReplyController {
    private final ReplyService replyService;

    /**
     * 댓글 등록 API
     * @param bno 게시글 번호
     * @param replyDTO 댓글 정보
     * @param bindingResult 유효성 검사 결과
     * @return 등록된 댓글 번호
     * @throws BindException 유효성 검사 실패 시
     */
    @PostMapping(value = "/{bno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> register(
            @PathVariable("bno") Long bno,
            @Valid @RequestBody ReplyDTO replyDTO, 
            BindingResult bindingResult) throws BindException {
        log.info(replyDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // bno 설정
        replyDTO.setBno(bno);

        Map<String, Long> resultMap = new HashMap<>();
        Long rno = replyService.register(replyDTO);
        resultMap.put("rno", rno);
        return resultMap;
    }

    /**
     * 댓글 목록 조회 API
     * @param bno 게시글 번호
     * @param pageRequestDTO 페이징 정보
     * @return 댓글 목록 및 페이징 정보
     */
    @GetMapping(value = "/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListofBoard(bno, pageRequestDTO);

        return responseDTO;
    }

    /**
     * 댓글 삭제 API
     * @param rno 댓글 번호
     * @return 삭제된 댓글 번호
     */
    @DeleteMapping("/{rno}")
    public Map<String, Long> delete(@PathVariable("rno") Long rno) {
        replyService.remove(rno);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }

    /**
     * 댓글 수정 API
     * @param rno 댓글 번호
     * @param replyDTO 수정할 댓글 정보
     * @return 수정된 댓글 번호
     */
    @PutMapping(value = "/{rno}")
    public Map<String, Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO) {
        log.info("댓글 수정: " + rno);
        replyDTO.setRno(rno);

        replyService.modify(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }
}
