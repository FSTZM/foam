package run.foam.app.model.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Data
public class PostVo {

    private Long id;

    private int state; //1草稿 2发布 3回收站

    private String resposity;
    private String storage;

    private String fileTitle;
    private String html;
    private String markDown;
    private String date;
    private String comment; //是否开启评论 1是2否
    private String top; //是否置顶 1是2否
    private String description;
    private String imgUrl;

    private List<Long> category;//所属分类
    private List<Long> tags;//所属标签
}
