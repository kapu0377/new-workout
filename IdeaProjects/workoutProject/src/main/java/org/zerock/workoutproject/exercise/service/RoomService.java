package org.zerock.workoutproject.exercise.service;

public interface RoomService {
    Long createRoom(String roomName, String roomPassword, Long trainerId);
    boolean checkPassword(Long roomId, String inputPassword);
} 