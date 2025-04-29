package org.zerock.workoutproject.qna.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "faqs")
@Getter
@Setter
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question = "";

    @Column(nullable = false, length = 1000)
    private String answer = "";

    private boolean isVisible = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}