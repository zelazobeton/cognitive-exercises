package com.zelazobeton.cognitiveexercises.controllers;

import static com.zelazobeton.cognitiveexercises.constant.SecurityConstants.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;
import com.zelazobeton.cognitiveexercises.HttpResponse;
import com.zelazobeton.cognitiveexercises.utility.JWTTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/token")
public class TokenController extends ExceptionHandling {
    public static final String TOKEN_SUCCESSFULLY_REFRESHED = "Token successfully refreshed";
    public static final String REFRESH_TOKEN_SUCCESSFULLY_DELETED = "Refresh token successfully deleted";

    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping(path = "/refresh", produces = { "application/json" })
    public ResponseEntity<HttpResponse> getNewAccessToken(@RequestBody String refreshToken) throws JWTVerificationException {
        String token = jwtTokenProvider.getNewAccessToken(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, token);
        return new ResponseEntity<>(new HttpResponse(OK, TOKEN_SUCCESSFULLY_REFRESHED), headers, HttpStatus.OK);
    }

    @PostMapping(path = "/delete", produces = { "application/json" })
    public ResponseEntity<HttpResponse> deleteRefreshToken(@RequestBody String refreshToken) throws JWTVerificationException {
        jwtTokenProvider.deleteRefreshToken(refreshToken);
        return new ResponseEntity<>(new HttpResponse(OK, REFRESH_TOKEN_SUCCESSFULLY_DELETED), HttpStatus.OK);
    }
}