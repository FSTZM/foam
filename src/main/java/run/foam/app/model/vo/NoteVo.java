package run.foam.app.model.vo;

import lombok.Data;

@Data
public class NoteVo {

    private Long id;

    private String createTime;

    private String content;

    private String updateTime;

}