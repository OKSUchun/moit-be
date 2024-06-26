package com.sparta.moit.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.moit.domain.member.controller.docs.MemberControllerDocs;
import com.sparta.moit.domain.member.dto.MemberResponseDto;
import com.sparta.moit.domain.member.service.KakaoService;
import com.sparta.moit.domain.member.service.MemberService;
import com.sparta.moit.domain.member.service.NaverService;
import com.sparta.moit.global.common.dto.ResponseDto;
import com.sparta.moit.global.jwt.JwtUtil;
import com.sparta.moit.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    /* 카카오 로그인 */
    @GetMapping("/signin/kakao")
    public ResponseEntity<ResponseDto<MemberResponseDto>> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        MemberResponseDto responseDto = kakaoService.kakaoLogin(code);
        /*accessToken, refreshToken cookie 추가*/
        jwtUtil.addAccessTokenToCookie(responseDto.getAccessToken(), response);
        jwtUtil.addRefreshTokenToCookie(responseDto.getRefreshToken(), response);

        return ResponseEntity.ok().body(ResponseDto.success("카카오 로그인 완료", responseDto));

    }

    /* 네이버 로그인 */
    @GetMapping("/signin/naver")
    public ResponseEntity<ResponseDto<MemberResponseDto>> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        MemberResponseDto responseDto = naverService.naverLogin(code, state);
        jwtUtil.addAccessTokenToCookie(responseDto.getAccessToken(), response);
        jwtUtil.addRefreshTokenToCookie(responseDto.getRefreshToken(), response);
        return ResponseEntity.ok().body(ResponseDto.success("네이버 로그인 완료", responseDto));
    }

    /* 회원탈퇴 */
    @DeleteMapping("/signout")
    public ResponseEntity<ResponseDto<String>> signOut(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.signOut(userDetails.getUser());
        return ResponseEntity.ok().body(ResponseDto.success("회원 탈퇴 완료", "회원 탈퇴가 완료되었습니다."));
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        String token = kakaoService.login();
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/refresh-test")
    public ResponseEntity<String> refreshTest() {
        String token = kakaoService.refreshTest();
        return ResponseEntity.ok().body(token);
    }
}