package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.FileInfo;
import run.foam.app.model.entity.PostTag;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Query(value = "from PostCategory where postId = ?1")
    List<PostTag> findByPostId(Long postId);

    @Query(value = "from PostTag where tagName = ?1")
    PostTag findByTagName(String tagName);
}
