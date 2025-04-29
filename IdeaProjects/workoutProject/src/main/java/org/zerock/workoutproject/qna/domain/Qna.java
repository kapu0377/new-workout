package org.zerock.workoutproject.qna.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qno;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false)
    private String writer;

    private boolean completed;
    private boolean secret;
    private boolean hide;
    @Builder.Default
    private Long view = 0L;

    @Column(name = "regDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate; // 질문 등록일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modDate; // 질문 수정일


    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QnaReply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QnaImage> images = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.regDate = this.regDate == null ? LocalDateTime.now() : this.regDate;
        this.modDate = this.modDate == null ? LocalDateTime.now() : this.modDate;
    }

    public void change(String title, String questionText) {
        this.title = title;
        this.questionText = questionText;
    }

    public void addImage(QnaImage image) {
        this.images.add(image);
        if (image.getQna() != this) {
            image.setQna(this);
        }
    }

    public void removeImage(QnaImage image) {
        images.remove(image);
        image.setQna(null);
    }

    public void clearImages() {
        images.forEach(image -> image.setQna(null));
        images.clear();
    }
}