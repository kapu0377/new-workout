package org.zerock.workoutproject.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.workoutproject.video.entity.VideoRoom;

import java.util.Optional;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {

    Optional<VideoRoom> findByRoomId(String roomId);
    
    boolean existsByRoomId(String roomId);
} 