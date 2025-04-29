package org.zerock.workoutproject.main.repository;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

@Repository
public class MainRepository {

    private final JdbcTemplate jdbcTemplate;

    public MainRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 특정 테이블에서 최신 게시물 가져오기
     * @param tableName 테이블 이름
     * @param primaryKey 프라이머리 키 이름
     * @param limit 가져올 게시물 수
     * @return 최신 게시물 리스트
     */
    public List<Map<String, Object>> getLatestPosts(String tableName, String primaryKey, int limit) {
        String sql = String.format(
                "SELECT t.%s AS id, t.title, t.content, t.url, t.regdate, CONCAT('s_', i.uuid, '_', i.file_name) AS url2 " +
                        "FROM %s t " +
                        "LEFT JOIN %s_image i ON t.%s = i.%s_%s " +
                        "GROUP BY t.%s " +
                        "ORDER BY t.regdate DESC " +
                        "LIMIT ?",
                primaryKey, tableName,tableName, primaryKey, tableName ,primaryKey,primaryKey
        );
        return jdbcTemplate.queryForList(sql, limit);
    }
}