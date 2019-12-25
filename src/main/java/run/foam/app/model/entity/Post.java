package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private int state; //1草稿 2发布 3回收站

    private String resposity;
    private String storage;

    private String fileTitle;
    @Column(name = "html", columnDefinition = "varchar(65535) default ''")
    private String html;
    @Column(name = "markDown", columnDefinition = "varchar(65535) default ''")
    private String markDown;
    private String date;
    private int comment; //是否开启评论 1是2否
    private int top; //是否置顶 1是2否
    private String description;
    private String imgUrl;
    private String createdTime;
    private String updateTime;

    private Long commentNum;//评论数
    private Long accessNum;//访问数
    private Long categoryId;//所属分类
    private Long tagId1;//所属标签1
    private Long tagId2;//所属标签2
    private Long tagId3;//所属标签3
    private Long tagId4;//所属标签4
    private Long tagId5;//所属标签5
}
