import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pia.tools.Debug;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    public static void main(String[] args) {
        try {
            StartJettyServer.main(args);
        } catch (Exception e) {
            logger.error("error occurred", e);
        }
    }
}
