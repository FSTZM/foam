package run.foam.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.PostCategory;
import run.foam.app.model.entity.PostTag;
import run.foam.app.model.vo.*;
import run.foam.app.service.PostService;
import run.foam.app.service.PostShowService;

import java.util.*;

@RestController
@RequestMapping("api/post/show")
public class PostShowController {

    @Autowired
    private PostShowService postShowService;

    @GetMapping("find/index/all")
    public ResponseEntity<PageResult<PostIndexVo>> findAllPostIndex(@RequestParam("currentPage") Integer currentPage,
                                                                    @RequestParam("pageSize") Integer pageSize,
                                                                    @RequestParam("title") String title,
                                                                    @RequestParam("state") Integer state,
                                                                    @RequestParam("category") Long categoryId,
                                                                    @RequestParam("tag") Long tagId) {
        return ResponseEntity.ok(postShowService.findAllPostIndex(currentPage, pageSize, title, state, categoryId, tagId));
    }

    @GetMapping("find/index/title")
    public ResponseEntity<List<PostTitleVo>> findAllByTitle(@RequestParam("title") String title) {
        return ResponseEntity.ok(postShowService.findAllByTitle(title));
    }

    @GetMapping("find/index/{id}")
    public ResponseEntity<PostIndexVo> findIndexPostById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postShowService.findIndexPostById(id));
    }

    @GetMapping("find/category/options")
    public ResponseEntity<List<PostCategory>> findCategoryOptions() {
        return ResponseEntity.ok(postShowService.findCategoryOptions());
    }

    @GetMapping("find/tag/options")
    public ResponseEntity<List<PostTagVo>> findTagOptions() {
        return ResponseEntity.ok(postShowService.findTagOptions());
    }

    @GetMapping("find/timeline")
    public ResponseEntity<Map<String,List<PostTimelineVo>>> findPostOfTimeline(@RequestParam(value = "tagId",required = false) Long tagId,
                                                                 @RequestParam(value = "categoryId",required = false) Long categoryId) {
        return ResponseEntity.ok(postShowService.findPostOfTimeline(tagId,categoryId));
    }

    @GetMapping("tag/{tagId}")
    public ResponseEntity<String> findTagNameByTagId(@PathVariable("tagId") Long tagId) {
        return ResponseEntity.ok(postShowService.findTagNameByTagId(tagId));
    }

    @GetMapping("category/{categoryId}")
    public ResponseEntity<String> findTagNameByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(postShowService.findTagNameByCategoryId(categoryId));
    }

    @PutMapping("save/accessNum")
    public ResponseEntity<Void> saveAccessNum(@RequestParam("id") Long id) {
        postShowService.saveAccessNum(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("last/next/{id}")
    public ResponseEntity<Map<Integer,Map<Long,String>>> postLastAndNext(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postShowService.postLastAndNext(id));
    }

    @GetMapping("user")
    public ResponseEntity<UserShowVo> findUser() {
        return ResponseEntity.ok(postShowService.findUser());
    }

    @GetMapping("total/accessNum")
    public ResponseEntity<Integer> getTotalAccessNum() {
        return ResponseEntity.ok(postShowService.getTotalAccessNum());
    }
}
