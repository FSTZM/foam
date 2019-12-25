package run.foam.app.model.vo;

import lombok.Data;

@Data
public class PostTagVo {

    private Long id;
    private String tagName;
    private String description;
    private Long postNum; //文章数
}
