package org.zerock.workoutproject.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.workoutproject.exercise.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
} 