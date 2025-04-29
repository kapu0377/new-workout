package org.zerock.workoutproject.exercise.service;


import org.zerock.workoutproject.board.dto.BoardDTO;
import org.zerock.workoutproject.exercise.domain.Exercise;
import org.zerock.workoutproject.exercise.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface ExerciseService {
  PageResponseDTO<ExerciseDTO> searchList(PageRequestDTO pageRequestDTO);
  int registerExercise(ExerciseDTO exerciseDTO);
  void modifyExercise(ExerciseDTO exerciseDTO);
  void removeExercise(int eno);
  ExerciseDTO getExercise(int eno);
  PageResponseDTO<ExerciseListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);
  PageResponseDTO<ExerciseListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);
  int register(ExerciseDTO exerciseDTO);

  default Exercise dtoToEntity(ExerciseDTO exerciseDTO) {
    Exercise exercise = Exercise.builder()
            .eno(exerciseDTO.getEno())
            .title(exerciseDTO.getTitle())
            .content(exerciseDTO.getContent())
            .url(exerciseDTO.getUrl())
            .build();
    if(exerciseDTO.getFileNames() != null){
      exerciseDTO.getFileNames().forEach(fileName -> {
        // uuid_파일이름.확장자를 uuid와 파일이름으로 나누기 위한 split메서드 사용
        String[] arr = fileName.split("_",2);
        exercise.addImage(arr[0],arr[1]);
      });
    }
    return exercise;
  }
  // entity를 dto로 변환하는 메서드
  default ExerciseDTO entityToDTO(Exercise exercise) {
    ExerciseDTO exerciseDTO = ExerciseDTO.builder()
            .eno(exercise.getEno())
            .title(exercise.getTitle())
            .content(exercise.getContent())
            .url(exercise.getUrl())
            .modDate(exercise.getModDate())
            .regDate(exercise.getRegDate())
            .build();
    List<String> fileNames = exercise.getImageSet().stream().sorted()
            .map(noticeImage -> noticeImage.getUuid()+"_"+noticeImage.getFileName())
            .collect(Collectors.toList());
    exerciseDTO.setFileNames(fileNames);
    return exerciseDTO;
  }
}
