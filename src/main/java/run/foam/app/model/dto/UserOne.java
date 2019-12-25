package run.foam.app.model.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@ToString(callSuper = true)
public class UserOne {

    private Long id;

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
     * 用户邮箱
     */
    private String email;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户描述
     */
    private String description;


    /**
     * 电话
     */
    private String phone;
}
