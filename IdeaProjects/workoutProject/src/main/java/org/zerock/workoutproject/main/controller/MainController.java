package org.zerock.workoutproject.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.workoutproject.main.service.MainService;

import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class MainController {
  private final MainService mainService;
//  @GetMapping("/")
//  public String index(){
//    return "index";
//  }
//  @GetMapping("/notice")
//  public String notice(){
//    return "exercise/notice";
//  }

  @GetMapping("/")
  public String getMainPage(Model model) {
    // 모든 게시판의 최신 게시물 가져오기
    Map<String, List<Map<String, Object>>> latestPosts = mainService.getLatestPostsFromAllBoards();
    model.addAttribute("latestPosts", latestPosts);
    return "index"; // main.html 템플릿
  }
}

