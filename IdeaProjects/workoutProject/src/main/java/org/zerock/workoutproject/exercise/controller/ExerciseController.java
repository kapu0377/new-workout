package org.zerock.workoutproject.exercise.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.workoutproject.exercise.dto.*;
import org.zerock.workoutproject.exercise.service.ExerciseReplyService;
import org.zerock.workoutproject.exercise.service.ExerciseService;
import org.zerock.workoutproject.main.service.MainService;

import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/exercise")
public class ExerciseController {
  private final ExerciseService exerciseService;
  private final ExerciseReplyService exerciseReplyService;
  private final MainService mainService;

  @GetMapping("/list")
  public void getNoticeList(PageRequestDTO pageRequestDTO, Model model) {
    PageResponseDTO<ExerciseListAllDTO> pageResponseDTO = exerciseService.listWithAll(pageRequestDTO);
    model.addAttribute("pageResponseDTO",pageResponseDTO);
  }
  @GetMapping("/add")
  public void register(@Valid PageRequestDTO pageRequestDTO){}
  @PostMapping("/add")
  public String register(@Valid ExerciseDTO exerciseDTO, PageRequestDTO pageRequestDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes){
    if(bindingResult.hasErrors()){
      log.info("has errors...............");
      redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
      return "redirect:/exercise/add";
    }
    log.info(exerciseDTO);
    int eno = exerciseService.registerExercise(exerciseDTO);
    redirectAttributes.addFlashAttribute("result", eno);
    return "redirect:/exercise/notice";
  }

  @GetMapping({"/modify"})
  public void modify(int eno,PageRequestDTO pageRequestDTO, Model model){
    ExerciseDTO dto = exerciseService.getExercise(eno);
    model.addAttribute("dto",dto);

  }
  @PostMapping("/modify")
  public String modify(PageRequestDTO pageRequestDTO,
                       @Valid ExerciseDTO exerciseDTO,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes){
    if(bindingResult.hasErrors()){
      String link = pageRequestDTO.getLink();
      redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
      redirectAttributes.addAttribute("eno", exerciseDTO.getEno());
      return "redirect:/exercise/modify?"+link;
    }
    exerciseService.modifyExercise(exerciseDTO);
    redirectAttributes.addFlashAttribute("result", "modified");
    redirectAttributes.addAttribute("eno", exerciseDTO.getEno());
    return "redirect:/exercise/read?eno="+ exerciseDTO.getEno()+"&"+pageRequestDTO.getLink();
  }
  @PostMapping("/remove")
  public String remove(int eno, RedirectAttributes redirectAttributes){
    exerciseService.removeExercise(eno);
   // redirectAttributes.addFlashAttribute("result", "removed");
    return "redirect:/exercise/notice";
  }

  @GetMapping("/notice")
  public String notice(PageRequestDTO pageRequestDTO, Model model){
//    ExerciseDTO dto = exerciseService.getExercise(eno);
//    model.addAttribute("dto",dto);
    PageResponseDTO<ExerciseListAllDTO> pageResponseDTO = exerciseService.listWithAll(pageRequestDTO);
    model.addAttribute("pageResponseDTO",pageResponseDTO);
    return "exercise/notice";
  }
  @GetMapping("/read")
  public String read(int eno, PageRequestDTO pageRequestDTO, Model model, ReplyDTO replyDTO){
    ExerciseDTO dto = exerciseService.getExercise(eno);
    model.addAttribute("dto",dto);
    return "exercise/read";
  }
}
