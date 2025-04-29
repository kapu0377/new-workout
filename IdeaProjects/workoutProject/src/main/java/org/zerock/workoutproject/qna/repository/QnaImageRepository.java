package org.zerock.workoutproject.qna.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.workoutproject.qna.domain.QnaImage;

import java.util.List;

public interface QnaImageRepository extends JpaRepository<QnaImage, Long> {
    @Query("SELECT qi FROM QnaImage qi JOIN FETCH qi.qna WHERE qi.qna.qno = :qno")
    List<QnaImage> findByQnaQnoWithQna(@Param("qno") Long qno);
}