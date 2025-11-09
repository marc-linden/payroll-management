package com.example.payroll.web.common;

import com.example.payroll.exception.EmployeeNotFoundException;
import com.example.payroll.exception.InconsistentRequestDataException;
import com.example.payroll.exception.ResourceAlreadyExistsException;
import com.example.payroll.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
  @ExceptionHandler({
      EmployeeNotFoundException.class,
      ResourceNotFoundException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String notFound(Exception ex) {
    logHandledException(HttpStatus.NOT_FOUND, ex);
    return ex.getMessage();
  }

  @ExceptionHandler({
      InconsistentRequestDataException.class,
      MethodArgumentNotValidException.class,
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String badRequest(Exception ex) {
    logHandledException(HttpStatus.BAD_REQUEST, ex);
    return ex.getMessage();
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  String conflict(Exception ex) {
    logHandledException(HttpStatus.CONFLICT, ex);
    return ex.getMessage();
  }

  private void logHandledException(HttpStatus httpStatus, Exception ex) {
    log.debug("Resolve exception {}[{}] to {}", ex.getClass().getSimpleName(), ex.getMessage(), httpStatus.value());
  }
}
