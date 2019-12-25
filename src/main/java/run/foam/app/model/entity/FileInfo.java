package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "tb_file_info")
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "storage")
    private Long storage;//储存位置 1阿里云

    @Column(name = "uploadtime", columnDefinition = "varchar(1000) default ''")
    private String uploadTime;
    @Column(name = "filename", columnDefinition = "varchar(1000) default ''")
    private String fileName;
    @Column(name = "address", columnDefinition = "varchar(1000) default ''")
    private String address;//路径

    @Column(name = "url", columnDefinition = "varchar(1000) default ''")
    private String url;//下载链接
    @Column(name = "showUrl", columnDefinition = "varchar(1000) default ''")
    private String showUrl;//markdown可使用链接

    @Column(name = "bucket", columnDefinition = "varchar(1000) default ''")
    private String bucket;//保存文件所在的仓库
}
