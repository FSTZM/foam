package run.foam.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.foam.app.model.dto.NoteDTO;
import run.foam.app.model.dto.PageResult;
import run.foam.app.model.entity.HandleRecord;
import run.foam.app.model.entity.Note;
import run.foam.app.model.entity.Post;
import run.foam.app.model.vo.CommentVo;
import run.foam.app.model.vo.NoteVo;
import run.foam.app.model.vo.PostTitleVo;
import run.foam.app.model.vo.RecordVo;
import run.foam.app.repository.DashboardRepository;
import run.foam.app.repository.NoteRepository;
import run.foam.app.repository.PostRepository;
import run.foam.app.service.CommentService;
import run.foam.app.service.DashboardService;
import run.foam.app.service.PostService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public RecordVo findMessage() {
        List<Post> postRecordByUpdateTime = postRepository.findPostRecordByUpdateTime();
        List<PostTitleVo> list = new ArrayList<>();
        for (Post post:postRecordByUpdateTime){
            PostTitleVo postTitleVo = new PostTitleVo();
            postTitleVo.setTitle(post.getFileTitle());
            postTitleVo.setId(post.getId());
            postTitleVo.setTime(post.getUpdateTime());
            list.add(postTitleVo);
        }

        PageResult<CommentVo> commentByfactor = commentService.findCommentByfactor(1, 5, "", "", "");
        List<CommentVo> content = commentByfactor.getContent();

        List<HandleRecord> recordByTime = dashboardRepository.findRecordByTime();

        RecordVo recordVo = new RecordVo();
        recordVo.setCommentList(content);
        recordVo.setHandleList(recordByTime);
        recordVo.setPostList(list);

        return recordVo;
    }

    @Transactional
    @Override
    public void saveNote(NoteDTO noteDTO) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        Note note;
        int a;
        if (noteDTO.getId() != null &&  !"".equals(noteDTO.getId())){
            note = noteRepository.getOne(noteDTO.getId());
            a=1;
        }else {
            note = new Note();
            note.setCreateTime(str);
            a=2;
        }

        note.setContent(noteDTO.getContent());
        note.setUpdateTime(str);
        Note save = noteRepository.save(note);

        if (a==1){
            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(save.getId());
            handleRecord.setMessage("更新便签");
            handleRecord.setTime(str);
            handleRecord.setType(8);
            handleRecord.setType2(3);
            dashboardRepository.save(handleRecord);
        }else {
            HandleRecord handleRecord = new HandleRecord();
            handleRecord.setCid(save.getId());
            handleRecord.setMessage("保存便签");
            handleRecord.setTime(str);
            handleRecord.setType(8);
            handleRecord.setType2(1);
            dashboardRepository.save(handleRecord);
        }
    }

    @Override
    public List<NoteVo> findAllNote() {
        List<NoteVo> list = new ArrayList<>();
        List<Note> all = noteRepository.findAllOrderByCreateTime();
        for (Note note : all){
            NoteVo noteVo = new NoteVo();
            noteVo.setId(note.getId());
            noteVo.setContent(note.getContent());
            noteVo.setCreateTime(note.getCreateTime());
            noteVo.setUpdateTime(note.getUpdateTime());
            list.add(noteVo);
        }
        return list;
    }

    /**
     * 批量删除note
     */
    @Override
    public void deleteNote(Long id) {
        Note note = noteRepository.getOne(id);
        noteRepository.delete(note);

        HandleRecord handleRecord = new HandleRecord();
        handleRecord.setCid(0L);
        handleRecord.setMessage("删除便签");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        handleRecord.setTime(str);
        handleRecord.setType(8);
        handleRecord.setType2(2);
        dashboardRepository.save(handleRecord);
    }

    @Override
    public NoteVo findNoteById(Long id) {
        Note note = noteRepository.getOne(id);
        NoteVo noteVo = new NoteVo();
        noteVo.setId(note.getId());
        noteVo.setContent(note.getContent());
        noteVo.setCreateTime(note.getCreateTime());
        noteVo.setUpdateTime(note.getUpdateTime());
        return noteVo;
    }
}
