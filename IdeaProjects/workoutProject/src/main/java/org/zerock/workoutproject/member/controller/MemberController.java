package org.zerock.workoutproject.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.workoutproject.member.dto.MemberDTO;
import org.zerock.workoutproject.member.repository.MemberRepository;
import org.zerock.workoutproject.member.service.MemberService;
import org.zerock.workoutproject.security.check.CheckIdValidaotr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes({"loginMember"})
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final CheckIdValidaotr checkIdValidaotr;

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @Autowired
    private MemberRepository memberRepository;


    @GetMapping("/join")
    public void join(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("memberDTO") MemberDTO dto,
                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                       BindingResult bindingResult, Model model,
                       RedirectAttributes redirectAttributes) throws MemberService.MidExistException {
        if (bindingResult.hasErrors()) {
            log.info("error");
            return "member/join";
        }
        
        try {
            // 프로필 이미지가 있는 경우 처리
            if (profileImage != null && !profileImage.isEmpty()) {
                String originalName = profileImage.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, "profiles", uuid + "_" + originalName);
                
                // profiles 디렉토리가 없으면 생성
                Files.createDirectories(savePath.getParent());
                
                // 원본 이미지 저장
                profileImage.transferTo(savePath);

                // 썸네일 생성 (200x200)
                File thumbFile = new File(uploadPath + File.separator + "profiles" + File.separator + "s_" + uuid + "_" + originalName);
                Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);

                // DTO에 이미지 정보 설정
                dto.setProfileImageName(originalName);
                dto.setProfileImageUuid(uuid);
            }

            log.info("member: " + dto);
            dto.setEmail(dto.getMid());
            memberService.join(dto);
            return "redirect:/member/login";
            
        } catch (IOException e) {
            log.error("프로필 이미지 저장 중 오류 발생: ", e);
            redirectAttributes.addFlashAttribute("error", "프로필 이미지 저장에 실패했습니다.");
            return "member/join";
        }
    }


    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        
        if (referer != null && !referer.contains("/member/login")) {
            request.getSession().setAttribute("url", referer);
        }
        
        return "member/login";
    }

    @PostMapping("/login")
    public String login(MemberDTO dto, HttpSession session, Model model) {
        MemberDTO memberDTO = memberService.login(dto);
        if (memberDTO != null) {
            session.setAttribute("loginInfo", memberDTO);
            return "redirect:/";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다");
            return "member/login"; // 로그인 화면으로 돌아가면서 오류 메시지 전달
        }
    }

    //   @GetMapping("/logout")
//    public String logout(HttpSession session){
//       session.removeAttribute("loginInfo");
//       session.invalidate();
//       return "redirect:/";
//   }
    @PreAuthorize("principal.username == #memberDTO.mid")
    @PostMapping("/remove")
    public String remove(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        try {
            memberService.remove(memberDTO);  // 회원 삭제 처리
            redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/logout";  // 메인 페이지로 리다이렉트
    }


    @GetMapping("/mypage")
    public String memberInfo(Principal principal, Model model) {
        String loginId = principal.getName();
        MemberDTO member = memberService.findById(loginId);
        model.addAttribute("member", member);
        return "member/mypage";
    }

    @PostMapping("/mypage")
    public String modify(@ModelAttribute MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        if (memberDTO == null || memberDTO.getMid() == null) {
            redirectAttributes.addFlashAttribute("error", "회원 정보가 유효하지 않습니다.");
            return "redirect:/member/mypage";
        }
        int result = memberService.updateInfo(memberDTO);
        if (result > 0) {
            redirectAttributes.addFlashAttribute("message", "회원정보 수정 완료.");


        }
        return "redirect:/";
    }
}


