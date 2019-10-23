package io.github.jelastic.core.exception;

public class JelasticException extends RuntimeException {

  public JelasticException() {
  }

  public JelasticException(String message) {
    super(message);
  }

  public JelasticException(String message, Throwable cause) {
    super(message, cause);
  }
}