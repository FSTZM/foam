package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.FileInfo;
import run.foam.app.model.entity.PostCategory;

import java.util.List;
import java.util.Optional;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long>, JpaSpecificationExecutor<PostCategory> {

    @Query(value = "from PostCategory where postId = ?1")
    List<PostCategory> findByPostId(Long postId);

    PostCategory findByCategoryName(String categoryName);

    @Query(value = "from PostCategory where parentId = ?1")
    List<PostCategory> findByParentId(Long parentId);
}
