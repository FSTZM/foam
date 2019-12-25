package run.foam.app.service;

import org.springframework.web.multipart.MultipartFile;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.FileInfo;
import run.foam.app.model.entity.UploadInfo;

import java.util.List;
import java.util.Map;

public interface UploadService {
    void createAliyunStorage(String bucketName);

    void uploadFile(MultipartFile file, String bucket, String address);

    Map<String,String> findAllAliyunResposity();

    UploadInfo findUploadInfo(Long storage);

    void saveUploadInfo(UploadInfo uploadInfo);


    void deleteFileInfoById(Long id);

    PageResult<FileInfo> findAllFileInfo(Integer currentPage, Integer pageSize, String prop, String order, String search);

    List<String> aliyunDeleteFiles(List<String> keys,String bucket);

    void deleteFileInfoByIds(List<Long> list);
}
