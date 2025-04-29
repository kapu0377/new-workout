package org.zerock.workoutproject.exercise.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.workoutproject.exercise.domain.Reply;

public interface ExerciseReplyRepository extends JpaRepository<Reply, Long> {
  // JPQL에서는 테이블이나 열이름이 아닌 Entity의 이름을 사용하여야 합니다.
  @Query("SELECT r FROM Reply r WHERE r.exercise.eno = :eno")
  Page<Reply> listOfExercise(int eno, Pageable pageable);

  void deleteByExercise_eno(int eno);
}
