package run.foam.app.model.dto;

import lombok.Data;
import run.foam.app.model.entity.PostCategory;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

@Data
public class PostTemporary {

    private Long id;
    private String state; //1草稿 2发布 3回收站
    private String resposity;
    private String storage;

    private String fileTitle;

    private String html;
    private String markDown;
    private String date;
    private String comment;
    private String top;
    private String description;
    private String imgUrl;

    private List<Long> category;
    private List<Long> tags;
}
