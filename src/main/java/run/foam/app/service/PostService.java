package run.foam.app.service;


import org.springframework.web.multipart.MultipartFile;
import run.foam.app.model.dto.*;
import run.foam.app.model.entity.Post;
import run.foam.app.model.entity.PostCategory;
import run.foam.app.model.vo.*;

import java.util.List;

public interface PostService {
    String uploadPicture(MultipartFile file,String bucket,String fileTitle);

    Post savePost(PostTemporary postTemporary);

    void saveCategory(PostCategoryTemporary postCategoryTemporary);

    void updateCategory(PostCategoryTemporary postCategoryTemporary);

    PageResult<PostCategoryVo> findAllCategory(int currentPage, int pageSize);

    List<PostCategory> findCategoryOptions();

    PostCategoryEditVo findCategoryById(Long id);

    void deleteCategoryById(Long id);

    void saveTag(PostTagTemporary postTagTemporary);

    void updateTag(PostTagTemporary postTagTemporary);

    List<PostTagVo> findAllTag();

    void deleteTagById(Long id);

    PageResult<PostTableVo> findAllPostByFactor(Integer currentPage, Integer pageSize, String prop, String order, String title, Integer state, List<Long> category);

    void recover(Long id,int targetState);

    void deletePostById(Long id);

    PostVo findPostById(Long id);

    void deletePostByIds(List<Long> ids);

    void recover2(List<Long> list);

    void recover3(List<Long> list);
}
