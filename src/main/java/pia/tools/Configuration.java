package pia.tools;

import java.io.File;

public class Configuration {
    //private static final String pathToFileStorage = System.getProperty("user.home")+ File.separator + "piaFileStorage";
    private static final String pathToFileStorage = "C:"+ File.separator + "piaFileStorage";

    public static String getPathToFileStorage() {
        // File(System.getProperty("user.home")
        return pathToFileStorage;
    }
}
