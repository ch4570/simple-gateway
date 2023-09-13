package com.example.userservice.controller;

import com.example.userservice.dto.request.LoginDto;
import com.example.userservice.dto.response.ResponseDto;
import com.example.userservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUtils jwtUtils;

    @GetMapping("/post/{postId}")
    public ResponseEntity<ResponseDto> getPost(@PathVariable("postId") String postId,
                                               @RequestHeader("X-Authorization-Email") String userEmail) {
      log.info("postId = {}", postId);
      log.info("userEmail = {}", userEmail);

      return ResponseEntity.ok(new ResponseDto(postId, userEmail));
    }

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        log.info("No Token for this request");

        return ResponseEntity.ok("No Token for this request");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(jwtUtils.generateToken(loginDto.getEmail()));
    }
}
