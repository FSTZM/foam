package run.foam.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import run.foam.app.model.dto.CommentDeleteDTO;
import run.foam.app.model.dto.CommentInfoDTO;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.vo.CommentInfoVo;
import run.foam.app.model.vo.CommentVo;
import run.foam.app.service.CommentService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("info/save")
    public ResponseEntity<Void> saveUploadInfo(@RequestBody CommentInfoDTO commentInfoDTO) {

        commentService.saveCommentInfo(commentInfoDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("info/update")
    public ResponseEntity<Void> updateCommentInfo(@RequestBody CommentInfoDTO commentInfoDTO) {

        commentService.updateCommentInfo(commentInfoDTO);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("info/find")
    public ResponseEntity<CommentInfoVo> findCommentInfo() {

        return ResponseEntity.ok(commentService.findCommentInfo());
    }

    @GetMapping("find/all/factor")
    public ResponseEntity<PageResult<CommentVo>> findCommentByfactor(@RequestParam("currentPage") Integer currentPage,
                                                                     @RequestParam("pageSize") Integer pageSize,
                                                                     @RequestParam("prop") String prop,
                                                                     @RequestParam("order") String order,
                                                                     @RequestParam("keyword") String keyword) {

        return ResponseEntity.ok(commentService.findCommentByfactor(currentPage,pageSize,prop,order,keyword));
    }

    @DeleteMapping("delete/id")
    public ResponseEntity<Void> deleteCommentById(@RequestParam("id") String id) {

        commentService.deleteCommentById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("delete/ids")
    public ResponseEntity<Void> deleteCommentById(@RequestBody List<CommentDeleteDTO> comments) {

        List<String> ids = new ArrayList<>();
        for (CommentDeleteDTO commentDeleteDTO:comments){
            ids.add(commentDeleteDTO.getObjectId());
        }
        commentService.deleteCommentByIds(ids);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
