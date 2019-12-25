package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import run.foam.app.model.entity.FileInfo;

import java.util.List;

public interface FileRepository extends JpaRepository<FileInfo, Long>, JpaSpecificationExecutor<FileInfo> {

    List<FileInfo> findByFileNameAndAddressAndBucket(String fileName, String address, String bucket);
}
