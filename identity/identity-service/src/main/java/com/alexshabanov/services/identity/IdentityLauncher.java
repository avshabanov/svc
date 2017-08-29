package com.alexshabanov.services.identity;

import com.truward.brikar.server.launcher.StandardLauncher;

/**
 * Launcher for identity service.
 */
public final class IdentityLauncher {

  public static void main(String[] args) throws Exception {
    try (StandardLauncher launcher = new StandardLauncher("classpath:/identityService/")) {
      launcher
          .setSpringSecurityEnabled(true)
          .start();
    }
  }
}
