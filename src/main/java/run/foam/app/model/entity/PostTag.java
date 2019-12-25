package run.foam.app.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_post_tag")
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String tagName;

    private String description;
}
