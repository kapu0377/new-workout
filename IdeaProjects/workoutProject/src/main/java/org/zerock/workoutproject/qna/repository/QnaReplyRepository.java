package org.zerock.workoutproject.qna.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.workoutproject.qna.domain.QnaReply;

import java.util.List;

@Repository

public interface QnaReplyRepository  extends JpaRepository<QnaReply, Long> {
    // 특정 QnA의 첫 페이지 댓글을 조회하는 쿼리
    @Query("SELECT r FROM QnaReply r " +
            "WHERE r.qna.qno = :qno " +
            "ORDER BY r.rno")
    List<QnaReply> findFirstPageByQno(
            @Param("qno") Long qno,
            Pageable pageable
    );

    // 특정 QnA의 이전 댓글들을 조회하는 쿼리
    @Query("SELECT r FROM QnaReply r " +
            "WHERE r.qna.qno = :qno " +
            "AND r.rno > :lastRno " +
            "ORDER BY r.rno ASC")
    List<QnaReply> findRepliesWithPaging(
            @Param("qno") Long qno,
            @Param("lastRno") Long lastRno,
            Pageable pageable
    );

    // 특정 QnA의 전체 댓글 수를 조회하는 쿼리
    @Query("SELECT COUNT(r) FROM QnaReply r WHERE r.qna.qno = :qno")
    long countByQno(@Param("qno") Long qno);
}