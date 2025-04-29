package org.zerock.workoutproject.board.service;

import org.zerock.workoutproject.board.dto.PageRequestDTO;
import org.zerock.workoutproject.board.dto.PageResponseDTO;
import org.zerock.workoutproject.board.dto.ReplyDTO;
import org.zerock.workoutproject.member.domain.Member;

public interface ReplyService {
    Long register(ReplyDTO replyDTO);
    void remove(Long rno);
    PageResponseDTO<ReplyDTO> getListofBoard(Long bno, PageRequestDTO pageRequestDTO);
    void modify(ReplyDTO replyDTO);
    public boolean checkAdmin(Member member);
}
