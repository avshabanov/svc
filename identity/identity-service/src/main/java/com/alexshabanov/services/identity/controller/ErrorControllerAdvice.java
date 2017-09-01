package com.alexshabanov.services.identity.controller;

import com.truward.brikar.error.HttpRestErrorException;
import com.truward.brikar.error.RestErrors;
import com.truward.brikar.error.StandardRestErrorCodes;
import com.truward.brikar.error.model.ErrorV1;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom error handler
 */
@ControllerAdvice
public final class ErrorControllerAdvice {

  private final RestErrors errors;

  public ErrorControllerAdvice(RestErrors errors) {
    this.errors = errors;
  }

  // spring security-only exception
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorV1.ErrorResponse accessDenied(AccessDeniedException e) {
    return RestErrors.errorResponse(
        errors.errorBuilder(StandardRestErrorCodes.UNAUTHORIZED)
            .setMessage(e.getMessage())
            .build());
  }

  @ExceptionHandler(HttpRestErrorException.class)
  @ResponseBody
  public ResponseEntity<ErrorV1.ErrorResponse> restErrorException(HttpRestErrorException e) {
    return new ResponseEntity<>(RestErrors.errorResponse(e.getError()), HttpStatus.valueOf(e.getStatusCode()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorV1.ErrorResponse illegalArgument(IllegalArgumentException e) {
    return RestErrors.errorResponse(errors.errorBuilder(StandardRestErrorCodes.INVALID_ARGUMENT)
        .setMessage(e.getMessage())
        .build());
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
  @ResponseBody
  public ErrorV1.ErrorResponse unsupported(UnsupportedOperationException e) {
    return RestErrors.errorResponse(errors.errorBuilder(StandardRestErrorCodes.NOT_IMPLEMENTED)
        .setMessage(e.getMessage())
        .build());
  }

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorV1.ErrorResponse internalServerError(Throwable e) {
    return RestErrors.errorResponse(errors.errorBuilder(StandardRestErrorCodes.INTERNAL)
        .setMessage(e.getMessage())
        .build());
  }
}
