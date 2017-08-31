package com.alexshabanov.services.identity.model.error.jetty;

import com.alexshabanov.services.identity.model.error.IdentityRestErrors;
import com.truward.brikar.error.StandardRestErrorCode;
import com.truward.brikar.error.model.ErrorV1;
import com.truward.brikar.protobuf.http.ProtobufHttpMessageConverter;
import com.truward.brikar.protobuf.http.json.ProtobufJsonHttpMessageConverter;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Custom error handler for all HTTP errors that application can respond with.
 * TODO: move to the separate package (within brikar-errors).
 */
@ParametersAreNonnullByDefault
public class JettyRestErrorHandler extends ErrorPageErrorHandler {
  private static final String GENERIC_ERROR_CODE = "GenericError";

  private final List<HttpMessageConverter<Object>> messageConverters = Arrays.asList(
      // Json should be the first one as it takes priority over binary error representation when error page
      // is opened in the browser
      new ProtobufJsonHttpMessageConverter(),
      new ProtobufHttpMessageConverter()
  );

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
    final HttpHeaders headers = getRequestHeaders(request);

    if (canTryWriteRestError(headers)) {
      // try to write error using rest-friendly error converters
      for (final HttpMessageConverter<Object> converter : messageConverters) {
        for (final MediaType acceptMediaType : headers.getAccept()) {
          AbstractHttpConnection connection = AbstractHttpConnection.getCurrentConnection();
          if (writeRestError(
              converter,
              acceptMediaType,
              response,
              connection.getResponse().getStatus(),
              connection.getResponse().getReason())) {
            // error has been written, mark request as handled and skip default error processing
            connection.getRequest().setHandled(true);
            return;
          }
        }
      }
    }

    // error left unhandled - delegate to default page error handler
    super.handle(target, baseRequest, request, response);
  }

  @Override
  protected void writeErrorPageBody(
      HttpServletRequest request,
      Writer writer,
      int code,
      String message,
      boolean showStacks) throws IOException {
    // Code below disables "Powered by Jetty" error message
    final String uri= request.getRequestURI();

    writeErrorPageMessage(request, writer, code, message, uri);

    if (showStacks) {
      writeErrorPageStacks(request, writer);
    }

    writer.write("<hr />");
  }

  //
  // Private
  //

  private boolean canTryWriteRestError(HttpHeaders headers) {
    for (final MediaType acceptMediaType : headers.getAccept()) {
      if (MediaType.TEXT_HTML.isCompatibleWith(acceptMediaType)) {
        // prefer text/html error description whenever possible
        return false;
      }
    }

    return true;
  }

  private boolean writeRestError(
      HttpMessageConverter<Object> messageConverter,
      MediaType acceptType,
      HttpServletResponse response,
      int statusCode,
      String reason) throws IOException {
    // get target content type
    MediaType errorContentType = null;
    for (MediaType candidate : messageConverter.getSupportedMediaTypes()) {
      if (candidate.isCompatibleWith(acceptType) &&
          messageConverter.canWrite(ErrorV1.ErrorResponse.class, candidate)) {
        errorContentType = candidate;
        break;
      }
    }

    if (errorContentType == null) {
      return false;
    }

    // set response status
    response.setStatus(statusCode);
    messageConverter.write(
        getErrorResponse(statusCode, reason),
        errorContentType,
        new ServletServerHttpResponse(response));

    return true;
  }

  private ErrorV1.ErrorResponse getErrorResponse(int statusCode, @Nullable String reason) {
    String code = GENERIC_ERROR_CODE;
    String message = reason != null ? reason : "";
    for (final StandardRestErrorCode errorCode : StandardRestErrorCode.values()) {
      if (errorCode.getHttpCode() == statusCode) {
        code = errorCode.getCodeName();
        if (StringUtils.isEmpty(message)) {
          message = errorCode.getDescription();
        }
      }
    }

    return ErrorV1.ErrorResponse.newBuilder()
        .setError(ErrorV1.Error.newBuilder()
            .setSource(IdentityRestErrors.SOURCE)
            .setCode(code)
            .setMessage(message)
            .build())
        .build();
  }

  private HttpHeaders getRequestHeaders(HttpServletRequest request) throws IOException {
    HttpHeaders headers = new HttpHeaders();
    setHeaderValue(HttpHeaders.ACCEPT, headers, request);
    setHeaderValue(HttpHeaders.CONTENT_TYPE, headers, request);
    return headers;
  }

  private static void setHeaderValue(String headerName, HttpHeaders target, HttpServletRequest request) {
    final String headerValue = request.getHeader(headerName);
    if (StringUtils.isEmpty(headerValue)) {
      return;
    }

    target.set(headerName, headerValue);
  }
}
