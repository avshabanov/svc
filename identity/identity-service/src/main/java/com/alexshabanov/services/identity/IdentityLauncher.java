package com.alexshabanov.services.identity;

import com.alexshabanov.services.identity.model.error.jetty.JettyRestErrorHandler;
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
//        final ServletHolder dispatcherServlet = contextHandler.addServlet(DispatcherServlet.class,
//            getDispatcherServletMapping());
//        dispatcherServlet.setInitParameter("contextConfigLocation", getDispatcherServletConfigLocations());
//        dispatcherServlet.setInitParameter("throwExceptionIfNoHandlerFound", "true");
        contextHandler.setErrorHandler(new JettyRestErrorHandler());
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
