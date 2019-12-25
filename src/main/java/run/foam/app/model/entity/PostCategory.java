package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tb_post_category")
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long parentId;

    private String categoryName;

    private String description;

    @Transient
    private Integer num;

    @Transient
    private List<PostCategory> children;

}
