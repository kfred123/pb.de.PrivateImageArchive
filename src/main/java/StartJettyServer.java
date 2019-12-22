
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import pia.database.Database;
import pia.rest.ImageService;
import pia.rest.MyApplication;

public class StartJettyServer {

    // pia.rest
    public static void main(String[] args) throws Exception {
        Database.initDatabase();

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
