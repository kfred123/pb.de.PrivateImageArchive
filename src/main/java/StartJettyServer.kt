import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import pia.database.Database;

import javax.xml.crypto.Data;

public class StartJettyServer {

    // pia.rest
    public static void main(String[] args) throws Exception {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.print(lc);

        Database.INSTANCE.getConnection();
        Server server = new Server(8080);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        server.setHandler(contextHandler);

        ServletHolder servletHolder = contextHandler.addServlet(ServletContainer.class, "/rest/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter("javax.ws.rs.Application", "pia.rest.MyApplication");
        //servletHolder.setInitParameter( "com.sun.jersey.config.property.resourceConfigClass", "pia.rest.MyApplication");
        //servletHolder.setInitParameter("jersey.config.server.provider.packages",
                //MyApplication.class.getCanonicalName());
        //        "pia.rest");
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
