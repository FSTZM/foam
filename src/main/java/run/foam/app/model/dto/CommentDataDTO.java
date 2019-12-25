package run.foam.app.model.dto;

import cn.leancloud.AVObject;
import lombok.Data;
import run.foam.app.model.vo.CommentVo;

import java.util.List;

@Data
public class CommentDataDTO {
    private Integer Commentcount;
    private List<CommentVo> resultList;
}
