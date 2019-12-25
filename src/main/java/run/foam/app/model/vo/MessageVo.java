package run.foam.app.model.vo;


import lombok.Data;

import java.util.Date;

@Data
public class MessageVo {
    private Integer postNum;
    private Integer commentCount;
    private Integer totalAccessNum;
    private Date createdDate;
}
