package run.foam.app.service.impl;


import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.core.AVOSCloud;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import run.foam.app.exception.enums.ExceptionEnum;
import run.foam.app.exception.exception.CustomizeRuntimeException;
import run.foam.app.model.dto.CommentDataDTO;
import run.foam.app.model.dto.CommentInfoDTO;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.About;
import run.foam.app.model.entity.CommentInfo;
import run.foam.app.model.entity.HandleRecord;
import run.foam.app.model.entity.Post;
import run.foam.app.model.vo.CommentInfoVo;
import run.foam.app.model.vo.CommentVo;
import run.foam.app.model.vo.PostTitleVo;
import run.foam.app.repository.AboutRepository;
import run.foam.app.repository.CommentRepository;
import run.foam.app.repository.DashboardRepository;
import run.foam.app.repository.PostRepository;
import run.foam.app.service.CommentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AboutRepository aboutRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    /**
     * 统计文章的评论数
     * @param postId
     * @return
     */
    public Integer getCommentCount(Long postId){

        List<CommentInfo> all = commentRepository.findAll();
        if (!CollectionUtils.isEmpty(all)){
            CommentInfo commentInfo = all.get(0);
        AVOSCloud.initialize(commentInfo.getAppID(), commentInfo.getAppKey());

        final CommentDataDTO commentDataDTO = new CommentDataDTO();
        AVQuery<AVObject> query = new AVQuery<>("Comment");

        if (postId!=null){
            query.whereEqualTo("url", "/index/post?id="+postId);
        }
        query.countInBackground().subscribe(new Observer<Integer>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(Integer count) {
                commentDataDTO.setCommentcount(count);

            }
            public void onError(Throwable throwable) {
                commentDataDTO.setCommentcount(0);
            }
            public void onComplete() {
            }
        });
        return commentDataDTO.getCommentcount();
        }else {
            return 0;
        }
    }

    /**
     * 按条件查询获取查询结果
     * @return
     */
    @Override
    public PageResult<CommentVo> findCommentByfactor(Integer currentPage, Integer pageSize, String prop, String order, String keyword){
        List<CommentInfo> all = commentRepository.findAll();
        if (!CollectionUtils.isEmpty(all)) {
            CommentInfo commentInfo = all.get(0);
            AVOSCloud.initialize(commentInfo.getAppID(), commentInfo.getAppKey());
            final CommentDataDTO commentDataDTO = new CommentDataDTO();

            AVQuery<AVObject> query = new AVQuery<>("Comment");

            if (StringUtils.isNotEmpty(keyword)) {
                AVQuery<AVObject> query2 = new AVQuery<>("Comment");
                query2.whereContains("comment", keyword);
                AVQuery<AVObject> query3 = new AVQuery<>("Comment");
                query3.whereContains("nick", keyword);
                AVQuery<AVObject> query4 = new AVQuery<>("Comment");
                query4.whereContains("mail", keyword);

                query = AVQuery.or(Arrays.asList(query2, query3, query4));
            }

            // 设置排序条件
            if (!"".equals(prop) && !"".equals(order)) {
                if ("ascending".equals(order)) {
                    query.orderByAscending(prop);
                } else if ("descending".equals(order)) {
                    query.orderByDescending(prop);
                } else {
                    throw new CustomizeRuntimeException(ExceptionEnum.INVALID_TABLE_ORDER);
                }
            } else {
                query.orderByDescending("insertedAt");
            }
            //分页
            query.limit(pageSize);
            query.skip((currentPage - 1) * pageSize);//跳过前N条

            query.findInBackground().subscribe(new Observer<List<AVObject>>() {
                public void onSubscribe(Disposable disposable) {
                }

                public void onNext(List<AVObject> objs) {

                    List<CommentVo> list = new ArrayList<>();
                    // students 是包含满足条件的 Student 对象的数组
                    for (AVObject avObject : objs) {
                        CommentVo commentVo = new CommentVo();
                        commentVo.setObjectId(avObject.getString("objectId"));
                        commentVo.setComment(avObject.getString("comment"));
                        commentVo.setInsertedAt(avObject.getDate("insertedAt"));
                        commentVo.setIp(avObject.getString("ip"));
                        commentVo.setLink(avObject.getString("link"));
                        commentVo.setMail(avObject.getString("mail"));
                        commentVo.setNick(avObject.getString("nick"));
                        commentVo.setPid(avObject.getString("pid"));
                        commentVo.setRid(avObject.getString("rid"));
                        commentVo.setUa(avObject.getString("ua"));
                        commentVo.setUrl(avObject.getString("url"));
                        String[] urls = avObject.getString("url").split("\\?id=");

                        if (urls.length > 1) {
                            if (Integer.valueOf(urls[1]) == -1) {
                                About about = aboutRepository.getOne(Long.valueOf(urls[1]));
                                PostTitleVo postTitleVo = new PostTitleVo();
                                postTitleVo.setId(about.getId());
                                postTitleVo.setTitle(about.getFileTitle());
                                commentVo.setPostTitleVo(postTitleVo);
                            } else {
                                Post post = postRepository.getOne(Long.valueOf(urls[1]));
                                PostTitleVo postTitleVo = new PostTitleVo();
                                postTitleVo.setId(post.getId());
                                postTitleVo.setDescription(post.getDescription());
                                postTitleVo.setTitle(post.getFileTitle());
                                commentVo.setPostTitleVo(postTitleVo);
                            }
                        }

                        list.add(commentVo);
                    }
                    commentDataDTO.setResultList(list);
                }

                public void onError(Throwable throwable) {
                    commentDataDTO.setResultList(null);
                }

                public void onComplete() {
                }
            });

            PageResult<CommentVo> pageResult = new PageResult<>();
            Integer total = getCommentCount(null);
            pageResult.setTotalElements(total);
            pageResult.setContent(commentDataDTO.getResultList());
            pageResult.setTotalPages((total - 1) / pageSize + 1);
            return pageResult;
        }else {
            return null;
        }
    }

    /**
     * 删除评论
     * @param id
     */
    @Transactional
    @Override
    public void deleteCommentById(String id) {
        CommentInfo commentInfo = commentRepository.findAll().get(0);
        AVOSCloud.initialize(commentInfo.getAppID(), commentInfo.getAppKey());
        AVOSCloud.setMasterKey(commentInfo.getMasterKey());
        AVObject todo = AVObject.createWithoutData("Comment", id);
        todo.delete();

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(0L);
        handleRecord.setMessage("删除评论");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(3);
        handleRecord.setType2(2);
        dashboardRepository.save(handleRecord);
    }

    /**
     * 批量删除评论
     * @param ids
     */
    @Transactional
    @Override
    public void deleteCommentByIds(List<String> ids) {
        CommentInfo commentInfo = commentRepository.findAll().get(0);
        AVOSCloud.initialize(commentInfo.getAppID(), commentInfo.getAppKey());
        AVOSCloud.setMasterKey(commentInfo.getMasterKey());
        List<AVObject> list = new ArrayList<>();
        for (String id:ids){
            AVObject todo = AVObject.createWithoutData("Comment", id);
            list.add(todo);
        }
        try {
            AVObject.deleteAll(list);
        } catch (AVException e) {
            throw new CustomizeRuntimeException(ExceptionEnum.COMMENT_DELETE_FAIL);
        }

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(0L);
        handleRecord.setMessage("删除评论");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(3);
        handleRecord.setType2(2);
        dashboardRepository.save(handleRecord);
    }

    /**
     * 保存评论配置信息
     * @param commentInfoDTO
     */
    @Transactional
    @Override
    public void saveCommentInfo(CommentInfoDTO commentInfoDTO) {
            CommentInfo commentInfo = new CommentInfo();
            commentInfo.setAppID(commentInfoDTO.getAppID());
            commentInfo.setAppKey(commentInfoDTO.getAppKey());
            commentInfo.setMasterKey(commentInfoDTO.getMasterKey());
        CommentInfo save = commentRepository.save(commentInfo);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("保存评论配置");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(5);
        handleRecord.setType2(1);
        dashboardRepository.save(handleRecord);
    }

    /**
     * 修改评论配置信息
     * @param commentInfoDTO
     */
    @Transactional
    @Override
    public void updateCommentInfo(CommentInfoDTO commentInfoDTO) {
        commentRepository.deleteAll();
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setAppID(commentInfoDTO.getAppID());
        commentInfo.setAppKey(commentInfoDTO.getAppKey());
        commentInfo.setMasterKey(commentInfoDTO.getMasterKey());
        CommentInfo save = commentRepository.save(commentInfo);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(save.getId());
        handleRecord.setMessage("修改评论配置");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(5);
        handleRecord.setType2(3);
        dashboardRepository.save(handleRecord);
    }

    /**
     * 查找评论配置信息
     * @return
     */
    @Override
    public CommentInfoVo findCommentInfo() {
        List<CommentInfo> all = commentRepository.findAll();
        CommentInfoVo commentInfoVo = new CommentInfoVo();
        if (!CollectionUtils.isEmpty(all)){

            CommentInfo commentInfo = all.get(0);
            commentInfoVo.setId(commentInfo.getId());
            commentInfoVo.setAppID(commentInfo.getAppID());
            commentInfoVo.setAppKey(commentInfo.getAppKey());
            commentInfoVo.setMasterKey(commentInfo.getMasterKey());
        }
        return commentInfoVo;
    }
}
