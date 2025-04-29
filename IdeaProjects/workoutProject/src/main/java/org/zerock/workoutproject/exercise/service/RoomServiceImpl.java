package org.zerock.workoutproject.exercise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.exercise.domain.Room;
import org.zerock.workoutproject.exercise.repository.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Long createRoom(String roomName, String roomPassword, Long trainerId) {
        Room room = new Room();
        room.setName(roomName);
        room.setPassword(roomPassword);
        room.setTrainerId(trainerId);
        roomRepository.save(room);
        return room.getId();
    }

    @Override
    public boolean checkPassword(Long roomId, String inputPassword) {
        Room room = roomRepository.findById(roomId).orElse(null);
        return room != null && room.getPassword().equals(inputPassword);
    }
} 