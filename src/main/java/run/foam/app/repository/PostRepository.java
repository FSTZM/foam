package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.Post;
import run.foam.app.model.vo.PostTitleVo;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {

    @Query(value = "select count(*) from tb_post where category_id = ?1",nativeQuery = true)
    Long findPostNumByCateogryId(Long id);

    @Query(value = "select * from tb_post where category_id = ?1",nativeQuery = true)
    List<Post> findPostByCateogryId(Long id);

    @Query(value = "select count(*) from tb_post where tag_id1 = ?1 or tag_id2 = ?1 or tag_id3 = ?1 or tag_id4 = ?1 or tag_id5 = ?1",nativeQuery = true)
    Long findPostNumByTagId(Long id);

    @Query(value = "select * from tb_post where tag_id1 = ?1",nativeQuery = true)
    List<Post> findPostByTagId1(Long id);

    @Query(value = "select * from tb_post where tag_id2 = ?1",nativeQuery = true)
    List<Post> findPostByTagId2(Long id);

    @Query(value = "select * from tb_post where tag_id3 = ?1",nativeQuery = true)
    List<Post> findPostByTagId3(Long id);

    @Query(value = "select * from tb_post where tag_id4 = ?1",nativeQuery = true)
    List<Post> findPostByTagId4(Long id);

    @Query(value = "select * from tb_post where tag_id5 = ?1",nativeQuery = true)
    List<Post> findPostByTagId5(Long id);

    @Query(value = "select * from tb_post where file_title like ?1",nativeQuery = true)
    List<Post> findPostByTitle(String title);

    @Query(value = "select * from tb_post order by date desc",nativeQuery = true)
    List<Post> findPostOrderByDesc();

    @Query(value = "select * from tb_post where tag_id1 = ?1 or tag_id2 = ?1 or tag_id3 = ?1 or tag_id4 = ?1 or tag_id5 = ?1 order by date desc",nativeQuery = true)
    List<Post> findPostOrderByTagIdDesc(Long tagId);

    @Query(value = "select count(*) from tb_post where category_id = ?1",nativeQuery = true)
    Integer computePostNumByCategoryId(Long cgId);

    @Query(value = "select * from tb_post order by top asc, date desc",nativeQuery = true)
    List<Post> findAllByOrder();

    @Query(value = "select * from tb_post order by update_time desc limit 5",nativeQuery = true)
    List<Post> findPostRecordByUpdateTime();
}
