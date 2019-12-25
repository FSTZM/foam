package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_about")
public class About {

    @Id
    @Column(name = "id")
    private Long id;

    private String resposity;
    private String storage;

    private String fileTitle;
    @Column(name = "html", columnDefinition = "varchar(65535) default ''")
    private String html;
    @Column(name = "markDown", columnDefinition = "varchar(65535) default ''")
    private String markDown;
    private int comment; //是否开启评论 1是2否

    private Long commentNum;//评论数
    private Long accessNum;//访问数
}
