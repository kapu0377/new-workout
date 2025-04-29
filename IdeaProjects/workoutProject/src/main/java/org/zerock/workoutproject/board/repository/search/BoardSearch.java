package org.zerock.workoutproject.board.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.workoutproject.board.domain.Board;
import org.zerock.workoutproject.board.dto.BoardListAllDTO;
import org.zerock.workoutproject.board.dto.BoardListReplyCountDTO;

public interface BoardSearch {
    Page<Board> searchAll(String keyword, Pageable pageable);
    Page<BoardListReplyCountDTO> searchWithReplyCount(String keyword, Pageable pageable);
    Page<BoardListAllDTO> searchWithAll(String keyword, Pageable pageable);
}
