package run.foam.app.model.dto;

import lombok.Data;

@Data
public class NoteDTO {

    private Long id;

    private String createTime;

    private String content;

    private String updateTime;

}