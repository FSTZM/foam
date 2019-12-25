package run.foam.app.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import run.foam.app.model.entity.User;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenUtils {


    //生成不含有自定义信息的token
    public static void createToken() {

        String secret = "secret";// token 密钥
        Algorithm algorithm = Algorithm.HMAC256("secret");

        // 头部信息
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        Date nowDate = new Date();
        Date expireDate = new Date(System.currentTimeMillis()+1000*60*60);//设置token过期时间

        String token = JWT.create()
                .withHeader(map)// 设置头部信息 Header
                .withIssuer("SERVICE")//设置 载荷 签名是有谁生成 例如 服务器
                .withSubject("this is test token")//设置 载荷 签名的主题
                // .withNotBefore(new Date())//设置 载荷 定义在什么时间之前，该jwt都是不可用的.
                .withAudience("APP")//设置 载荷 签名的观众 也可以理解谁接受签名的
                .withIssuedAt(nowDate) //设置 载荷 生成签名的时间
                .withExpiresAt(expireDate)//设置 载荷 签名过期的时间
                .sign(algorithm);//签名 Signature
    }

    //生成含自定义信息的token
    public static String createTokenWithChineseClaim(String secret, User user) {

        //Map<String, Object> map = new HashMap<String, Object>();
        //map.put("alg", "HS256");
        //map.put("typ", "JWT");

        String token = JWT.create()
                //.withHeader(map)
                /* 设置 载荷 Payload (即自定义信息)*/
                .withClaim("id", user.getId())
                .withClaim("userName", user.getUsername())
                .withIssuer("SERVICE")// 签名是有谁生成 例如 服务器
                //.withSubject("this is test token")// 签名的主题
                // .withNotBefore(new Date())//定义在什么时间之前，该jwt都是不可用的
                //.withAudience("APP")// 签名的观众 也可以理解谁接受签名的
                .withIssuedAt(new Date()) // 生成签名的时间
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60))// 签名过期的时间
                /* 签名 Signature */
                .sign(Algorithm.HMAC256(secret));//secret是秘钥  Algorithm.HMAC256为签名算法

        return token;
    }

    //验证token
    public static DecodedJWT verifyToken(String token,String secret) {

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withIssuer("SERVICE").build(); // Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);

        //获取token中的信息
        /*String subject = jwt.getSubject();
        List<String> audience = jwt.getAudience();
        Map<String, Claim> claims = jwt.getClaims();
        for (Map.Entry<String, Claim> entry : claims.entrySet()) {
            String key = entry.getKey();
            Claim claim = entry.getValue();
            log.info("key:" + key + " value:" + claim.asString());
        }
        Claim claim = claims.get("loginName");

        log.info(claim.asString());
        log.info(subject);
        log.info(audience.get(0));*/

        return jwt;
    }

}
