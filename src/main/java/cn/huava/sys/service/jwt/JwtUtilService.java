package cn.huava.sys.service.jwt;

import cn.hutool.v7.json.jwt.JWT;
import cn.hutool.v7.json.jwt.JWTUtil;

/**
 * Wrapper service for JWT utilities to allow for easier testing
 */
public class JwtUtilService {
    
    public boolean verify(String token, byte[] key) {
        return JWTUtil.verify(token, key);
    }
    
    public JWT parseToken(String token) {
        return JWTUtil.parseToken(token);
    }
}