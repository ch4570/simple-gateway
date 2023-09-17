package com.example.userservice.controller;

import com.example.userservice.dto.request.LoginDto;
import com.example.userservice.dto.response.ResponseDto;
import com.example.userservice.utils.AesUtils;
import com.example.userservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUtils jwtUtils;
    private final Environment env;
    private final AesUtils aesUtils;

    @GetMapping("/post/{postId}")
    public ResponseEntity<ResponseDto> getPost(@PathVariable("postId") String postId,
                                               @RequestHeader("X-Authorization-Email") String userEmail) {
      log.info("postId = {}", postId);
      String decodeEmail = aesUtils.decodeString(userEmail);
      log.info("userEmail = {}", decodeEmail);

      return ResponseEntity.ok(new ResponseDto(postId, decodeEmail));
    }

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        log.info("No Token for this request");
        log.info("jwt.secret = {}", env.getProperty("jwt.secret"));
        return ResponseEntity.ok("No Token for this request");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String token = jwtUtils.generateToken(loginDto.getEmail());
        return ResponseEntity.ok(aesUtils.enCodeString(token));
    }
}
