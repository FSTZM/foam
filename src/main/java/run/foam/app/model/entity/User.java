package run.foam.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@Table(name = "tb_user")
@ToString(callSuper = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 用户账号
     */
    @Column(name = "username", columnDefinition = "varchar(50) not null")
    private String username;

    /**
     * 用户昵称
     */
    @Column(name = "nickname", columnDefinition = "varchar(255) not null")
    private String nickname;

    /**
     * 用户密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", columnDefinition = "varchar(255) not null")
    private String password;

    /**
     * 用户邮箱
     */
    @Column(name = "email", columnDefinition = "varchar(127) default ''")
    private String email;

    /**
     * 用户头像
     */
    @Column(name = "avatar", columnDefinition = "varchar(1023) default ''")
    private String avatar;

    /**
     * 用户描述
     */
    @Column(name = "description", columnDefinition = "varchar(1023) default ''")
    private String description;

    /**
     * 创建时间
     */
    @Column(name = "created")
    private Date created;

    /**
     * 密码的盐值
     */
    @JsonIgnore
    @Column(name = "salt", columnDefinition = "varchar(1023) default ''")
    private String salt;

    /**
     * 电话
     */
    @Column(name = "phone", columnDefinition = "varchar(300) default ''")
    private String phone;
}
