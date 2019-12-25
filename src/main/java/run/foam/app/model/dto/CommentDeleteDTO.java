package run.foam.app.model.dto;

import lombok.Data;
import run.foam.app.model.vo.PostTitleVo;

import java.util.Date;

@Data
public class CommentDeleteDTO {

    private String objectId;
    private String comment;
    private Date insertedAt;
    private String ip;
    private String link;
    private String mail;
    private String nick;
    private String pid;
    private String rid;
    private String ua;
    private String url;

    private PostTitleVo postTitleVo;
}