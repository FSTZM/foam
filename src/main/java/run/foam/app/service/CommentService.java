package run.foam.app.service;

import run.foam.app.model.dto.CommentInfoDTO;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.vo.CommentInfoVo;
import run.foam.app.model.vo.CommentVo;

import java.util.List;

public interface CommentService {
    Integer getCommentCount(Long postId);

    void saveCommentInfo(CommentInfoDTO commentInfoDTO);

    void updateCommentInfo(CommentInfoDTO commentInfoDTO);

    CommentInfoVo findCommentInfo();

    PageResult<CommentVo> findCommentByfactor(Integer currentPage, Integer pageSize, String prop, String order, String keyword);

    void deleteCommentById(String id);

    void deleteCommentByIds(List<String> ids);
}
