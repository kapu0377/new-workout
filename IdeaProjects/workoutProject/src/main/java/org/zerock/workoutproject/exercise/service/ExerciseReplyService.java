package org.zerock.workoutproject.exercise.service;

import org.zerock.workoutproject.exercise.dto.PageRequestDTO;
import org.zerock.workoutproject.exercise.dto.PageResponseDTO;
import org.zerock.workoutproject.exercise.dto.ReplyDTO;

public interface ExerciseReplyService {
  Long register(ReplyDTO replyDTO);
  ReplyDTO read(Long rno);
  void modify(ReplyDTO replyDTO);
  void remove(Long rno);
  PageResponseDTO<ReplyDTO> getListOfExercise(int eno, PageRequestDTO pageRequestDTO);
}
