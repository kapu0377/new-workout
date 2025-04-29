package org.zerock.workoutproject.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.workoutproject.board.dto.*;
import org.zerock.workoutproject.board.service.BoardService;
import org.zerock.workoutproject.main.service.MainService;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    @Value("${org.zerock.upload.path}")
    private String uploadPath;
    private final BoardService boardService;
    private final MainService mainService;


    @GetMapping("/add")
    public void register(PageRequestDTO req) {}

    @PostMapping("/add")
    public String register(@Valid @ModelAttribute BoardDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        System.out.println("==== /board/add POST 진입 ====");
        System.out.println("title: " + dto.getTitle() + ", content: " + dto.getContent() + ", writer: " + dto.getWriter());
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println("유효성 에러: " + error.toString()));
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/add";
        }
        Long bno = boardService.register(dto);
        System.out.println("글 등록 성공, bno: " + bno);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/read?bno=" + bno;
    }

    @GetMapping({"/read","modify"})
    public void read(Long bno, Model model, PageRequestDTO req) {
        BoardDTO dto = boardService.readOne(bno);
        model.addAttribute("board", dto);
    }


    @PostMapping("/modify")
    public String modify(BoardDTO dto) {
        Long bno = boardService.modify(dto);
        return "redirect:/board/read?bno="+bno;
    }


    @PostMapping("/remove")
    public String remove(BoardDTO dto, RedirectAttributes redirectAttributes) {
        Long bno = dto.getBno();
        boardService.remove(bno);
        List<String> fileNames = dto.getFileNames();
        if (fileNames != null && fileNames.size() > 0) {
        }
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files) {
        for (String fileName : files) {
    
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();
            try{
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<BoardDTO> responseDTO = boardService.searchList(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "board/list";
    }
}
