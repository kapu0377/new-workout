package org.zerock.workoutproject.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.workoutproject.main.repository.MainRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainService {

    private final MainRepository mainRepository;

    // 테이블 이름과 프라이머리 키 매핑
    private static final Map<String, String> TABLE_PRIMARY_KEYS = Map.of(
            "exercise", "eno",       // 일반 게시판 테이블과 프라이머리 키
            "board", "bno"   // 공지사항 테이블과 프라이머리 키
//            "free_board", "free_id"        // 자유 게시판 테이블과 프라이머리 키
    );

    /**
     * 모든 게시판에서 최신 게시물 가져오기
     * @return 게시판별 최신 게시물 Map
     */
    public Map<String, List<Map<String, Object>>> getLatestPostsFromAllBoards() {
        Map<String, List<Map<String, Object>>> latestPosts = new HashMap<>();

        // 각 테이블에서 최신 게시물 가져오기
        for (Map.Entry<String, String> entry : TABLE_PRIMARY_KEYS.entrySet()) {
            String tableName = entry.getKey();
            String primaryKey = entry.getValue();
            latestPosts.put(tableName, mainRepository.getLatestPosts(tableName, primaryKey, 3));
        }

        return latestPosts;
    }
}
