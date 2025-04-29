package org.zerock.workoutproject.board.service;

import org.zerock.workoutproject.board.domain.Board;
import org.zerock.workoutproject.board.dto.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface BoardService {
    Long register(BoardDTO dto);
    BoardDTO readOne(Long bno);
    Long modify(BoardDTO dto);
    void remove(Long bno);
    PageResponseDTO<BoardDTO> searchList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);
    // 최근 게시물 가져오기
    List<BoardDTO> getRecentPosts(int count);
    // 인기 게시물 가져오기
    BoardDTO getPopularPost();
    List<ViewCountDTO> getAllViewCounts();
    int increaseViewCount(Long bno);
    default Board dtoToEntity(BoardDTO boardDTO){
        Board board = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .view(boardDTO.getView())
                .build();
        if(boardDTO.getFileNames() != null){
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_",2);
                board.addImage(arr[0], arr[1]);
            });
        }
        return board;
    }
    default BoardDTO entityToDto(Board board) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .view(board.getView())
                .build();
        // 두 필드 모두 BoardDTO에 필요해보여서
        // view와 writer 속성을 모두 포함하도록 수정했습니다.
        List<String> fileNames = board.getImageSet().stream().sorted()
                .map(boardImage -> boardImage.getUuid()+"_"+boardImage.getFileName())
                .collect(Collectors.toList());
        boardDTO.setFileNames(fileNames);
        return boardDTO;
    }


}
