package run.foam.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import run.foam.app.model.dto.NoteDTO;
import run.foam.app.model.entity.Note;
import run.foam.app.model.vo.CommentInfoVo;
import run.foam.app.model.vo.NoteVo;
import run.foam.app.model.vo.RecordVo;
import run.foam.app.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("find/message")
    public ResponseEntity<RecordVo> findMessage() {

        return ResponseEntity.ok(dashboardService.findMessage());
    }

    @PostMapping("save/note")
    public ResponseEntity<Void> saveNote(@RequestBody NoteDTO noteDTO) {
        dashboardService.saveNote(noteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("update/note")
    public ResponseEntity<Void> updateNote(@RequestBody NoteDTO noteDTO) {
        dashboardService.saveNote(noteDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("find/note")
    public ResponseEntity<List<NoteVo>> findNote() {

        return ResponseEntity.ok(dashboardService.findAllNote());
    }

    @GetMapping("find/note/{id}")
    public ResponseEntity<NoteVo> findNoteById(@PathVariable("id") Long id) {

        return ResponseEntity.ok(dashboardService.findNoteById(id));
    }

    @DeleteMapping("delete/note")
    public ResponseEntity<Void> deleteNote(@RequestParam("id") Long id) {
        dashboardService.deleteNote(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
