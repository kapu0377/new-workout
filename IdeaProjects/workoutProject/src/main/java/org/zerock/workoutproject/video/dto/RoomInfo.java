package org.zerock.workoutproject.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.workoutproject.video.entity.VideoRoom;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfo {
    private String roomId;
    private String roomName;
    private LocalDateTime createdAt;

    public RoomInfo(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.createdAt = LocalDateTime.now();
    }

    public static RoomInfo fromEntity(VideoRoom entity) {
        return RoomInfo.builder()
                .roomId(entity.getRoomId())
                .roomName(entity.getRoomName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
} 