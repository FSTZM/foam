package run.foam.app.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import run.foam.app.exception.enums.ExceptionEnum;
import run.foam.app.exception.exception.CustomizeRuntimeException;
import run.foam.app.model.entity.FileInfo;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.HandleRecord;
import run.foam.app.model.entity.UploadInfo;
import run.foam.app.repository.DashboardRepository;
import run.foam.app.repository.FileRepository;
import run.foam.app.repository.UploadRepository;
import run.foam.app.service.UploadService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private UploadRepository uploadRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    /**
     * 创建阿里云OSS仓库
     *
     * @param bucketName
     */
    @Transactional
    @Override
    public void createAliyunStorage(String bucketName) {

        OSS ossClient = getOSSClient();
        try {
            // 判断Bucket是否存在。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
            if (ossClient.doesBucketExist(bucketName)) {
                System.out.println("您已经创建Bucket：" + bucketName + "。");
                throw new CustomizeRuntimeException(ExceptionEnum.STORAGE_ALREADY_EXIST);
            } else {
                System.out.println("您的Bucket不存在，创建Bucket：" + bucketName + "。");
                // 创建Bucket。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
                // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
                ossClient.createBucket(bucketName);
            }

            // 查看Bucket信息。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
            BucketInfo info = ossClient.getBucketInfo(bucketName);
            System.out.println("Bucket " + bucketName + "的信息如下：");
            System.out.println("\t数据中心：" + info.getBucket().getLocation());
            System.out.println("\t创建时间：" + info.getBucket().getCreationDate());
            System.out.println("\t用户标志：" + info.getBucket().getOwner());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }


    @Transactional
    @Override
    public void uploadFile(MultipartFile file, String bucket, String address) {

        OSS ossClient = getOSSClient();

        try {
            String address2 = address.trim();

            if (!address2.equals("") && address2.startsWith("/")) {
                address2 = address2.substring(1);
            }

            if (!address2.equals("") && !address2.endsWith("/")) {

                address2 = address2 + "/";
            }

            String objectName = address2 + file.getOriginalFilename();
            //上传文件
            ossClient.putObject(bucket, objectName, file.getInputStream());
            UploadInfo info = findUploadInfo(1L);
            //可以用来下载的url
            String url = "https://" + bucket + "." + info.getEndpoint() + "/" + objectName;
            //可以在markdown用来展示图片的url
            String showUrl = "![](" + url + ")";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date();
            String str = sdf.format(d);


            FileInfo fileInfo = new FileInfo();
            fileInfo.setUrl(url);
            fileInfo.setShowUrl(showUrl);
            fileInfo.setStorage(1L);
            fileInfo.setUploadTime(str);
            fileInfo.setFileName(file.getOriginalFilename());
            fileInfo.setAddress(address2);
            fileInfo.setBucket(bucket);
            FileInfo save = fileRepository.save(fileInfo);

            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(save.getId());
            handleRecord.setMessage("上传文件");
            handleRecord.setTime(str);
            handleRecord.setType(7);
            handleRecord.setType2(1);
            dashboardRepository.save(handleRecord);

        } catch (IOException e) {
            throw new CustomizeRuntimeException(ExceptionEnum.FILE_UPLOAD_ERROR);
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }

    }


    /**
     * 查找阿里云OSS储存仓库
     *
     * @return
     */
    @Override
    public Map<String, String> findAllAliyunResposity() {

        Map<String, String> map = new HashMap<>();
        OSS ossClient = getOSSClient();
        try {
            // 列举存储空间。
            List<Bucket> buckets = ossClient.listBuckets();
            for (Bucket bucket : buckets) {
                map.put(bucket.getName(), bucket.getName());
            }
        }catch (Exception e){
            throw new CustomizeRuntimeException(ExceptionEnum.STORAGE_FIND_FAIL);
        }finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return map;
    }

    /**
     * 查询文件上传相关配置
     *
     * @param storage
     * @return
     */
    @Override
    public UploadInfo findUploadInfo(Long storage) {
        try {
            List<UploadInfo> info = uploadRepository.findByStorage(storage);
            if (!CollectionUtils.isEmpty(info)){
                UploadInfo uploadInfo = info.get(0);
                return uploadInfo;
            }else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存修改文件上传相关配置
     *
     * @param uploadInfo
     */
    @Transactional
    @Override
    public void saveUploadInfo(UploadInfo uploadInfo) {
        try {
            List<UploadInfo> infos = uploadRepository.findByStorage(uploadInfo.getStorage());
            if (!CollectionUtils.isEmpty(infos)) {
                uploadRepository.deleteAll(infos);
            }
            UploadInfo save = uploadRepository.save(uploadInfo);

            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(save.getId());
            handleRecord.setMessage("更新上传文件配置");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String str = sdf.format(date);
            handleRecord.setTime(str);
            handleRecord.setType(6);
            handleRecord.setType2(3);
            dashboardRepository.save(handleRecord);
        } catch (Exception e) {
            uploadRepository.save(uploadInfo);
        }
    }


    public String findAliyunLocation(String bucket) {

        OSS ossClient = getOSSClient();

        String location = ossClient.getBucketLocation(bucket);

        // 关闭OSSClient。
        ossClient.shutdown();

        return location;
    }

    /**
     * 获取阿里云OSSClient实例
     *
     * @return
     */
    public OSS getOSSClient() {

        try {
            UploadInfo uploadInfo = findUploadInfo(1L);
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(uploadInfo.getEndpoint(), uploadInfo.getAccessKeyId(), uploadInfo.getAccessKeySecret());
            return ossClient;
        } catch (Exception e) {
            throw new CustomizeRuntimeException(ExceptionEnum.INVALID_ALIYUN_OSS_MESSAGE_ERROR);
        }
    }

    /**
     * 查询所有上传过的文件的信息
     *
     * @return
     */
    @Override
    public PageResult<FileInfo> findAllFileInfo(Integer currentPage, Integer pageSize, String prop, String order, String search) {

        Specification<FileInfo> spec = new Specification<FileInfo>() {

            //使用匿名内部类的方式，创建一个Specification的实现类，并实现toPredicate方法
            @Override
            public Predicate toPredicate(Root<FileInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 姑且叫做 新建查询参数数组吧, 是多个查询条件组合的的数组
                List<Predicate> predicatesList = new ArrayList<>();
                // 当if成立 添加 匹配大于开始时间的查询参数
                if (!"".equals(search)) {
                    predicatesList.add(cb.like(root.get("fileName").as(String.class), "%" + search + "%"));
                }
                // 设置排序条件
                if (!"".equals(prop) && !"".equals(order)) {
                    if ("ascending".equals(order)) {
                        query.orderBy(cb.asc(root.get(prop)));
                    } else if ("descending".equals(order)) {
                        query.orderBy(cb.desc(root.get(prop)));
                    } else {
                        throw new CustomizeRuntimeException(ExceptionEnum.INVALID_TABLE_ORDER);
                    }
                } else {
                    query.orderBy(cb.desc(root.get("uploadTime")));
                }
                // 将排序与添加好的查询参数数组 作为返回值
                query.where(cb.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
                return query.getRestriction();
            }
        };

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<FileInfo> page = fileRepository.findAll(spec, pageable);
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        List<FileInfo> content = page.getContent();

        PageResult<FileInfo> pageResult = new PageResult<>();
        pageResult.setTotalPages(totalPages);
        pageResult.setTotalElements(totalElements);
        pageResult.setContent(content);
        return pageResult;
    }

    @Transactional
    @Override
    public void deleteFileInfoById(Long id) {
        FileInfo info = fileRepository.getOne(id);
        List<String> list = new ArrayList<>();
        String objectName = info.getAddress() + info.getFileName();
        list.add(objectName);
        aliyunDeleteFiles(list,info.getBucket());
        fileRepository.delete(info);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(0L);
        handleRecord.setMessage("删除上传文件");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(7);
        handleRecord.setType2(2);
        dashboardRepository.save(handleRecord);
    }

    @Transactional
    public List<String> aliyunDeleteFiles(List<String> keys,String bucket) {

        // 创建OSSClient实例。
        OSS ossClient = getOSSClient();

        // 删除文件。key等同于ObjectName，表示删除OSS文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。

        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(keys));
        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();//删除的文件列表

        // 关闭OSSClient。
        ossClient.shutdown();
        return deletedObjects;
    }

    @Transactional
    @Override
    public void deleteFileInfoByIds(List<Long> list) {
        for (Long id:list){
            deleteFileInfoById(id);
        }
    }

}
