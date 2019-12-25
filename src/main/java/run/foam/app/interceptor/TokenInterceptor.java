package run.foam.app.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import run.foam.app.config.FilterProperties;
import run.foam.app.exception.enums.ExceptionEnum;
import run.foam.app.exception.exception.CustomizeRuntimeException;
import run.foam.app.model.entity.User;
import run.foam.app.util.CookieUtils;
import run.foam.app.util.TokenUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableConfigurationProperties(FilterProperties.class)
public class TokenInterceptor implements HandlerInterceptor {

    private FilterProperties filterProperties;

    private String secret;

    public TokenInterceptor(FilterProperties filterProperties, String secret) {
        this.filterProperties = filterProperties;
        this.secret = secret;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 获取请求的URL路径
        String path = request.getRequestURI();
        // 判断是否在白名单内 如果在 则放行
        if (isAllowPath(path)) {
            return true;
        }

        // 解析token -- 解析token要先获得cookie
        String token = request.getHeader("token");
        System.out.println(token);
        System.out.println("得到token"+token);
            if (StringUtils.isEmpty(token)){
                throw new CustomizeRuntimeException(ExceptionEnum.TOKEN_NO_EXIST);
            }

        //校验token
        try {
            User user = new User();

            DecodedJWT jwt = TokenUtils.verifyToken(token, secret);

            Map<String, Claim> claims = jwt.getClaims();

            for (Map.Entry<String, Claim> entry : claims.entrySet()) {

                String key = entry.getKey();
                Claim claim = entry.getValue();
                if ("id".equals(key)) {
                    user.setId(claim.asLong());
                } else if ("userName".equals(key)) {
                    user.setUsername(claim.asString());
                }
            }
            // 刷新token并重新写入
            String newToken = TokenUtils.createTokenWithChineseClaim(secret, user);
            //CookieUtils.newBuilder(response).httpOnly().request(request).build("Admin_Token", newToken);
            response.setHeader("Access-Control-Expose-Headers","Cache-Control,Content-Type,Expires,Pragma,Content-Language,Last-Modified,token");
            response.setHeader("token", token); //设置响应头
            return true;
        } catch (Exception e) {
            throw new CustomizeRuntimeException(ExceptionEnum.TOKEN_VERIFY_ERROR);
        }

    }

    private boolean isAllowPath(String path) {

        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths) {
            if (path.startsWith(allowPath)) {
                return true;
            }
        }
        return false;
    }
}
