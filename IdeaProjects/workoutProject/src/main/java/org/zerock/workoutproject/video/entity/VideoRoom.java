package org.zerock.workoutproject.video.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomId;  // UUID로 생성되는 고유 ID

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private String roomPassword;  // 실제 환경에서는 암호화해서 저장해야 합니다

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 추가 필드로 확장 가능 (ex: 생성자 정보, 최대 인원 수, 만료 시간 등)
} 