package org.zerock.workoutproject.exercise.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.workoutproject.exercise.domain.Exercise;
import org.zerock.workoutproject.exercise.dto.ExerciseListAllDTO;
import org.zerock.workoutproject.exercise.dto.ExerciseListReplyCountDTO;

public interface ExerciseSearch {
  Page<Exercise> searchAll(String keyword, Pageable pageable);
  Page<ExerciseListReplyCountDTO> searchWithReplyCount(String keyword, Pageable pageable);
  Page<ExerciseListAllDTO> searchWithAll(String keyword, Pageable pageable);
}
