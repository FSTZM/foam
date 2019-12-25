package run.foam.app.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PostCategoryVo {

    private Long id;
    private String categoryName;
    private String description;
    private Long parentId;
    private Long postNum; //文章数
}
