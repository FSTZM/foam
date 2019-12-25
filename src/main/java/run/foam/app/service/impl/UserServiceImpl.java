package run.foam.app.service.impl;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import run.foam.app.exception.enums.ExceptionEnum;
import run.foam.app.exception.exception.CustomizeRuntimeException;
import run.foam.app.model.dto.PostTemporary;
import run.foam.app.model.entity.*;
import run.foam.app.model.vo.MessageVo;
import run.foam.app.model.vo.PostVo;
import run.foam.app.repository.AboutRepository;
import run.foam.app.repository.DashboardRepository;
import run.foam.app.repository.PostRepository;
import run.foam.app.repository.UserRepository;
import run.foam.app.service.CommentService;
import run.foam.app.service.UserService;
import run.foam.app.util.CodecUtils;
import run.foam.app.util.TokenUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AboutRepository aboutRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Value("${foam.jwt.secret}")
    private String secret;

    /**
     * 用户注册
     * @param username
     * @param password
     * @param nickname
     */
    @Override
    @Transactional
    public void register(String username, String password, String nickname) {
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        // 对密码进行加密
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(password, salt));

        // 写入数据库
        user.setCreated(new Date());
        User save = userRepository.save(user);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("用户注册");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(1);
        handleRecord.setType2(1);
        dashboardRepository.save(handleRecord);
    }

    @Override
    public Integer checkUser() {
        List<User> users = userRepository.findAll();
        if (!CollectionUtils.isEmpty(users) && users.size() >= 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String login(String username, String password) {

        try {
            // 校验用户名和密码
            User user = userRepository.findUser(username);
            if (user == null) {
                throw new CustomizeRuntimeException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            // 校验密码
            if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password, user.getSalt()))) {
                throw new CustomizeRuntimeException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            // 生成token
            String token = TokenUtils.createTokenWithChineseClaim(secret, user);

            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(user.getId());
            handleRecord.setMessage("用户登录");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String str = sdf.format(date);
            handleRecord.setTime(str);
            handleRecord.setType(1);
            handleRecord.setType2(4);
            dashboardRepository.save(handleRecord);
            return token;
        } catch (Exception e) {
            log.error("[授权中心] 用户名或者密码有误，用户名称：{}", username, e);
            throw new CustomizeRuntimeException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    /**
     * 刷新token
     *
     * @param token
     * @return
     *//*
    @Transactional
    @Override
    public String verify(String token) {
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
            return newToken;
        } catch (Exception e) {
            throw new CustomizeRuntimeException(ExceptionEnum.TOKEN_VERIFY_ERROR);
        }
    }*/

    /**
     * 更新个人设置
     *
     * @param username
     * @param nickname
     * @param email
     * @param message
     */
    @Transactional
    @Override
    public void updateUserInfoOne(Long id, String username, String nickname, String phone, String email, String message) {
        User user = userRepository.getOne(id);
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setEmail(email);
        user.setDescription(message);
        User save = userRepository.save(user);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("更新资料");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(1);
        handleRecord.setType2(3);
        dashboardRepository.save(handleRecord);
    }

    @Transactional
    @Override
    public void updateUserInfoTwo(Long id, String oldPassword, String password) {

        User user = userRepository.getOne(id);
        // 校验密码
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(oldPassword, user.getSalt()))) {
            throw new CustomizeRuntimeException(ExceptionEnum.INVALID_PASSWORD);
        }
            user.setPassword(CodecUtils.md5Hex(password, user.getSalt()));
        User save = userRepository.save(user);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("密码更改");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(1);
        handleRecord.setType2(3);
        dashboardRepository.save(handleRecord);
    }

    @Override
    public User findUserInfo() {
        User user = userRepository.findAll().get(0);

        return user;
    }

    /**
     * 更换头像
     * @param circleUrl
     */
    @Transactional
    @Override
    public void changeAvatar(String circleUrl) {
        User user = userRepository.findAll().get(0);
        user.setAvatar(circleUrl);
        User save = userRepository.save(user);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("更换头像");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(1);
        handleRecord.setType2(3);
        dashboardRepository.save(handleRecord);
    }

    /**
     * 保存关于我信息
     *
     * @param postTemporary
     */
    @Transactional
    @Override
    public void saveAbout(PostTemporary postTemporary) {
        About about = new About();
        about.setId(postTemporary.getId());
        about.setStorage(postTemporary.getStorage());
        about.setResposity(postTemporary.getResposity());
        about.setFileTitle(postTemporary.getFileTitle());
        about.setHtml(postTemporary.getHtml());
        about.setMarkDown(postTemporary.getMarkDown());
        about.setComment(Integer.parseInt(postTemporary.getComment()));
        about.setAccessNum(0L);
        about.setCommentNum(0L);
        About save = aboutRepository.save(about);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("更新【关于】页面");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(4);
        handleRecord.setType2(3);
        dashboardRepository.save(handleRecord);
    }

    @Override
    public PostVo findAbout() {
        List<About> all = aboutRepository.findAll();

            PostVo postVo = new PostVo();
        if (!CollectionUtils.isEmpty(all)){
            About about = all.get(0);
            postVo.setId(about.getId());
            postVo.setResposity(about.getResposity());
            postVo.setStorage(about.getStorage());
            postVo.setFileTitle(about.getFileTitle());
            postVo.setHtml(about.getHtml());
            postVo.setMarkDown(about.getMarkDown());
            postVo.setComment(String.valueOf(about.getComment()));
        }

        return postVo;
    }

    @Override
    public MessageVo findMessage() {
        MessageVo messageVo = new MessageVo();
        int postNum = postRepository.findAll().size();
        Integer commentCount = commentService.getCommentCount(null);
        List<Post> all = postRepository.findAll();
        int totalAccessNum = 0;
        for (Post post:all){
            totalAccessNum += post.getAccessNum();
        }
        messageVo.setCommentCount(commentCount);
        messageVo.setPostNum(postNum);
        messageVo.setCreatedDate(userRepository.findAll().get(0).getCreated());
        messageVo.setTotalAccessNum(totalAccessNum);

        return messageVo;
    }

}
