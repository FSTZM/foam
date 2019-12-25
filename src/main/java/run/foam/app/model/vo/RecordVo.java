package run.foam.app.model.vo;

import lombok.Data;
import run.foam.app.model.entity.HandleRecord;

import java.util.List;

@Data
public class RecordVo {

    List<HandleRecord> handleList;
    List<CommentVo> commentList;
    List<PostTitleVo> postList;
}
