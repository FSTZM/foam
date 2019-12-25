package run.foam.app.model.dto;

import lombok.Data;

@Data
public class UploadInfoTemporary {

    private Long storage;
    private String endPoint;
    private String accessKeyId;
    private String accessKeySecret;
}
