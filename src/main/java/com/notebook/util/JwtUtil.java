package com.notebook.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;
import java.util.Map;

/**
 * Project: course_mall
 * File: JwtUtil
 *
 * @author evan
 * @date 2020/10/27
 */
public final class JwtUtil {
    public static String encryptData(String audience, String secret, String claimKey, String claimValue) {
        return JWT.create().withAudience(audience).withClaim(claimKey, claimValue).sign(Algorithm.HMAC256(secret));
    }

    public static String tryDecodeToken(String token) {
        try {
            return JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static boolean tryVerifyToekn(String token, Algorithm algorithm) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
