package org.zerock.workoutproject.video.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.workoutproject.video.dto.RoomInfo;
import org.zerock.workoutproject.video.entity.VideoRoom;
import org.zerock.workoutproject.video.repository.VideoRoomRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoServiceImpl implements VideoService {

    private final VideoRoomRepository videoRoomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RoomInfo createRoom(String roomName, String password) {
        log.info("Creating new room with name: {}", roomName);
        String roomId = UUID.randomUUID().toString();
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        log.info("Password encoded successfully");
        
        VideoRoom videoRoom = VideoRoom.builder()
                .roomId(roomId)
                .roomName(roomName)
                .roomPassword(encodedPassword)
                .createdAt(LocalDateTime.now())
                .build();
        
        // 저장
        try {
            VideoRoom savedRoom = videoRoomRepository.save(videoRoom);
            log.info("Room created and saved successfully: id={}, name={}", savedRoom.getRoomId(), savedRoom.getRoomName());
            return RoomInfo.fromEntity(savedRoom);
        } catch (Exception e) {
            log.error("Error saving room to database", e);
            throw e;
        }
    }

    @Override
    public RoomInfo getRoomById(String roomId) {
        log.info("Getting room by ID: {}", roomId);
        return videoRoomRepository.findByRoomId(roomId)
                .map(room -> {
                    log.info("Room found: {}", room.getRoomName());
                    return RoomInfo.fromEntity(room);
                })
                .orElseGet(() -> {
                    log.warn("Room not found with ID: {}", roomId);
                    return null;
                });
    }

    @Override
    public List<RoomInfo> getAllRooms() {
        log.info("Retrieving all rooms from database");
        List<VideoRoom> allRooms = videoRoomRepository.findAll();
        log.info("Found {} rooms in database", allRooms.size());
        
        if (allRooms.isEmpty()) {
            log.warn("No rooms found in the database");
        } else {
            for (VideoRoom room : allRooms) {
                log.info("Room in DB: ID={}, Name={}, CreatedAt={}", 
                        room.getRoomId(), room.getRoomName(), room.getCreatedAt());
            }
        }
        
        // 모든 방을 가져와서 DTO로 변환
        return allRooms.stream()
                .map(room -> {
                    RoomInfo dto = RoomInfo.fromEntity(room);
                    log.info("Mapped to DTO: {}", dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRoom(String roomId) {
        log.info("Attempting to delete room with ID: {}", roomId);
        videoRoomRepository.findByRoomId(roomId)
                .ifPresent(room -> {
                    videoRoomRepository.delete(room);
                    log.info("Room deleted: id={}", roomId);
                });
    }
    
    /**
     * 방 비밀번호 검증
     * @param roomId 방 ID
     * @param password 입력한 비밀번호
     * @return 비밀번호 일치 여부
     */
    @Override
    public boolean verifyRoomPassword(String roomId, String password) {
        log.info("Verifying password for room: {}", roomId);
        return videoRoomRepository.findByRoomId(roomId)
                .map(room -> {
                    boolean matches = passwordEncoder.matches(password, room.getRoomPassword());
                    log.info("Password verification for room {}: {}", roomId, matches ? "SUCCESS" : "FAILED");
                    return matches;
                })
                .orElseGet(() -> {
                    log.warn("Cannot verify password - Room not found: {}", roomId);
                    return false;
                });
    }
} 