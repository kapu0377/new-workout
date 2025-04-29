package org.zerock.workoutproject.qna.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qna_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino; // 이미지 번호
    private String imageName; // 실제 서버에 저장된 이미지 파일명
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qno")
    private Qna qna;
}
