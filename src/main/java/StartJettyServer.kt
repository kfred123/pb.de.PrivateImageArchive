import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.glassfish.jersey.servlet.ServletContainer
import org.slf4j.LoggerFactory
import pia.database.Database.connection
import pia.logic.StagedFileAnalyzer

object StartJettyServer {
    // pia.rest
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        // print logback's internal status
        StatusPrinter.print(lc)
        connection
        val server = Server(8080)
        val contextHandler = ServletContextHandler(ServletContextHandler.NO_SESSIONS)
        contextHandler.contextPath = "/"
        server.handler = contextHandler
        val servletHolder = contextHandler.addServlet(ServletContainer::class.java, "/rest/*")
        servletHolder.initOrder = 0
        servletHolder.setInitParameter("javax.ws.rs.Application", "pia.rest.MyApplication")
        //servletHolder.setInitParameter( "com.sun.jersey.config.property.resourceConfigClass", "pia.rest.MyApplication");
        //servletHolder.setInitParameter("jersey.config.server.provider.packages",
        //MyApplication.class.getCanonicalName());
        //        "pia.rest");
        StagedFileAnalyzer.Instance.start(1000)
        try {
            server.start()
            server.join()
        } finally {
            server.destroy()
        }
    }
}