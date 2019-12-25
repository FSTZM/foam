package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_handle_record")
public class HandleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String time;

    private String message;

    private Integer type; //1登录2文章3评论4About5评论设置信息6上传配置信息7文件信息8便签

    private Integer type2;//1增2删3改4查

    private Long cid;//操作对象id

}