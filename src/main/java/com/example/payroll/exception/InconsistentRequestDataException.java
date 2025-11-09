package com.example.payroll.exception;

public class InconsistentRequestDataException extends RuntimeException {
  public InconsistentRequestDataException(String message) {
    super(message);
  }
}
