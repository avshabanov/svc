package com.alexshabanov.services.identity.controller;

import com.google.protobuf.StringValue;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for identity diagnostics resources.
 */
@Controller
@RequestMapping("/api/diagnostics")
public final class IdentityDiagnosticsController {

  @RequestMapping("status")
  @ResponseBody
  public StringValue getDiagnosticStatus(
      @RequestParam(value = "mode", required = false) String modeName) {
    final DiagnosticMode mode = getDiagnosticMode(modeName);
    return StringValue.newBuilder()
        .setValue("Generated at " + System.currentTimeMillis() + " for mode=" + mode)
        .build();
  }

  //
  // Private
  //

  private enum DiagnosticMode {

    FAST,

    FULL
  }

  private static DiagnosticMode getDiagnosticMode(String modeName) {
    if (StringUtils.isEmpty(modeName)) {
      return DiagnosticMode.FAST;
    }

    switch (modeName) {
      case "fast":
        return DiagnosticMode.FAST;

      case "full":
        return DiagnosticMode.FULL;

      default:
        throw new IllegalArgumentException("Unknown mode=" +  modeName);
    }
  }
}
