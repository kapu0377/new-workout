package org.zerock.workoutproject.exercise.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.zerock.workoutproject.common.BaseEntity;
import org.zerock.workoutproject.exercise.dto.ExerciseDTO;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Exercise extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int eno;
  @Column(length=255, nullable = false)
  private String title;
  @Column(columnDefinition = "TEXT",nullable = false)
  private String content;
  private String url;
  private int view;


  public void change(ExerciseDTO dto){
    this.title = dto.getTitle();
    this.content = dto.getContent();
    this.url = dto.getUrl();
  }
  @OneToMany(mappedBy="exercise",
          cascade={CascadeType.ALL},
          fetch=FetchType.LAZY,
          orphanRemoval = true)
  @Builder.Default
  @BatchSize(size=20)
  private Set<ExerciseImage> imageSet = new HashSet<>();
  // board 엔티티에 이미지를 추가하는 메서드
  public void addImage(String uuid, String fileName) {
    ExerciseImage exerciseImage = ExerciseImage.builder()
            .uuid(uuid)
            .fileName(fileName)
            // board_bno 설정
            .exercise(this)
            //0번부터 시작하는 순서를 설정
            .ord(imageSet.size())
            .build();
    imageSet.add(exerciseImage);
  }
  //게시글에있는 모든 이미지 파일을 삭제
  public void clearImages(){
    imageSet.forEach(boardImage -> boardImage.changeBoard(null));
    this.imageSet.clear();
  }

  public void change(String title, String content, String url) {
    this.title = title;
    this.content = content;
    this.url = url;
  }
}
