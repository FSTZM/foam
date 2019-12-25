package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_comment_info")
public class CommentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "appID", columnDefinition = "varchar(100) default ''")
    private String appID;
    @Column(name = "appKey", columnDefinition = "varchar(100) default ''")
    private String appKey;
    @Column(name = "masterKey", columnDefinition = "varchar(100) default ''")
    private String masterKey;
}

