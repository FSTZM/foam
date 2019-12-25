package run.foam.app.model.dto;

import lombok.Data;

@Data
public class PostTableDTO {

    private Long id;

    private int state; //1草稿 2发布 3回收站

    private String fileTitle;
    private String date;

    private Long commentNum;//评论数
    private Long accessNum;//访问数
    private String category;//所属分类
    private String tag1;//所属标签1
    private String tag2;//所属标签2
    private String tag3;//所属标签3
    private String tag4;//所属标签4
    private String tag5;//所属标签5
}