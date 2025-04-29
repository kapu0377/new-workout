package org.zerock.workoutproject.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.zerock.workoutproject.main.service.MainService;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    private final MainService mainService;

    public GlobalModelAttributeAdvice(MainService mainService) {
        this.mainService = mainService;
    }

    @ModelAttribute("latestPosts")
    public Map<String, List<Map<String, Object>>> addLatestPosts() {
        return mainService.getLatestPostsFromAllBoards();
    }
}