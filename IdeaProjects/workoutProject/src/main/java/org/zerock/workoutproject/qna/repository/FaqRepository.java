package org.zerock.workoutproject.qna.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.workoutproject.qna.domain.Faq;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    @Query("SELECT f FROM Faq f WHERE f.isVisible = true ORDER BY f.displayOrder ASC")
    List<Faq> findAllVisibleFaqs();}
