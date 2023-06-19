package com.practice.growth.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Log4j2
public class JwtUtils {

    private static final String AUTHORITIES_KEY = "auth";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";

    public static final Long accessTokenValidMillisecond = 24 * 60 * 60 * 1000L; // 1일
    //private final Long accessTokenValidMillisecond = 60 * 1000L; // 1분
    public static final Long refreshTokenValidMillisecond = 365 * 24 * 60 * 60 * 1000L; // 1년
}
