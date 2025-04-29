package org.zerock.workoutproject.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.workoutproject.board.domain.BoardReply;

public interface ReplyRepository extends JpaRepository<BoardReply, Long> {
    @Query("select r from BoardReply r where r.board.bno =:bno")
    Page<BoardReply> listOfBoard(Long bno, Pageable pageable);
    void deleteByBoard_bno(Long bno);


}
