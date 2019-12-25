package run.foam.app.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;


@Data
@ToString(callSuper = true)
public class UserRegister {

    /**
     * 用户账号
     */
    @NotEmpty(message = "用户名不能为空！")
    @Length(min = 6, max = 20, message = "用户名长度必须在6~20位之间")
    private String username;

    /**
     * 用户昵称
     */
    @NotEmpty(message = "用户昵称不能为空！")
    @Length(min = 0, max = 20, message = "用户昵称长度必须在0~20位之间")
    private String nickname;

    /**
     * 用户密码
     */
    @NotEmpty(message = "密码不能为空！")
    @Length(min = 6, max = 20, message = "密码长度必须在6~20位之间")
    private String password;
}
