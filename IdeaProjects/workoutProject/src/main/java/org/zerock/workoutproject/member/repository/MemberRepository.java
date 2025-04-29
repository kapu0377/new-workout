package org.zerock.workoutproject.member.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.workoutproject.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    @Query("SELECT m FROM Member m WHERE m.mid=:id AND m.mpw=:pw")
    Member login(@Param("id") String id, @Param("pw") String pw);

    @EntityGraph(attributePaths = "roleSet")
    @Query("SELECT m FROM Member m WHERE m.mid = :mid")
    Optional<Member> getWithRoles(@Param("mid")String mid);

    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.mid = :mid")
    void remove(@Param("mid") String mid);

    boolean existsByMid(String mid);



}

