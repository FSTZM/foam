package run.foam.app.exception.advice;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import run.foam.app.exception.exception.CustomizeRuntimeException;
import run.foam.app.exception.vo.ExceptionResult;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(CustomizeRuntimeException.class)
    public ResponseEntity<ExceptionResult> handleException(CustomizeRuntimeException e) {
        return ResponseEntity.status(e.getExceptionEnum().getCode())
                .body(new ExceptionResult(e.getExceptionEnum()));
    }
}
