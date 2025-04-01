package mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private final MongoCollection<Document> collection;

    public PersonaDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.collection = database.getCollection("Personas");
    }

    // Registrar una nueva persona
    public void registrarPersona(String nombre, String email) {
        Document persona = new Document("nombre", nombre)
                .append("email", email)
                .append("notasPropias", new ArrayList<String>())
                .append("notasCompartidas", new ArrayList<String>());

        collection.insertOne(persona);
        System.out.println("Persona registrada con éxito!");
    }

    // Buscar persona por nombre
    public Document buscarPersona(String nombre) {
        return collection.find(new Document("nombre", nombre)).first();
    }

    // Listar todas las personas
    public List<Document> listarPersonas() {
        return collection.find().into(new ArrayList<>());
    }

    // Agregar nota a una persona
    public void agregarNotaAPropietario(String nombre, String notaId) {
        collection.updateOne(new Document("nombre", nombre),
                new Document("$push", new Document("notasPropias", notaId)));
    }

    // Compartir nota con otra persona
    public void compartirNota(String nombre, String notaId) {
        collection.updateOne(new Document("nombre", nombre),
                new Document("$push", new Document("notasCompartidas", notaId)));
    }
}
