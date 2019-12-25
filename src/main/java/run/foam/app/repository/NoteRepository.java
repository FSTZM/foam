package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    @Query(value = "select * from tb_note order by create_time desc",nativeQuery = true)
    List<Note> findAllOrderByCreateTime();
}
