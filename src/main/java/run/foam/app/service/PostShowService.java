package run.foam.app.service;


import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.PostCategory;
import run.foam.app.model.entity.PostTag;
import run.foam.app.model.vo.*;

import java.util.List;
import java.util.Map;

public interface PostShowService {

    PageResult<PostIndexVo> findAllPostIndex(Integer currentPage, Integer pageSize, String title, Integer state, Long categoryId, Long tagId);


    List<PostTitleVo> findAllByTitle(String title);


    PostIndexVo findIndexPostById(Long id);

    List<PostCategory> findCategoryOptions();

    Map<String,List<PostTimelineVo>> findPostOfTimeline(Long tagId, Long categoryId);

    String findTagNameByTagId(Long tagId);

    String findTagNameByCategoryId(Long categoryId);

    List<PostTagVo> findTagOptions();

    void saveAccessNum(Long id);

    Map<Integer,Map<Long,String>> postLastAndNext(Long id);

    UserShowVo findUser();

    Integer getTotalAccessNum();
}
