package run.foam.app.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostCategoryTemporary {

    private Long id;
    private String categoryName;
    private String description;
    private List<Long> parentId;
}
