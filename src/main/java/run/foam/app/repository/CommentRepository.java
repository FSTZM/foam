package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.foam.app.model.entity.CommentInfo;

public interface CommentRepository extends JpaRepository<CommentInfo, Long>, JpaSpecificationExecutor<CommentInfo> {


}
