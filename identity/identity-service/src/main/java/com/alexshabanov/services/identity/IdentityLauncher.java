package com.alexshabanov.services.identity;

import com.alexshabanov.services.identity.model.error.IdentityRestErrors;
import com.truward.brikar.error.jetty.StandardJettyRestErrorHandler;
import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.annotation.Nonnull;

/**
 * Launcher for identity service.
 */
public final class IdentityLauncher {

  public static void main(String[] args) throws Exception {
    try (StandardLauncher launcher = new StandardLauncher("classpath:/identityService/") {
      @Override
      protected void initServlets(@Nonnull ServletContextHandler contextHandler) {
        super.initServlets(contextHandler);

        // Set REST-friendly error handler
        contextHandler.setErrorHandler(new StandardJettyRestErrorHandler(IdentityRestErrors.SOURCE));
      }
    }) {
      // Start launcher with enabled spring security but disabled sessions and disabled static handler
      launcher
          .setSpringSecurityEnabled(true)
          .setSessionsEnabled(false)
          .setStaticHandlerEnabled(false)
          .start();
    }
  }
}
