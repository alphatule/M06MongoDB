package alex.gestion.notas.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static final String URI = "mongodb://localhost:27017/";
    private static final String DATABASE_NAME = "gestion";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // Método para obtener la conexión a MongoDB
    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
        System.out.println("Intentando conectar con MongoDB en: " + URI);
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
        }
        return database;
    }

    // Método para cerrar la conexión
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}

