package run.foam.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import run.foam.app.exception.enums.ExceptionEnum;
import run.foam.app.exception.exception.CustomizeRuntimeException;
import run.foam.app.model.dto.PostTemporary;
import run.foam.app.model.entity.About;
import run.foam.app.model.entity.Post;
import run.foam.app.model.entity.User;
import run.foam.app.model.dto.UserOne;
import run.foam.app.model.dto.UserRegister;
import run.foam.app.model.dto.UserTwo;
import run.foam.app.model.vo.MessageVo;
import run.foam.app.model.vo.PostVo;
import run.foam.app.service.UserService;
import run.foam.app.util.CookieUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${foam.jwt.secret}")
    private String secret;

    /**
     * 用户注册
     *
     * @param userRegister
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegister userRegister, BindingResult result) {
        if (result.hasErrors()) {//BindingResult 如果有错抛出异常继续执行
            throw new RuntimeException(result.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }// 进行自定义错误|为分隔符
        userService.register(userRegister.getUsername(), userRegister.getPassword(), userRegister.getNickname());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("check")
    public ResponseEntity<Integer> checkUser() {
        Integer num = userService.checkUser();
        return ResponseEntity.ok(num);
    }

    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestBody User user,// 无需给前端浏览器返回token，token只有后端才需要使用，并且将token保存到cookie中
                                      HttpServletResponse response,
                                      HttpServletRequest request) {
        // 登录功能的实现
        String token = userService.login(user.getUsername(), user.getPassword());
        // 将token写入cookie --- 工厂模式
        // httpOnly()：避免别的js代码来操作你的cookie，是一种安全措施
        // charset(): 不需要编码 因为token中没有中文
        // maxAge()： cookie的生命周期，默认是-1，代表一次会话，浏览器关闭cookie就失效
        // response: 将cookie写入 --- response中有一个方法 addCookie()
        // request: cookie中有域的概念 domain 例如一个cookie只能在www.baidu.com生效，无法在别的域下生效
        // 给cookie绑定一个域，防止别的网站访问你的cookie，也是一种安全措施
        //CookieUtils.newBuilder(response).httpOnly().request(request).build("Admin_Token", token);
        response.setHeader("Access-Control-Expose-Headers","Cache-Control,Content-Type,Expires,Pragma,Content-Language,Last-Modified,token");
        response.setHeader("token", token); //设置响应头

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

/*    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Admin_Token")) {
                cookie.setValue(null);
                cookie.setMaxAge(0);// 立即销毁cookie
                cookie.setPath("/");
                response.addCookie(cookie);
                break;
            }
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }*/

/*    @GetMapping("verify")
    public ResponseEntity<Void> verify(
            @CookieValue("Admin_Token") String token,
            HttpServletResponse response,
            HttpServletRequest request) {

        String newToken = userService.verify(token);

        CookieUtils.newBuilder(response).httpOnly().request(request).build("Admin_Token", newToken);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }*/

    @PutMapping("update/user1")
    public ResponseEntity<Void> updateUserInfoOne(@Valid @RequestBody UserOne user1, BindingResult result) {

        if (result.hasErrors()) {//BindingResult 如果有错抛出异常继续执行
            throw new RuntimeException(result.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }// 进行自定义错误|为分隔符
        userService.updateUserInfoOne(user1.getId(),user1.getUsername(), user1.getNickname(), user1.getPhone(), user1.getEmail(), user1.getDescription());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("update/user2")
    public ResponseEntity<Void> updateUserInfoTwo(@Valid @RequestBody UserTwo userTwo, BindingResult result) {
        if (result.hasErrors()) {//BindingResult 如果有错抛出异常继续执行
            throw new RuntimeException(result.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("|")));
        }// 进行自定义错误|为分隔符
        if (!userTwo.getPassword().equals(userTwo.getPassword2())) {
            throw new CustomizeRuntimeException(ExceptionEnum.INVALID_PASSWORD_TWICE);
        }
        userService.updateUserInfoTwo(userTwo.getId(),userTwo.getOldPassword(), userTwo.getPassword());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("find/user/info")
    public ResponseEntity<User> findUserInfo() {
        return ResponseEntity.ok(userService.findUserInfo());
    }

    @PutMapping("avatar")
    public ResponseEntity<Void> changeAvatar(@RequestParam("avatar") String avatar) {
        userService.changeAvatar(avatar);
        return ResponseEntity.ok().build();
    }

    @PostMapping("save/about")
    public ResponseEntity<Void> saveAbout(@RequestBody PostTemporary postTemporary) {
        userService.saveAbout(postTemporary);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("update/about")
    public ResponseEntity<Void> updateAbout(@RequestBody PostTemporary postTemporary) {

        userService.saveAbout(postTemporary);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("about/find")
    public ResponseEntity<PostVo> findAbout() {
        return ResponseEntity.ok(userService.findAbout());
    }

    @GetMapping("message")
    public ResponseEntity<MessageVo> findMessage() {
        return ResponseEntity.ok(userService.findMessage());
    }
}
