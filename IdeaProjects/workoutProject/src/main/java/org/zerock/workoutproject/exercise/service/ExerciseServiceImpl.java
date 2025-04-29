package org.zerock.workoutproject.exercise.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.workoutproject.exercise.domain.Exercise;
import org.zerock.workoutproject.exercise.dto.*;
import org.zerock.workoutproject.exercise.repository.ExerciseReplyRepository;
import org.zerock.workoutproject.exercise.repository.ExerciseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseServiceImpl implements ExerciseService {
  private final ExerciseRepository exerciseRepository;
  private final ModelMapper modelMapper;
  private final ExerciseReplyRepository exerciseReplyRepository;

  @Override
  public PageResponseDTO<ExerciseDTO> searchList(PageRequestDTO pageRequestDTO) {
    String keyword = pageRequestDTO.getKeyword();
    Pageable pageable = pageRequestDTO.getPageable("eno");
    Page<Exercise> result = exerciseRepository.searchAll(keyword, pageable);
    List<ExerciseDTO> dtoList = result.getContent().stream()
        .map(exercise -> modelMapper.map(exercise, ExerciseDTO.class))
        .collect(Collectors.toList());
    return PageResponseDTO.<ExerciseDTO>withAll().
        pageRequestDTO(pageRequestDTO)
        .dtoList(dtoList)
        .total((int)result.getTotalElements())
        .build();
  }

  @Override
  public int registerExercise(ExerciseDTO exerciseDTO) {
    Exercise exercise = modelMapper.map(exerciseDTO, Exercise.class);
    return exerciseRepository.save(exercise).getEno();
  }

  @Override
  public void modifyExercise(ExerciseDTO exerciseDTO) {
    Optional<Exercise> result = exerciseRepository.findById(exerciseDTO.getEno());
    Exercise exercise = result.orElseThrow();
    exercise.change(exerciseDTO.getTitle(), exerciseDTO.getContent(),exerciseDTO.getUrl());
    // 모든 이미지 초기화
    exercise.clearImages();
    // 새로운 이미지 추가
    if(exerciseDTO.getFileNames() != null){
      for(String fileName : exerciseDTO.getFileNames()){
        String[] arr = fileName.split("_", 2);
        exercise.addImage(arr[0], arr[1]);
      }
    }
    exerciseRepository.save(exercise);
  }

  @Override
  public void removeExercise(int eno) {
    exerciseRepository.deleteById(eno);
    exerciseReplyRepository.deleteByExercise_eno(eno);
  }

  @Override
  public ExerciseDTO getExercise(int eno) {
    Optional<Exercise> result = exerciseRepository.findById(eno);
    Exercise exercise = result.orElseThrow();
    ExerciseDTO exerciseDTO = entityToDTO(exercise);
    modelMapper.map(exercise, ExerciseDTO.class);
    return exerciseDTO;
  }

  @Override
  public PageResponseDTO<ExerciseListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {

    String keyword = pageRequestDTO.getKeyword();
    Pageable pageable = pageRequestDTO.getPageable("eno");

    // 레포지토리를 실행하여 검색 결과를 저장
    Page<ExerciseListAllDTO> result = exerciseRepository.searchWithAll(keyword,pageable);

    // PageResponseDTO에 필요한 데이터를 설정하여 반환
    return PageResponseDTO.<ExerciseListAllDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(result.getContent())
            .total((int)result.getTotalElements())
            .build();
  }

  @Override
  public PageResponseDTO<ExerciseListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
    String keyword = pageRequestDTO.getKeyword();
    Pageable pageable = pageRequestDTO.getPageable("eno");

    // 레포지토리를 실행하여 검색 결과를 저장
    Page<ExerciseListReplyCountDTO> result = exerciseRepository.searchWithReplyCount(keyword,pageable);

    // PageResponseDTO에 필요한 데이터를 설정하여 반환
    return PageResponseDTO.<ExerciseListReplyCountDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(result.getContent())
            .total((int)result.getTotalElements())
            .build();
  }

  @Override
  public int register(ExerciseDTO exerciseDTO) {
    Exercise exercise = dtoToEntity(exerciseDTO);
    //레포지토리의 save메서드를 이용하여 insert문 실행 후 생성된 데이터의 bno를 반환
    int eno = exerciseRepository.save(exercise).getEno();
    return eno;
  }
}









