package run.foam.app.model.dto;

import lombok.Data;
import run.foam.app.model.entity.FileInfo;

import java.util.List;

@Data
public class PageResult<T> {

    private int totalPages;
    private long totalElements;
    private List<T> content;
}
