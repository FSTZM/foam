package run.foam.app.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.foam.app.model.dto.*;
import run.foam.app.model.entity.Post;
import run.foam.app.model.entity.PostCategory;
import run.foam.app.model.vo.*;
import run.foam.app.service.PostService;

import java.util.*;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("upload/picture")
    public ResponseEntity<Map<String, String>> uploadPicture(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("storage") String storage,
                                                             @RequestParam("resposity") String resposity,
                                                             @RequestParam("fileTitle") String bucket) {
        String url = "";
        if (StringUtils.equals(storage, "1")) {
            url = postService.uploadPicture(file, resposity, bucket);
        }
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }

    @PostMapping("save/post")
    public ResponseEntity<Post> savePost(@RequestBody PostTemporary postTemporary) {

        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(postTemporary));
    }

    @PutMapping("update/post")
    public ResponseEntity<Void> updatePost(@RequestBody PostTemporary postTemporary) {

        postService.savePost(postTemporary);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("save/category")
    public ResponseEntity<Void> saveCategory(@RequestBody PostCategoryTemporary postCategoryTemporary) {

        postService.saveCategory(postCategoryTemporary);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("update/category")
    public ResponseEntity<Void> updateCategory(@RequestBody PostCategoryTemporary postCategoryTemporary) {

        postService.updateCategory(postCategoryTemporary);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("find/category")
    public ResponseEntity<PageResult<PostCategoryVo>> findAllCategory(@RequestParam("currentPage") int currentPage,
                                                                      @RequestParam("pageSize") int pageSize) {
        return ResponseEntity.ok(postService.findAllCategory(currentPage, pageSize));
    }

    @GetMapping("find/category/options")
    public ResponseEntity<List<PostCategory>> findCategoryOptions() {
        return ResponseEntity.ok(postService.findCategoryOptions());
    }

    @GetMapping("find/category/one")
    public ResponseEntity<PostCategoryEditVo> findCategoryOptions(@RequestParam("id") Long id) {
        return ResponseEntity.ok(postService.findCategoryById(id));
    }

    @DeleteMapping("delete/category/one")
    public ResponseEntity<PostCategoryEditVo> deleteCategoryById(@RequestParam("id") Long id) {
        postService.deleteCategoryById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("delete/tag/one")
    public ResponseEntity<PostCategoryEditVo> deleteTagById(@RequestParam("id") Long id) {
        System.out.println(id);
        postService.deleteTagById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("save/tag")
    public ResponseEntity<Void> saveTag(@RequestBody PostTagTemporary postTagTemporary) {

        postService.saveTag(postTagTemporary);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("update/tag")
    public ResponseEntity<Void> updateTag(@RequestBody PostTagTemporary postTagTemporary) {

        postService.updateTag(postTagTemporary);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("find/tag")
    public ResponseEntity<List<PostTagVo>> findAllTag() {
        return ResponseEntity.ok(postService.findAllTag());
    }

    @GetMapping("find/all/factor")
    public ResponseEntity<PageResult<PostTableVo>> findAllPostByFactor(@RequestParam("currentPage") Integer currentPage,
                                                                       @RequestParam("pageSize") Integer pageSize,
                                                                       @RequestParam("prop") String prop,
                                                                       @RequestParam("order") String order,
                                                                       @RequestParam("title") String title,
                                                                       @RequestParam("state") Integer state,
                                                                       @RequestParam("category") String categoryString) {
        if (categoryString != null && !"".equals(categoryString)) {
            List<Long> category = new ArrayList<>();
            String[] split = categoryString.split("&");
            for (String s : split) {
                String[] split1 = s.split("=");
                category.add(Long.parseLong(split1[1]));
            }
            return ResponseEntity.ok(postService.findAllPostByFactor(currentPage, pageSize, prop, order, title, state, category));
        }else {
            return ResponseEntity.ok(postService.findAllPostByFactor(currentPage, pageSize, prop, order, title, state, null));
        }
    }

    @PutMapping("recover")
    public ResponseEntity<Void> recover(@RequestParam("id") Long id,
                                        @RequestParam("targetState") int targetState) {
        postService.recover(id,targetState);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("recover/selected")
    public ResponseEntity<Void> recover2(@RequestBody List<PostTableDTO> dtos) {
        List<Long> list = new ArrayList<>();
        for (PostTableDTO postTableDTO : dtos){
            list.add(postTableDTO.getId());
        }
        postService.recover2(list);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("recover/selected2")
    public ResponseEntity<Void> recover3(@RequestBody List<PostTableDTO> dtos) {
        List<Long> list = new ArrayList<>();
        for (PostTableDTO postTableDTO : dtos){
            list.add(postTableDTO.getId());
        }
        postService.recover3(list);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("delete/post")
    public ResponseEntity<Void> deletePostById(@RequestParam("id") Long id) {
        postService.deletePostById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("delete/post/selected")
    public ResponseEntity<Void> deletePostByIds(@RequestBody List<PostTableDTO> dtos) {
        List<Long> ids = new ArrayList<>();
        for (PostTableDTO postTableDTO:dtos){
            ids.add(postTableDTO.getId());
        }
        postService.deletePostByIds(ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("find/{id}")
    public ResponseEntity<PostVo> findPostById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }
}
