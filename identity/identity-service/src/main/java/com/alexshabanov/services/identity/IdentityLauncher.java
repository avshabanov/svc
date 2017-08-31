package com.alexshabanov.services.identity;

import com.truward.brikar.server.launcher.StandardLauncher;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Nonnull;

/**
 * Launcher for identity service.
 */
public final class IdentityLauncher {

  public static void main(String[] args) throws Exception {
    try (StandardLauncher launcher = new StandardLauncher("classpath:/identityService/") {
      @Override
      protected void initServlets(@Nonnull ServletContextHandler contextHandler) {
        final ServletHolder dispatcherServlet = contextHandler.addServlet(DispatcherServlet.class,
            getDispatcherServletMapping());
        dispatcherServlet.setInitParameter("contextConfigLocation", getDispatcherServletConfigLocations());
        dispatcherServlet.setInitParameter("throwExceptionIfNoHandlerFound", "true");
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
