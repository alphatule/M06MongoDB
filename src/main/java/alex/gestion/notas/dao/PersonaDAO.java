package alex.gestion.notas.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

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

    public List<Document> listarNotasDePersona(String nombre) {
        Document persona = buscarPersona(nombre);
        if (persona == null) {
            System.out.println("Persona no encontrada.");
            return new ArrayList<>();
        }

        List<String> idsNotas = new ArrayList<>();
        idsNotas.addAll((List<String>) persona.get("notasPropias"));
        idsNotas.addAll((List<String>) persona.get("notasCompartidas"));

        return MongoDBConnection.getDatabase()
                .getCollection("Notas")
                .find(new Document("_id", new Document("$in", idsNotas)))
                .into(new ArrayList<>());
    }


    // Agregar nota a una persona
    public void agregarNotaAPropietario(String nombre, String notaId) {
        collection.updateOne(new Document("nombre", nombre),
                new Document("$push", new Document("notasPropias", notaId)));
    }

    // Compartir nota con otra persona
    public void compartirNota(String nombre, String notaId) {
        Document persona = buscarPersona(nombre);
        if (persona == null) {
            System.out.println("Error: Persona no encontrada.");
            return;
        }

        Document nota = MongoDBConnection.getDatabase()
                .getCollection("Notas")
                .find(new Document("_id", new ObjectId(notaId)))
                .first();

        if (nota == null) {
            System.out.println("Error: Nota no encontrada.");
            return;
        }

        List<String> notasCompartidas = persona.getList("notasCompartidas", String.class);
        if (notasCompartidas != null && notasCompartidas.contains(notaId)) {
            System.out.println("Esta nota ya ha sido compartida con esta persona.");
            return;
        }

        collection.updateOne(new Document("nombre", nombre),
                new Document("$push", new Document("notasCompartidas", notaId)));

        MongoDBConnection.getDatabase()
                .getCollection("Notas")
                .updateOne(new Document("_id", new ObjectId(notaId)),
                        new Document("$push", new Document("compartidoCon", nombre)));

        System.out.println("Nota compartida con éxito.");
    }

}
