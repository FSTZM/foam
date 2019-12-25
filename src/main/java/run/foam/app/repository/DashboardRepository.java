package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.HandleRecord;

import java.util.List;

public interface DashboardRepository extends JpaRepository<HandleRecord, Long>, JpaSpecificationExecutor<HandleRecord> {

    @Query(value = "select * from tb_handle_record order by time desc limit 5",nativeQuery = true)
    List<HandleRecord> findRecordByTime();
}
