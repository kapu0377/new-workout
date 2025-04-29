package org.zerock.workoutproject.qna.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.workoutproject.board.dto.ViewCountDTO;
import org.zerock.workoutproject.qna.domain.Qna;
import org.zerock.workoutproject.qna.domain.QnaReply;
import org.zerock.workoutproject.qna.dto.QnaDTO;
import org.zerock.workoutproject.qna.dto.QnaListDTO;
import org.zerock.workoutproject.qna.dto.QnaReplyDTO;
import org.zerock.workoutproject.qna.dto.QnaReplyListDTO;

import java.io.IOException;
import java.util.List;

@Service
public interface QnaService {
    QnaDTO register(QnaDTO dto, MultipartFile[] imageFiles);

    Qna hideQna(Long qno, boolean hide);

    Qna getQna(Long qno);

    Qna answeredQna(Long qno, boolean completed);

    List<Qna> getQnaList(QnaDTO dto);

    QnaReplyDTO convertToQnaReply(QnaReply reply);

    QnaReply convertToQnaReplyEntity(QnaReplyDTO replyDTO);

    Qna modifyQna(Long qno, QnaDTO dto);

    QnaReplyDTO createReply(QnaReplyDTO replyDTO);

    QnaReplyListDTO getRepliesWithPaging(Long qno, Long lastRno);

    void remove(Long qno);

    boolean canDelete(Long qno, UserDetails userDetails);

    Resource getImageAsResource(Long ino) throws IOException;

    Page<QnaListDTO>  getQnaList(int page, int size);
    long getTotalCount();
    long increaseViewCount(Long qno);

    List<ViewCountDTO> getAllViewCounts();
}


