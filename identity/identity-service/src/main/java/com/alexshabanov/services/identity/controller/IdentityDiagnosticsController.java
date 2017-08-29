package com.alexshabanov.services.identity.controller;

import com.google.protobuf.StringValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for identity diagnostics resources.
 */
@Controller
@RequestMapping("/g/diagnostics")
public final class IdentityDiagnosticsController {

  @RequestMapping("test")
  @ResponseBody
  public StringValue test() {
    return StringValue.newBuilder()
        .setValue("Generated at " + System.currentTimeMillis())
        .build();
  }
}
