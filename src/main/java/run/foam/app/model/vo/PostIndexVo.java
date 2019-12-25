package run.foam.app.model.vo;

import lombok.Data;

import java.util.Map;

@Data
public class PostIndexVo {


    private Long id;

    private int state; //1草稿 2发布 3回收站

    private String resposity;
    private String storage;

    private String fileTitle;
    private String html;
    private String markDown;
    private String date;
    private int comment; //是否开启评论 1是2否
    private int top; //是否置顶 1是2否
    private String description;
    private String imgUrl;

    private Long commentNum;//评论数
    private Long accessNum;//访问数
    private Map<Long,String> categories;//所属分类
    private Map<Long,String> tags;//所属标签1
}