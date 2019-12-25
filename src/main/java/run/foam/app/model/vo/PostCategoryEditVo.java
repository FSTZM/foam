package run.foam.app.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PostCategoryEditVo {

    private Long id;
    private String categoryName;
    private String description;
    private List<Long> parentId;
    private Long postNum; //文章数
}
