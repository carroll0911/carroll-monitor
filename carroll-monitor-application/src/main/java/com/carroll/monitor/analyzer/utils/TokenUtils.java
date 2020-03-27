package com.carroll.monitor.analyzer.utils;


import com.carroll.auth.entity.TokenMessage;
import com.carroll.auth.user.UserTokenUtils;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.util.Date;

/**
 * @author: carroll
 * @date 2019/9/9
 *
 */
public class TokenUtils extends UserTokenUtils {

    public static TokenMessage createToken(String userId, String phone) throws JoseException, IOException {
        JsonWebEncryption jwe = getJWE();
        JwtClaims claims;
        (claims = new JwtClaims()).setClaim("userId", userId);
        claims.setClaim("phone", phone);
        claims.setClaim("tokenCreatedTime", (new Date()).getTime());
        jwe.setPayload(claims.toJson());
        TokenMessage tokenMessage;
        (tokenMessage = new TokenMessage()).setToken(jwe.getCompactSerialization());
        return tokenMessage;
    }
}
