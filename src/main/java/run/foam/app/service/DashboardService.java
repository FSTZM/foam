package run.foam.app.service;

import run.foam.app.model.dto.NoteDTO;
import run.foam.app.model.vo.NoteVo;
import run.foam.app.model.vo.RecordVo;

import java.util.List;

public interface DashboardService {

    RecordVo findMessage();

    void saveNote(NoteDTO noteDTO);

    List<NoteVo> findAllNote();

    void deleteNote(Long id);

    NoteVo findNoteById(Long id);
}
