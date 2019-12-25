package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.UploadInfo;
import run.foam.app.model.entity.User;

import java.util.List;

public interface UploadRepository extends JpaRepository<UploadInfo,Long> {

    @Query(value = "from UploadInfo where storage = ?1")
    List<UploadInfo> findByStorage(Long storage);
}
