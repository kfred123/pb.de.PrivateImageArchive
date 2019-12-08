package pia.database;

public class Database {
    private static DataBaseConnection connection;
    public static void initDatabase() {
        connection = new DataBaseConnectionMongo("privateimagearchive");
    }
    public static DataBaseConnection getConnection() {
        return connection;
    }
}
