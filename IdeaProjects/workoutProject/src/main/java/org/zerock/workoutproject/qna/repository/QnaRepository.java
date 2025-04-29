package org.zerock.workoutproject.qna.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.workoutproject.qna.domain.Qna;

import java.util.Optional;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    @Query("SELECT q FROM Qna q ORDER BY q.regDate DESC")
    Page<Qna> findAllByOrderByRegDateDesc(Pageable pageable);

    @Query("SELECT q FROM Qna q LEFT JOIN FETCH q.images WHERE q.qno = :qno")
    Optional<Qna> findQnaWithImages(@Param("qno") Long qno);
    // 조회수 증가 쿼리
    @Modifying
    @Query("UPDATE Qna b SET b.view = b.view + 1 WHERE b.qno = :bno")
    void increaseViewCount(Long qno);
}