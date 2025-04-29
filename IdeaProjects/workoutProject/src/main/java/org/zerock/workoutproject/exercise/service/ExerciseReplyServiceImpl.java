package org.zerock.workoutproject.exercise.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.exercise.domain.Reply;
import org.zerock.workoutproject.exercise.dto.PageRequestDTO;
import org.zerock.workoutproject.exercise.dto.PageResponseDTO;
import org.zerock.workoutproject.exercise.dto.ReplyDTO;
import org.zerock.workoutproject.exercise.repository.ExerciseReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseReplyServiceImpl implements ExerciseReplyService {
  private final ExerciseReplyRepository exerciseReplyRepository;
  private final ModelMapper modelMapper;
  @Override
  public Long register(ReplyDTO replyDTO) {
    Reply reply = modelMapper.map(replyDTO,Reply.class);
    Long rno = exerciseReplyRepository.save(reply).getRno();
    return rno;
  }

  @Override
  public ReplyDTO read(Long rno) {
    Optional<Reply> replyOptional = exerciseReplyRepository.findById(rno);
    Reply reply = replyOptional.orElseThrow();
    return modelMapper.map(reply, ReplyDTO.class);
  }

  @Override
  public void modify(ReplyDTO replyDTO) {
    Optional<Reply> replyOptional = exerciseReplyRepository.findById(replyDTO.getRno());
    Reply reply = replyOptional.orElseThrow();
    reply.changeText(replyDTO.getReplyText());
    exerciseReplyRepository.save(reply);
  }

  @Override
  public void remove(Long rno) {
    exerciseReplyRepository.deleteById(rno);
  }

  @Override
  public PageResponseDTO<ReplyDTO> getListOfExercise(int eno, PageRequestDTO pageRequestDTO) {
    Pageable pageable = PageRequest.of(
        pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage()-1,
        pageRequestDTO.getSize(),
        Sort.by("rno").descending());
    Page<Reply> result = exerciseReplyRepository.listOfExercise(eno, pageable);
    List<ReplyDTO> dtoList = result.getContent().stream()
        .map(reply -> modelMapper.map(reply, ReplyDTO.class))
        .collect(Collectors.toList());
    return PageResponseDTO.<ReplyDTO>withAll()
        .pageRequestDTO(pageRequestDTO)
        .dtoList(dtoList)
        .total((int)result.getTotalElements())
        .build();
  }
}
