package run.foam.app.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PostTitleVo {

    private Long id;
    private String title;
    private String description;
    private String time;
}
