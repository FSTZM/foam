package run.foam.app.service.impl;

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
import run.foam.app.model.dto.*;
import run.foam.app.model.entity.*;
import run.foam.app.model.vo.*;
import run.foam.app.repository.*;
import run.foam.app.service.CommentService;
import run.foam.app.service.PostService;
import run.foam.app.service.UploadService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DashboardRepository dashboardRepository;

    /**
     * 上传文章缩略图
     *
     * @param file
     * @param bucket
     * @param fileTitle
     * @return
     */
    @Transactional
    @Override
    public String uploadPicture(MultipartFile file, String bucket, String fileTitle) {

        String address = "bolg/post/assets/" + fileTitle + "/";

        List<FileInfo> fileInfos = fileRepository.findByFileNameAndAddressAndBucket(file.getOriginalFilename(), address, bucket);

        if (!CollectionUtils.isEmpty(fileInfos)) {
            for (FileInfo fileInfo : fileInfos) {
                List<String> list = new ArrayList<>();
                String objectName = fileInfo.getAddress() + fileInfo.getFileName();
                list.add(objectName);
                uploadService.aliyunDeleteFiles(list, fileInfo.getBucket());
                fileRepository.delete(fileInfo);
            }
        }

        uploadService.uploadFile(file, bucket, address);

        FileInfo fileInfo = fileRepository.findByFileNameAndAddressAndBucket(file.getOriginalFilename(), address, bucket).get(0);

        return fileInfo.getUrl();
    }

    /**
     * 保存文章
     *
     * @param postTemporary
     */
    @Transactional
    @Override
    public Post savePost(PostTemporary postTemporary) {
        Post post;
        int a;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);

        if (postTemporary.getId() != null && !"".equals(postTemporary.getId())){
            post = postRepository.getOne(postTemporary.getId());
            a=1;
            post.setUpdateTime(str);
        }else {
            post = new Post();
            post.setId(null);
            a=2;
            post.setCreatedTime(str);
            post.setUpdateTime(str);
        }
        post.setState(Integer.parseInt(postTemporary.getState()));
        post.setStorage(postTemporary.getStorage());
        post.setResposity(postTemporary.getResposity());
        post.setFileTitle(postTemporary.getFileTitle());
        post.setHtml(postTemporary.getHtml());
        post.setMarkDown(postTemporary.getMarkDown());
        if (postTemporary.getDate() != null && !"".equals(postTemporary.getDate())) {
            post.setDate(postTemporary.getDate());
        } else {
            post.setDate(str);
        }
        post.setComment(Integer.parseInt(postTemporary.getComment()));
        post.setTop(Integer.parseInt(postTemporary.getTop()));
        post.setDescription(postTemporary.getDescription());
        post.setImgUrl(postTemporary.getImgUrl());
        if (postTemporary.getId() == null){
            post.setAccessNum(0L);
            post.setCommentNum(0L);
        }
        List<Long> categories = postTemporary.getCategory();
        int size = categories.size();
        if (size == 0) {
            post.setCategoryId(0L);
        } else {
            post.setCategoryId(categories.get(categories.size() - 1));
        }

        List<Long> tags = postTemporary.getTags();
        int size1 = tags.size();
        if (size1 > 5) {
            throw new CustomizeRuntimeException(ExceptionEnum.TAGS_NUM_ERROR);
        }
        int num = 5 - size1;
        if (num == 0) {
            post.setTagId1(tags.get(0));
            post.setTagId2(tags.get(1));
            post.setTagId3(tags.get(2));
            post.setTagId4(tags.get(3));
            post.setTagId5(tags.get(4));
        } else if (num == 1) {
            post.setTagId1(tags.get(0));
            post.setTagId2(tags.get(1));
            post.setTagId3(tags.get(2));
            post.setTagId4(tags.get(3));
            post.setTagId5(0L);
        } else if (num == 2) {
            post.setTagId1(tags.get(0));
            post.setTagId2(tags.get(1));
            post.setTagId3(tags.get(2));
            post.setTagId4(0L);
            post.setTagId5(0L);
        } else if (num == 3) {
            post.setTagId1(tags.get(0));
            post.setTagId2(tags.get(1));
            post.setTagId3(0L);
            post.setTagId4(0L);
            post.setTagId5(0L);
        } else if (num == 4) {
            post.setTagId1(tags.get(0));
            post.setTagId2(0L);
            post.setTagId3(0L);
            post.setTagId4(0L);
            post.setTagId5(0L);
        } else {
            post.setTagId1(0L);
            post.setTagId2(0L);
            post.setTagId3(0L);
            post.setTagId4(0L);
            post.setTagId5(0L);
        }
        Post save = postRepository.save(post);

        if (a==1){
            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(save.getId());
            handleRecord.setMessage("更新文章");
            handleRecord.setTime(str);
            handleRecord.setType(2);
            handleRecord.setType2(3);
            dashboardRepository.save(handleRecord);
        }else {
            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(save.getId());
            handleRecord.setMessage("保存文章");
            handleRecord.setTime(str);
            handleRecord.setType(2);
            handleRecord.setType2(1);
            dashboardRepository.save(handleRecord);
        }

        return save;
    }

    /**
     * 保存修改的分类信息
     *
     * @param postCategoryTemporary
     */
    @Transactional
    @Override
    public void saveCategory(PostCategoryTemporary postCategoryTemporary) {

        //判断分类名称是否存在
        PostCategory category1 = postCategoryRepository.findByCategoryName(postCategoryTemporary.getCategoryName());
        if (category1 != null) {
            throw new CustomizeRuntimeException(ExceptionEnum.POST_CATEGORY_EXIST);
        }
        PostCategory postCategory = new PostCategory();
        postCategory.setCategoryName(postCategoryTemporary.getCategoryName());
        postCategory.setDescription(postCategoryTemporary.getDescription());
        int size = postCategoryTemporary.getParentId().size();
        if (size == 0) {
            postCategoryTemporary.getParentId().add(0L);
            size = postCategoryTemporary.getParentId().size();
        }
        postCategory.setParentId(postCategoryTemporary.getParentId().get(size - 1));

        postCategoryRepository.save(postCategory);
    }

    @Transactional
    @Override
    public void updateCategory(PostCategoryTemporary postCategoryTemporary) {

        if (postCategoryTemporary.getId() != null && postCategoryRepository.getOne(postCategoryTemporary.getId()) != null) {
            PostCategory category = postCategoryRepository.getOne(postCategoryTemporary.getId());
            category.setCategoryName(postCategoryTemporary.getCategoryName());
            category.setDescription(postCategoryTemporary.getDescription());
            int size = postCategoryTemporary.getParentId().size();
            if (size == 0) {
                postCategoryTemporary.getParentId().add(0L);
                size = postCategoryTemporary.getParentId().size();
            }
            Long parentId = postCategoryTemporary.getParentId().get(size - 1);
            if (parentId - category.getId() == 0) {
                throw new CustomizeRuntimeException(ExceptionEnum.PARENT_ID_ERROR);
            }
            category.setParentId(parentId);

            postCategoryRepository.save(category);
        } else {
            throw new CustomizeRuntimeException(ExceptionEnum.UPDATE_ERROR);
        }
    }

    /**
     * 查询所有分类信息返回表数据
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<PostCategoryVo> findAllCategory(int currentPage, int pageSize) {
        Specification<PostCategory> spec = new Specification<PostCategory>() {

            //使用匿名内部类的方式，创建一个Specification的实现类，并实现toPredicate方法
            @Override
            public Predicate toPredicate(Root<PostCategory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 姑且叫做 新建查询参数数组吧, 是多个查询条件组合的的数组
                List<Predicate> predicatesList = new ArrayList<>();
                // 设置排序条件
                query.orderBy(cb.desc(root.get("categoryName")));
                // 将排序与添加好的查询参数数组 作为返回值
                query.where(cb.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
                return query.getRestriction();
            }
        };

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<PostCategory> page = postCategoryRepository.findAll(spec, pageable);
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        List<PostCategory> content = page.getContent();
        List<PostCategoryVo> content2 = new ArrayList<>();
        for (PostCategory postCategory : content) {
            PostCategoryVo postCategoryVo = new PostCategoryVo();
            postCategoryVo.setCategoryName(postCategory.getCategoryName());
            postCategoryVo.setDescription(postCategory.getDescription());
            postCategoryVo.setParentId(postCategory.getParentId());
            postCategoryVo.setId(postCategory.getId());
            Long num = postRepository.findPostNumByCateogryId(postCategory.getId());
            postCategoryVo.setPostNum(num);
            content2.add(postCategoryVo);
        }

        PageResult<PostCategoryVo> pageResult = new PageResult<>();
        pageResult.setTotalPages(totalPages);
        pageResult.setTotalElements(totalElements);
        pageResult.setContent(content2);
        return pageResult;
    }

    /**
     * 查询所有分类返回树形控件数据
     *
     * @return
     */
    @Override
    public List<PostCategory> findCategoryOptions() {
        List<PostCategory> tops = postCategoryRepository.findByParentId(0L);

        packTree(tops);

        return tops;
    }

    /**
     * 递归查询将所有分类查询出来组成一颗树
     *
     * @param parents
     */
    public void packTree(List<PostCategory> parents) {
        for (PostCategory postCategory : parents) {
            List<PostCategory> children = postCategoryRepository.findByParentId(postCategory.getId());
            Integer num = computePostNumByCategoryId(postCategory.getId());
            postCategory.setNum(num);
            if (children.isEmpty()) {
                continue;
            }

            postCategory.setChildren(children);
            packTree(children);
        }
    }

    /**
     * 根据id查询分类信息，返回的parentId属性为一个集合
     *
     * @param id
     * @return
     */
    @Override
    public PostCategoryEditVo findCategoryById(Long id) {
        PostCategory postCategory = postCategoryRepository.getOne(id);
        LinkedList<Long> list = new LinkedList<>();
        if (postCategory.getParentId() != 0) {
            list.add(postCategory.getParentId());
            getCategoryList(postCategory.getParentId(), list);
        }
        PostCategoryEditVo postCategoryEditVo = new PostCategoryEditVo();
        postCategoryEditVo.setCategoryName(postCategory.getCategoryName());
        postCategoryEditVo.setDescription(postCategory.getDescription());
        postCategoryEditVo.setId(postCategory.getId());
        postCategoryEditVo.setParentId(list);
        return postCategoryEditVo;
    }

    /**
     * 递归查询目标分类及其所有父分类
     *
     * @param id   目标分类的id
     * @param list 储存用的list
     */
    public void getCategoryList(Long id, LinkedList<Long> list) {
        PostCategory postCategory = postCategoryRepository.getOne(id);
        if (postCategory.getParentId() != 0) {
            list.addFirst(postCategory.getParentId());
            getCategoryList(postCategory.getParentId(), list);
        }
    }

    /**
     * 删除分类及其子分类
     *
     * @param id
     */
    @Transactional
    @Override
    public void deleteCategoryById(Long id) {
        PostCategory postCategory = postCategoryRepository.getOne(id);
        postCategoryRepository.delete(postCategory);
        List<Post> postList = postRepository.findPostByCateogryId(id);
        if (!CollectionUtils.isEmpty(postList)){
            for (Post post : postList){
                post.setCategoryId(0L);
                postRepository.save(post);
            }
        }
        List<Long> list = new ArrayList<>();
        List<PostCategory> categories = postCategoryRepository.findByParentId(id);
        getCategoryListSon(categories, list);
        for (int i = 0; i < list.size(); i++) {
            PostCategory postCategory2 = postCategoryRepository.getOne(list.get(i));
            postCategoryRepository.delete(postCategory2);
            List<Post> postList2 = postRepository.findPostByCateogryId(list.get(i));
            if (!CollectionUtils.isEmpty(postList2)){
                for (Post post : postList2){
                    post.setCategoryId(0L);
                    postRepository.save(post);
                }
            }
        }
    }

    /**
     * 递归寻找出所有目标分类的子分类
     *
     * @param parents 目标分类
     * @param list    结果存储
     */
    public void getCategoryListSon(List<PostCategory> parents, List<Long> list) {
        for (PostCategory postCategory : parents) {
            list.add(postCategory.getId());
            List<PostCategory> children = postCategoryRepository.findByParentId(postCategory.getId());
            if (children.isEmpty()) {
                continue;
            }
            getCategoryListSon(children, list);
        }
    }

    @Transactional
    @Override
    public void saveTag(PostTagTemporary postTagTemporary) {

        //判断分类名称是否存在
        PostTag tag1 = postTagRepository.findByTagName(postTagTemporary.getTagName());
        if (tag1 != null) {
            throw new CustomizeRuntimeException(ExceptionEnum.POST_TAG_EXIST);
        }
        PostTag postTag = new PostTag();
        postTag.setTagName(postTagTemporary.getTagName());
        postTag.setDescription(postTagTemporary.getDescription());

        postTagRepository.save(postTag);
    }

    @Override
    public void updateTag(PostTagTemporary postTagTemporary) {
        PostTag tag = postTagRepository.getOne(postTagTemporary.getId());
        tag.setTagName(postTagTemporary.getTagName());
        tag.setDescription(postTagTemporary.getDescription());

        postTagRepository.save(tag);
    }

    @Override
    public List<PostTagVo> findAllTag() {

        List<PostTag> list = postTagRepository.findAll();
        List<PostTagVo> list2 = new ArrayList<>();
        for (PostTag postTag : list) {
            PostTagVo postTagVo = new PostTagVo();
            postTagVo.setId(postTag.getId());
            postTagVo.setDescription(postTag.getDescription());
            postTagVo.setTagName(postTag.getTagName());
            Long num = postRepository.findPostNumByTagId(postTag.getId());
            postTagVo.setPostNum(num);
            list2.add(postTagVo);
        }

        return list2;
    }

    @Transactional
    @Override
    public void deleteTagById(Long id) {
        PostTag postTag = postTagRepository.getOne(id);
        postTagRepository.delete(postTag);
        List<Post> list1 = postRepository.findPostByTagId1(id);
        if (!CollectionUtils.isEmpty(list1)){
            for (Post post : list1){
                post.setTagId1(0L);
                postRepository.save(post);
            }
        }
        List<Post> list2 = postRepository.findPostByTagId2(id);
        if (!CollectionUtils.isEmpty(list2)){
            for (Post post : list2){
                post.setTagId2(0L);
                postRepository.save(post);
            }
        }
        List<Post> list3 = postRepository.findPostByTagId3(id);
        if (!CollectionUtils.isEmpty(list3)){
            for (Post post : list3){
                post.setTagId3(0L);
                postRepository.save(post);
            }
        }
        List<Post> list4 = postRepository.findPostByTagId4(id);
        if (!CollectionUtils.isEmpty(list4)){
            for (Post post : list4){
                post.setTagId4(0L);
                postRepository.save(post);
            }
        }
        List<Post> list5 = postRepository.findPostByTagId5(id);
        if (!CollectionUtils.isEmpty(list5)){
            for (Post post : list5){
                post.setTagId5(0L);
                postRepository.save(post);
            }
        }
    }

    @Override
    public PageResult<PostTableVo> findAllPostByFactor(Integer currentPage, Integer pageSize, String prop, String order, String title, Integer state, List<Long> category) {

        List<Long> list = new ArrayList();

        if (!CollectionUtils.isEmpty(category)) {
            Long categorySelect = category.get(category.size() - 1);
            PostCategory category1 = postCategoryRepository.getOne(categorySelect);
            List<PostCategory> categoryList = new ArrayList() {{
                add(category1);
            }};
            getCategoryListSon(categoryList, list);
        }

        Specification<Post> spec = new Specification<Post>() {

            //使用匿名内部类的方式，创建一个Specification的实现类，并实现toPredicate方法
            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 姑且叫做 新建查询参数数组吧, 是多个查询条件组合的的数组
                List<Predicate> predicatesList = new ArrayList<>();
                List<Predicate> categoryList = new ArrayList<>();
                // 当if成立 添加 匹配大于开始时间的查询参数
                if (!"".equals(title) && title != null) {
                    predicatesList.add(cb.like(root.get("fileTitle").as(String.class), "%" + title + "%"));
                }

                if (!"".equals(state) && state != null) {
                    predicatesList.add(cb.equal(root.get("state").as(int.class), state));
                }

                if (!CollectionUtils.isEmpty(list)) {
                    for (Long l : list) {
                        categoryList.add(cb.equal(root.get("categoryId").as(long.class), l));
                    }
                    predicatesList.add(cb.or(categoryList.toArray(new Predicate[categoryList.size()])));
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
                    query.orderBy(cb.desc(root.get("date")));
                }
                // 将排序与添加好的查询参数数组 作为返回值
                query.where(cb.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
                return query.getRestriction();
            }
        };

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Post> page = postRepository.findAll(spec, pageable);
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        List<Post> content = page.getContent();

        PageResult<PostTableVo> pageResult = new PageResult<>();
        pageResult.setTotalPages(totalPages);
        pageResult.setTotalElements(totalElements);
        List<PostTableVo> content2 = new ArrayList<>();
        for (Post p : content) {
            PostTableVo postTableVo = new PostTableVo();
            postTableVo.setId(p.getId());
            postTableVo.setState(p.getState());
            postTableVo.setFileTitle(p.getFileTitle());
            postTableVo.setDate(p.getDate());

            Integer commentCount = commentService.getCommentCount(p.getId());
            postTableVo.setCommentNum(Long.valueOf(commentCount));
            postTableVo.setAccessNum(p.getAccessNum());

            if (p.getCategoryId() != 0) {
                String categoryName = postCategoryRepository.getOne(p.getCategoryId()).getCategoryName();
                postTableVo.setCategory(categoryName);
            }

            if (p.getTagId1() != 0) {
                String tag1Name = postTagRepository.getOne(p.getTagId1()).getTagName();
                postTableVo.setTag1(tag1Name);
            }

            if (p.getTagId2() != 0) {
                String tag2Name = postTagRepository.getOne(p.getTagId2()).getTagName();
                postTableVo.setTag2(tag2Name);
            }

            if (p.getTagId3() != 0) {
                String tag3Name = postTagRepository.getOne(p.getTagId3()).getTagName();
                postTableVo.setTag3(tag3Name);
            }

            if (p.getTagId4() != 0) {
                String tag4Name = postTagRepository.getOne(p.getTagId4()).getTagName();
                postTableVo.setTag4(tag4Name);
            }

            if (p.getTagId5() != 0) {
                String tag5Name = postTagRepository.getOne(p.getTagId5()).getTagName();
                postTableVo.setTag5(tag5Name);
            }
            content2.add(postTableVo);
        }
        pageResult.setContent(content2);
        return pageResult;
    }

    /**
     * 改变文章状态
     * @param id
     * @param targetState
     */
    @Transactional
    @Override
    public void recover(Long id, int targetState) {
        Post p = postRepository.getOne(id);
        p.setState(targetState);
        postRepository.save(p);
    }

    @Transactional
    @Override
    public void deletePostById(Long id) {
        Post p = postRepository.getOne(id);
        if (p.getState() != 3) {
            throw new CustomizeRuntimeException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        postRepository.delete(p);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(0L);
        handleRecord.setMessage("删除文章");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(2);
        handleRecord.setType2(2);
        dashboardRepository.save(handleRecord);
    }

    @Override
    public PostVo findPostById(Long id) {
        Post post = postRepository.getOne(id);
        PostVo postVo = new PostVo();
        postVo.setId(post.getId());
        postVo.setState(post.getState());
        postVo.setResposity(post.getResposity());
        postVo.setStorage(post.getStorage());
        postVo.setFileTitle(post.getFileTitle());
        postVo.setHtml(post.getHtml());
        postVo.setMarkDown(post.getMarkDown());
        postVo.setDate(post.getDate());
        postVo.setComment(String.valueOf(post.getComment()));
        postVo.setTop(String.valueOf(post.getTop()));
        postVo.setDescription(post.getDescription());
        postVo.setImgUrl(post.getImgUrl());

        Long categoryId = post.getCategoryId();
        LinkedList<Long> categoryList = new LinkedList<>();

        if (categoryId != 0) {
            categoryList.add(categoryId);

            PostCategory postCategory = postCategoryRepository.getOne(categoryId);
            if (postCategory.getParentId() != 0) {
                categoryList.addFirst(postCategory.getParentId());
                getCategoryList(postCategory.getParentId(), categoryList);
            }
        }else{
            categoryList.add(0L);
        }

        postVo.setCategory(categoryList);

        List<Long> tagList = new ArrayList<>();
        if (post.getTagId1() != 0) {
            tagList.add(post.getTagId1());
        }

        if (post.getTagId2() != 0) {
            tagList.add(post.getTagId2());
        }

        if (post.getTagId3() != 0) {
            tagList.add(post.getTagId3());
        }

        if (post.getTagId4() != 0) {
            tagList.add(post.getTagId4());
        }

        if (post.getTagId5() != 0) {
            tagList.add(post.getTagId5());
        }
        postVo.setTags(tagList);
        return postVo;
    }

    @Transactional
    @Override
    public void deletePostByIds(List<Long> ids) {
        List<Post> all = postRepository.findAllById(ids);
        postRepository.deleteAll(all);
        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(0L);
        handleRecord.setMessage("删除文章");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(2);
        handleRecord.setType2(2);
        dashboardRepository.save(handleRecord);
    }

    /**
     * 将文章批量放入回收站
     * @param list
     */
    @Override
    public void recover2(List<Long> list) {
        List<Post> all = postRepository.findAllById(list);
        for (Post post : all){
            post.setState(3);
        }
        postRepository.saveAll(all);
    }

    /**
     * 将文章批量还原
     * @param list
     */
    @Override
    public void recover3(List<Long> list) {
        List<Post> all = postRepository.findAllById(list);
        for (Post post : all){
            post.setState(1);
        }
        postRepository.saveAll(all);
    }

    /**
     * 将Post封装成PostIndexVo
     * @param p
     * @return
     */
    public PostIndexVo packagePost(Post p){
        PostIndexVo postIndexVo = new PostIndexVo();
        postIndexVo.setId(p.getId());
        postIndexVo.setState(p.getState());
        postIndexVo.setStorage(p.getStorage());
        postIndexVo.setResposity(p.getResposity());
        postIndexVo.setFileTitle(p.getFileTitle());
        postIndexVo.setHtml(p.getHtml());
        postIndexVo.setMarkDown(p.getMarkDown());
        postIndexVo.setDate(p.getDate());
        postIndexVo.setComment(p.getComment());
        postIndexVo.setTop(p.getTop());
        postIndexVo.setDescription(p.getDescription());
        postIndexVo.setImgUrl(p.getImgUrl());
        postIndexVo.setCommentNum(p.getCommentNum());
        postIndexVo.setAccessNum(p.getAccessNum());

        Map<Long,String> categoryMap = new HashMap<>();
        if (p.getCategoryId() != 0) {
            String categoryName = postCategoryRepository.getOne(p.getCategoryId()).getCategoryName();
            categoryMap.put(p.getCategoryId(),categoryName);
        }
        postIndexVo.setCategories(categoryMap);

        Map<Long,String> tagMap = new HashMap<>();
        if (p.getTagId1() != 0) {
            String tag1Name = postTagRepository.getOne(p.getTagId1()).getTagName();
            tagMap.put(p.getTagId1(),tag1Name);
        }
        if (p.getTagId2() != 0) {
            String tag2Name = postTagRepository.getOne(p.getTagId2()).getTagName();
            tagMap.put(p.getTagId2(),tag2Name);
        }
        if (p.getTagId3() != 0) {
            String tag3Name = postTagRepository.getOne(p.getTagId3()).getTagName();
            tagMap.put(p.getTagId3(),tag3Name);
        }
        if (p.getTagId4() != 0) {
            String tag4Name = postTagRepository.getOne(p.getTagId4()).getTagName();
            tagMap.put(p.getTagId4(),tag4Name);
        }
        if (p.getTagId5() != 0) {
            String tag5Name = postTagRepository.getOne(p.getTagId5()).getTagName();
            tagMap.put(p.getTagId5(),tag5Name);
        }
        postIndexVo.setTags(tagMap);
        return postIndexVo;
    }

    /**
     * 查询每个分类所拥有的文章总数
     * @param categoryId
     * @return
     */
    public Integer computePostNumByCategoryId(Long categoryId) {
        List<Long> resultList = new ArrayList<>();

        List<PostCategory> list = new ArrayList<>();
        list.add(postCategoryRepository.getOne(categoryId));
        getCategoryListSon(list,resultList);
        int num = 0;
        for (Long cgId:resultList){
            Integer n = postRepository.computePostNumByCategoryId(cgId);
            num += n;
        }
        return num;
    }
}
