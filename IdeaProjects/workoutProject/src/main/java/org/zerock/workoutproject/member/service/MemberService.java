package org.zerock.workoutproject.member.service;


import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Errors;
import org.zerock.workoutproject.member.dto.MemberDTO;
import org.zerock.workoutproject.security.dto.MemberSecurityDTO;


import java.util.Map;


public interface MemberService {

    MemberDTO login(MemberDTO memberDTO);

    void join(MemberDTO memberDTO) throws MidExistException;

    void remove(MemberDTO dto);

    MemberDTO findById(String memberDTO);

    int updateInfo(@Valid MemberDTO memberupdateDTO);

    boolean midCheck(String mid);

    void updateProfileImage(String username, String originalName, String uuid);

    class MidExistException extends Exception {
    }
}
