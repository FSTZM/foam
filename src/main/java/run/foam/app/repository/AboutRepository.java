package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.foam.app.model.entity.About;
import run.foam.app.model.entity.CommentInfo;

public interface AboutRepository extends JpaRepository<About, Long>, JpaSpecificationExecutor<About> {
}
