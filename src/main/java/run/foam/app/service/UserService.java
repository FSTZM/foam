package run.foam.app.service;

import run.foam.app.model.dto.PostTemporary;
import run.foam.app.model.entity.About;
import run.foam.app.model.entity.UploadInfo;
import run.foam.app.model.entity.User;
import run.foam.app.model.vo.MessageVo;
import run.foam.app.model.vo.PostVo;

import java.util.Map;

public interface UserService {

    void register(String username, String password, String nickname);

    Integer checkUser();

    String login(String username, String password);

    //String verify(String token);

    void updateUserInfoOne(Long id, String username, String nickname, String phone, String email, String message);

    void updateUserInfoTwo(Long id, String oldPassword, String password);

    User findUserInfo();

    void changeAvatar(String circleUrl);

    void saveAbout(PostTemporary postTemporary);

    PostVo findAbout();

    MessageVo findMessage();
}
