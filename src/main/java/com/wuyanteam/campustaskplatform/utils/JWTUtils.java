package com.wuyanteam.campustaskplatform.utils;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

public class JWTUtils {
    @Value("${JwT.expire}")
    private static long expire;
    @Value("${JwT.secret}")
    private static String secret;

    //生成token
    public static String generateToken(String uname){
        Date now=new Date();
        Date expiration=new Date(now.getTime()+expire*1000);
        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setSubject(uname)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    public static Claims getClaimsByToken(String token){//从token获得claim
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }


    public static boolean verifyToken(String token) {//检测token是否合法
        try {
            // 解析JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            // 检查过期时间
            Date expiration = claims.getExpiration();
            Date now = new Date();
            if (expiration.before(now)) {
                return false; // 令牌已过期
            }
            return true; // 令牌验证通过
        } catch (Exception e) {
            // 捕获异常，如签名不匹配、令牌格式错误等
            return false;
        }
    }
}


