package org.zerock.workoutproject.video.service;

import org.zerock.workoutproject.video.dto.RoomInfo;

import java.util.List;

public interface VideoService {
    RoomInfo createRoom(String roomName, String password);
    RoomInfo getRoomById(String roomId);
    List<RoomInfo> getAllRooms();
    void deleteRoom(String roomId); 
    
    /**
     * 방 비밀번호 검증
     * @param roomId 방 ID
     * @param password 입력한 비밀번호
     * @return 비밀번호 일치 여부
     */
    boolean verifyRoomPassword(String roomId, String password);
} 