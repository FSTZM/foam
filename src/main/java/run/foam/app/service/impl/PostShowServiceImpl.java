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
import org.springframework.util.StringUtils;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.Post;
import run.foam.app.model.entity.PostCategory;
import run.foam.app.model.entity.PostTag;
import run.foam.app.model.entity.User;
import run.foam.app.model.vo.*;
import run.foam.app.repository.PostCategoryRepository;
import run.foam.app.repository.PostRepository;
import run.foam.app.repository.PostTagRepository;
import run.foam.app.repository.UserRepository;
import run.foam.app.service.PostService;
import run.foam.app.service.PostShowService;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PostShowServiceImpl implements PostShowService {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public PageResult<PostIndexVo> findAllPostIndex(Integer currentPage, Integer pageSize, String title, Integer state, Long categoryId, Long tagId) {
        List<Long> list = new ArrayList();

        if (categoryId != null && categoryId != 0) {
            PostCategory category1 = postCategoryRepository.getOne(categoryId);
            List<PostCategory> categoryList = new ArrayList() {{
                add(category1);
            }};
            postService.getCategoryListSon(categoryList, list);
        }

        Specification<Post> spec = new Specification<Post>() {

            //使用匿名内部类的方式，创建一个Specification的实现类，并实现toPredicate方法
            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 姑且叫做 新建查询参数数组吧, 是多个查询条件组合的的数组
                List<Predicate> predicatesList = new ArrayList<>();
                List<Predicate> categoryList = new ArrayList<>();
                List<Predicate> tagList = new ArrayList<>();
                List<Order> orderList = new ArrayList<>();
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

                if (!StringUtils.isEmpty(tagId)) {
                    tagList.add(cb.equal(root.get("tagId1").as(long.class), tagId));
                    tagList.add(cb.equal(root.get("tagId2").as(long.class), tagId));
                    tagList.add(cb.equal(root.get("tagId3").as(long.class), tagId));
                    tagList.add(cb.equal(root.get("tagId4").as(long.class), tagId));
                    tagList.add(cb.equal(root.get("tagId5").as(long.class), tagId));
                    predicatesList.add(cb.or(tagList.toArray(new Predicate[tagList.size()])));
                }

                orderList.add(cb.asc(root.get("top")));
                orderList.add(cb.desc(root.get("date")));
                query.orderBy(orderList);
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

        PageResult<PostIndexVo> pageResult = new PageResult<>();
        pageResult.setTotalPages(totalPages);
        pageResult.setTotalElements(totalElements);
        List<PostIndexVo> content2 = new ArrayList<>();
        for (Post p : content) {
            PostIndexVo postIndexVo = postService.packagePost(p);
            content2.add(postIndexVo);
        }
        pageResult.setContent(content2);
        return pageResult;
    }

    @Override
    public List<PostTitleVo> findAllByTitle(String title) {
        List<Post> list = postRepository.findPostByTitle("%" + title + "%");
        List<PostTitleVo> list2 = new ArrayList<>();
        for (Post p : list){
            PostTitleVo postTitleVo = new PostTitleVo();
            postTitleVo.setTitle(p.getFileTitle());
            postTitleVo.setId(p.getId());
            postTitleVo.setDescription(p.getDescription());
            list2.add(postTitleVo);
        }
        return list2;
    }

    @Override
    public PostIndexVo findIndexPostById(Long id) {
        Post p = postRepository.getOne(id);
        PostIndexVo postIndexVo = postService.packagePost(p);
        return postIndexVo;
    }

    @Override
    public List<PostCategory> findCategoryOptions() {
        return postService.findCategoryOptions();
    }

    @Override
    public Map<String,List<PostTimelineVo>> findPostOfTimeline(Long tagId, Long categoryId) {
        List<Post> list;
        if (tagId != null){
            list = postRepository.findPostOrderByTagIdDesc(tagId);
        }else if(categoryId != null){
            //查找分类所有子分类
            List<Long> resultList = new ArrayList();
            List<PostCategory> parentList = new ArrayList();
            parentList.add(postCategoryRepository.getOne(categoryId));
            postService.getCategoryListSon(parentList,resultList);

            Specification<Post> spec = new Specification<Post>() {

                //使用匿名内部类的方式，创建一个Specification的实现类，并实现toPredicate方法
                @Override
                public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    // 姑且叫做 新建查询参数数组吧, 是多个查询条件组合的的数组
                    List<Predicate> predicatesList = new ArrayList<>();
                    List<Predicate> categoryList = new ArrayList<>();
                    // 当if成立 添加 匹配大于开始时间的查询参数
                    if (!CollectionUtils.isEmpty(resultList)) {
                        for (Long l : resultList) {
                            categoryList.add(cb.equal(root.get("categoryId").as(long.class), l));
                        }
                        predicatesList.add(cb.or(categoryList.toArray(new Predicate[categoryList.size()])));
                    }

                    query.orderBy(cb.desc(root.get("date")));
                    // 将排序与添加好的查询参数数组 作为返回值
                    query.where(cb.and(predicatesList.toArray(new Predicate[predicatesList.size()])));
                    return query.getRestriction();
                }
            };
            list = postRepository.findAll(spec);
        }else {
            list = postRepository.findPostOrderByDesc();
        }

        Map<String,List<PostTimelineVo>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(list)){
            for (Post p : list){
                PostTimelineVo postTimelineVo = new PostTimelineVo();
                postTimelineVo.setTitle(p.getFileTitle());
                postTimelineVo.setId(p.getId());
                if (!StringUtils.isEmpty(p.getDate())){
                    String[] arr = p.getDate().split(" ");
                    String[] split = arr[0].split("-");
                    String newDate = split[1]+"/"+split[2];
                    postTimelineVo.setDate(newDate);

                    if (!map.containsKey(split[0])){
                        List<PostTimelineVo> list2 = new ArrayList<>();
                        list2.add(postTimelineVo);
                        map.put(split[0],list2);
                    }else {
                        List<PostTimelineVo> postTimelineVoList = map.get(split[0]);
                        postTimelineVoList.add(postTimelineVo);
                        map.put(split[0],postTimelineVoList);
                    }
                }
            }
            return map;
        }else {
            return null;
        }
    }

    @Override
    public String findTagNameByTagId(Long tagId) {
        return postTagRepository.getOne(tagId).getTagName();
    }

    @Override
    public String findTagNameByCategoryId(Long categoryId) {
        return postCategoryRepository.getOne(categoryId).getCategoryName();
    }

    @Override
    public List<PostTagVo> findTagOptions() {
        return postService.findAllTag();
    }

    @Transactional
    @Override
    public void saveAccessNum(Long id) {
        Post post = postRepository.getOne(id);
        post.setAccessNum(post.getAccessNum()+1);
        postRepository.save(post);
    }

    @Override
    public Map<Integer,Map<Long,String>> postLastAndNext(Long id) {
        Map<Long, String> in1 = new HashMap<>();
        Map<Long, String> in2 = new HashMap<>();
        Map<Integer,Map<Long,String>> out = new HashMap<>();
        List<Post> list = postRepository.findAllByOrder();
        Post post = postRepository.getOne(id);
        if (list.contains(post)){
            if (list.size()<= 1){
                out.put(1,null);
                out.put(2,null);
            }else {
                int i = list.indexOf(post);
                if (i == list.size()-1){
                    in1.put(list.get(i-1).getId(),list.get(i-1).getFileTitle());
                    out.put(1,in1);
                    out.put(2,null);
                }else if (i == 0){
                    in2.put(list.get(i+1).getId(),list.get(i+1).getFileTitle());
                    out.put(1,null);
                    out.put(2,in2);
                }else {
                    in1.put(list.get(i-1).getId(),list.get(i-1).getFileTitle());
                    in2.put(list.get(i+1).getId(),list.get(i+1).getFileTitle());
                    out.put(1,in1);
                    out.put(2,in2);
                }
            }
        }

        return out;
    }

    @Override
    public UserShowVo findUser() {
        User user = userRepository.findAll().get(0);
        UserShowVo userShowVo = new UserShowVo();
        userShowVo.setAvatar(user.getAvatar());
        userShowVo.setNickname(user.getNickname());
        userShowVo.setDescription(user.getDescription());
        return userShowVo;
    }

    @Override
    public Integer getTotalAccessNum() {
        List<Post> all = postRepository.findAll();
        int totalAccessNum = 0;
        for (Post post:all){
            totalAccessNum += post.getAccessNum();
        }
        return totalAccessNum;
    }

}
