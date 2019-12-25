package run.foam.app.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVo {

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
