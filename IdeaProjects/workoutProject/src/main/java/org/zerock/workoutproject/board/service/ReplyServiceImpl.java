package org.zerock.workoutproject.board.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.board.domain.BoardReply;
import org.zerock.workoutproject.board.dto.PageRequestDTO;
import org.zerock.workoutproject.board.dto.PageResponseDTO;
import org.zerock.workoutproject.board.dto.ReplyDTO;
import org.zerock.workoutproject.board.repository.ReplyRepository;
import org.zerock.workoutproject.member.domain.Member;
import org.zerock.workoutproject.member.domain.MemberRole;
import org.zerock.workoutproject.member.dto.MemberDTO;
import org.zerock.workoutproject.member.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    private final MemberService memberService;

    @Override
    public Long register(ReplyDTO replyDTO) {
        String replyerId = replyDTO.getReplyer();
        MemberDTO memberDTO = memberService.findById(replyerId);
        if (memberDTO == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }


        Member member = modelMapper.map(memberDTO, Member.class);

        boolean flag = checkAdmin(member);
        System.out.println("Replyer: " + replyerId + ", isAdmin: " + flag);

        BoardReply boardReply = modelMapper.map(replyDTO, BoardReply.class);
        boardReply.setFlag(replyDTO.isFlag());


        Long rno = replyRepository.save(boardReply).getRno();
        return rno;
    }

    @Override
    public void remove(Long rno) {
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListofBoard(Long bno, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <=0? 0: pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(), Sort.by("rno").ascending());
        Page<BoardReply> result = replyRepository.listOfBoard(bno,pageable);
        List<ReplyDTO> dtoList = result.getContent().stream().map(boardReply -> modelMapper.map(boardReply, ReplyDTO.class)).collect(Collectors.toList());
        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
        BoardReply boardReply = replyRepository.findById(replyDTO.getRno()).orElseThrow();
        boardReply.changeText(replyDTO.getReplyText());
        replyRepository.save(boardReply);
    }

    @Override
    public boolean checkAdmin(Member member) {
        return member.getRoleSet().contains(MemberRole.ADMIN);

    }

}
