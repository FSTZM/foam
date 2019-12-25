package run.foam.app.model.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;


@Data
@ToString(callSuper = true)
public class UserTwo {

    private Long id;

    /**
     * 用户密码
     */
    @NotEmpty(message = "密码不能为空！")
    @Length(min = 6, max = 20, message = "密码长度必须在6~20位之间")
    private String oldPassword;

    @NotEmpty(message = "密码不能为空！")
    @Length(min = 6, max = 20, message = "密码长度必须在6~20位之间")
    private String password;

    @NotEmpty(message = "密码不能为空！")
    @Length(min = 6, max = 20, message = "密码长度必须在6~20位之间")
    private String password2;
}
