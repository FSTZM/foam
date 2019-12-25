package run.foam.app.exception.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.foam.app.exception.enums.ExceptionEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomizeRuntimeException extends RuntimeException{

    private ExceptionEnum exceptionEnum;
}
