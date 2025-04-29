package org.zerock.workoutproject.exercise.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.workoutproject.exercise.dto.PageRequestDTO;
import org.zerock.workoutproject.exercise.dto.PageResponseDTO;
import org.zerock.workoutproject.exercise.dto.ReplyDTO;
import org.zerock.workoutproject.exercise.service.ExerciseReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/exercise/replies")
@Log4j2
@RequiredArgsConstructor
public class ExerciseReplyController {
  private final ExerciseReplyService exerciseReplyService;

  // consumes = 통신에 사용되는 형식 설정
  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  // @RequestBody : RestController에서 폼 태그의 데이터를 받기위해 반드시 필요한 어노테이션
  // ResponseEntity객체 : HttpStatus,HttpHeader, HttpBody의 내용을 설정하는 객체, 컨트롤러의 정상 실행이나 데이터등의 여러가지 데이터를 설정하여 브라우저에 돌려주는 객체
  public Map<String,Long> register(
      @Valid @RequestBody ReplyDTO replyDTO,
      BindingResult bindingResult) throws BindException {
    log.info(replyDTO);
    if(bindingResult.hasErrors()){
      throw new BindException(bindingResult);
    }
    Map<String, Long> resultMap = new HashMap<>();
    Long rno = exerciseReplyService.register(replyDTO);
    resultMap.put("rno", rno);
    return resultMap;
  }

  @GetMapping(value = "/read/{eno}")
  public PageResponseDTO<ReplyDTO> getList(@PathVariable("eno") int eno, PageRequestDTO pageRequestDTO, Model model){
    PageResponseDTO<ReplyDTO> responseDTO = exerciseReplyService.getListOfExercise(eno, pageRequestDTO);
//    model.addAttribute("responseDTO", responseDTO);
    return responseDTO;
  }
  @GetMapping(value = "/{rno}")
  public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno){
    ReplyDTO replyDTO = exerciseReplyService.read(rno);
    return replyDTO;
  }
  @DeleteMapping("/{rno}")
  public Map<String, Long> remove(@PathVariable("rno") Long rno){
    exerciseReplyService.remove(rno);
    Map<String,Long> resultMap = new HashMap<>();
    resultMap.put("rno", rno);
    return resultMap;
  }
  @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO){
    replyDTO.setRno(rno);
    exerciseReplyService.modify(replyDTO);
    Map<String,Long> resultMap = new HashMap<>();
    resultMap.put("rno", rno);
    return resultMap;
  }

}














