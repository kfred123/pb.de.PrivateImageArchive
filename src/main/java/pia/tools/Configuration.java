package pia.tools;

import java.io.File;

public class Configuration {
    private static final String pathToFileStorage = System.getProperty("user.home")+ File.separator + "piaFileStorage";

    public static String getPathToFileStorage() {
        return pathToFileStorage;
    }
}
