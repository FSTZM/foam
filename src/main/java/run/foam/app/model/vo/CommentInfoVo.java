package run.foam.app.model.vo;

import lombok.Data;

import javax.persistence.Column;

@Data
public class CommentInfoVo {

    private Long id;
    private String appID;
    private String appKey;
    private String masterKey;
}
