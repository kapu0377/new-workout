package org.zerock.workoutproject.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.workoutproject.exercise.domain.Exercise;
import org.zerock.workoutproject.exercise.repository.search.ExerciseSearch;

public interface ExerciseRepository extends JpaRepository<Exercise,Integer>, ExerciseSearch {
}
