package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_upload_info")
public class UploadInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "storage")
    private Long storage;

    @Column(name = "endpoint", columnDefinition = "varchar(100) default ''")
    private String endpoint;
    @Column(name = "accessKeyId", columnDefinition = "varchar(100) default ''")
    private String accessKeyId;
    @Column(name = "accessKeySecret", columnDefinition = "varchar(100) default ''")
    private String accessKeySecret;
}
