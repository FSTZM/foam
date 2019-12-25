package run.foam.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.foam.app.model.entity.FileInfo;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.UploadInfo;
import run.foam.app.model.dto.UploadInfoTemporary;
import run.foam.app.service.UploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/file")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 创建阿里云OSS存储空间
     *
     * @param bucketName
     * @return
     */
    @PostMapping("/aliyun/storage")
    public ResponseEntity<Void> createAliyunStorage(@RequestParam("bucketName") String bucketName) {

        uploadService.createAliyunStorage(bucketName);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/aliyun/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("address") String address,
                                           @RequestParam("resposity") String bucket) {

        uploadService.uploadFile(file,bucket,address);
        return ResponseEntity.ok().build();

    }

    /**
     * 根据存储地址查找该地址下的所有仓库
     *
     * @return
     */
    @GetMapping("/aliyun/resposity")
    public ResponseEntity<Map<String, String>> findAllRespositiesById() {

        return ResponseEntity.ok(uploadService.findAllAliyunResposity());
    }

    /**
     * 查询上传配置信息
     * @return
     */
    @GetMapping("/upload/info/find")
    public ResponseEntity<UploadInfo> findUploadInfo(@RequestParam("storage") Long storage) {

        return ResponseEntity.ok(uploadService.findUploadInfo(storage));
    }

    /**
     * 保存上传配置信息
     * @param uploadInfoTemporary
     * @return
     */
    @PutMapping("/upload/info/save")
    public ResponseEntity<Void> saveUploadInfo(@RequestBody UploadInfoTemporary uploadInfoTemporary) {

        UploadInfo uploadInfo = new UploadInfo();

        uploadInfo.setStorage(uploadInfoTemporary.getStorage());
        uploadInfo.setEndpoint(uploadInfoTemporary.getEndPoint().trim());
        uploadInfo.setAccessKeyId(uploadInfoTemporary.getAccessKeyId().trim());
        uploadInfo.setAccessKeySecret(uploadInfoTemporary.getAccessKeySecret().trim());
        uploadService.saveUploadInfo(uploadInfo);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*currentPage: this.pageInfo.currentPage, // 当前页
    pageSize: this.pageInfo.pageSize, // 每页条数
    prop: this.pageInfo.prop, // 排序字段
    order: this.pageInfo.order, // 是否降序
    search: this.pageInfo.search // 查询字段*/
    @GetMapping("/info/find")
    public ResponseEntity<PageResult<FileInfo>> findAllFileInfo(@RequestParam("currentPage") Integer currentPage,
                                                      @RequestParam("pageSize") Integer pageSize,
                                                      @RequestParam("prop") String prop,
                                                      @RequestParam("order") String order,
                                                      @RequestParam("search") String search){

        return ResponseEntity.ok(uploadService.findAllFileInfo(currentPage,pageSize,prop,order,search));
    }

    @DeleteMapping("/info/delete/{id}")
    public ResponseEntity<List<FileInfo>> deleteFileInfoById(@PathVariable("id") Long id){
        uploadService.deleteFileInfoById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/info/delete/ids")
    public ResponseEntity<List<FileInfo>> deleteFileInfoByIds(@RequestBody List<FileInfo> infos){
        List<Long> list = new ArrayList<>();
        for (FileInfo fileInfo:infos){
            list.add(fileInfo.getId());
        }
        uploadService.deleteFileInfoByIds(list);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
