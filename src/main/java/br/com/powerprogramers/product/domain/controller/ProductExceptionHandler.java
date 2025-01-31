package br.com.powerprogramers.product.domain.controller;

import br.com.powerprogramers.product.domain.exceptions.CreateProductUseCaseException;
import br.com.powerprogramers.product.domain.exceptions.DominioException;
import br.com.powerprogramers.product.domain.exceptions.ProductException;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadMoveFileException;
import br.com.powerprogramers.product.domain.exceptions.ProductNotFoundException;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Exception management class. */
@RestControllerAdvice
public class ProductExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles exceptions related to product operations and constructs a ResponseEntity with the
   * appropriate error details.
   *
   * @param ex the product-related exception that occurred
   * @param request the web request during which the exception occurred
   * @return a ResponseEntity containing the error details and the appropriate HTTP status code
   */
  @ExceptionHandler(
      value = {
        ProductException.class,
        CreateProductUseCaseException.class,
        ProductNotFoundException.class,
        ProductLoadMoveFileException.class,
        ProductLoadJobException.class
      })
  public ResponseEntity<DominioException> productExceptionHandler(
      ProductException ex, WebRequest request) {
    return ResponseEntity.status(ex.getStatus()).body(DominioException.from(ex, request));
  }

  /**
   * Handles MethodArgumentTypeMismatchException and constructs a ResponseEntity with the
   * appropriate error details.
   *
   * @param ex the exception that occurred due to method argument type mismatch
   * @param request the web request during which the exception occurred
   * @return a ResponseEntity containing the error details and the HTTP status code 400 (Bad
   *     Request)
   */
  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  public ResponseEntity<DominioException> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    String message =
        "Error converting %s with value of type %s".formatted(ex.getName(), ex.getValue());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(DominioException.from(HttpStatus.BAD_REQUEST, message, request));
  }

  /**
   * Handles IllegalArgumentException and constructs a ResponseEntity with the appropriate error
   * details.
   *
   * @param ex the exception that occurred due to an illegal argument
   * @param request the web request during which the exception occurred
   * @return a ResponseEntity containing the error details and the HTTP status code 400 (Bad
   *     Request)
   */
  @ExceptionHandler(value = IllegalArgumentException.class)
  public ResponseEntity<DominioException> illegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(DominioException.from(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
  }

  /**
   * Handles MethodArgumentNotValidException and constructs a ResponseEntity with the appropriate
   * error details.
   *
   * @param ex the exception that occurred due to method argument not being valid (can be null)
   * @param headers the HTTP headers for the response (can be null)
   * @param status the HTTP status code for the response (can be null)
   * @param request the web request during which the exception occurred (can be null)
   * @return a ResponseEntity containing the error details and appropriate HTTP status code
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @Nullable MethodArgumentNotValidException ex,
      @Nullable HttpHeaders headers,
      @Nullable HttpStatusCode status,
      @Nullable WebRequest request) {

    if (ex == null || status == null || request == null) {
      return generateServerErrorResponse();
    }

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return ResponseEntity.status(status)
        .body(
            DominioException.build(HttpStatus.valueOf(status.value()), errors.toString(), request));
  }

  /**
   * Handles HttpMediaTypeNotSupportedException and constructs a ResponseEntity with the appropriate
   * error details.
   *
   * @param ex the exception that occurred due to unsupported media type (can be null)
   * @param headers the HTTP headers for the response (can be null)
   * @param status the HTTP status code for the response (can be null)
   * @param request the web request during which the exception occurred (can be null)
   * @return a ResponseEntity containing the error details and appropriate HTTP status code
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      @Nullable HttpMediaTypeNotSupportedException ex,
      @Nullable HttpHeaders headers,
      @Nullable HttpStatusCode status,
      @Nullable WebRequest request) {

    if (ex == null || status == null || request == null) {
      return generateServerErrorResponse();
    }

    return ResponseEntity.status(status)
        .body(
            DominioException.build(
                HttpStatus.valueOf(status.value()), ex.getBody().getDetail(), request));
  }

  /**
   * Handles HttpMessageNotReadableException and constructs a ResponseEntity with the appropriate
   * error details.
   *
   * @param ex the exception that occurred due to the message not being readable (can be null)
   * @param headers the HTTP headers for the response (can be null)
   * @param status the HTTP status code for the response (can be null)
   * @param request the web request during which the exception occurred (can be null)
   * @return a ResponseEntity containing the error details and appropriate HTTP status code
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      @Nullable HttpMessageNotReadableException ex,
      @Nullable HttpHeaders headers,
      @Nullable HttpStatusCode status,
      @Nullable WebRequest request) {

    if (ex == null || status == null || request == null) {
      return generateServerErrorResponse();
    }

    return ResponseEntity.status(status)
        .body(
            DominioException.build(
                HttpStatus.valueOf(status.value()), "Failed to read request", request));
  }

  /**
   * Handles ConstraintViolationException and constructs a ResponseEntity with the appropriate
   * error.
   *
   * @param ex exception
   * @param request web request
   * @return a ResponseEntity containing the error details and the HTTP status code 400
   */
  @ExceptionHandler(value = ConstraintViolationException.class)
  public ResponseEntity<DominioException> constraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(DominioException.from(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      @Nullable MissingServletRequestParameterException ex,
      @Nullable HttpHeaders headers,
      @Nullable HttpStatusCode status,
      @Nullable WebRequest request) {

    if (ex == null || status == null || request == null) {
      return generateServerErrorResponse();
    }

    String errorMessage = "The mandatory parameter '" + ex.getParameterName() + "' it is absent";

    return ResponseEntity.status(status)
        .body(DominioException.build(HttpStatus.valueOf(status.value()), errorMessage, request));
  }

  private ResponseEntity<Object> generateServerErrorResponse() {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            DominioException.build(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null));
  }
}
