package mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class NotaDAO {
    private final MongoCollection<Document> collection;

    public NotaDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.collection = database.getCollection("Notas");
    }

    // Crear una nueva nota
    public String crearNota(String titulo, String contenido, String autor, List<String> etiquetas, String prioridad) {
        Document nota = new Document("titulo", titulo)
                .append("contenido", contenido)
                .append("fecha", java.time.LocalDate.now().toString())
                .append("tags", etiquetas)
                .append("prioridad", prioridad)
                .append("estado", "pendiente")
                .append("autor", autor)
                .append("compartidoCon", new ArrayList<String>());

        collection.insertOne(nota);
        System.out.println("Nota creada con éxito!");
        return nota.getObjectId("_id").toHexString();
    }

    // Buscar una nota por título (con regex)
    public Document buscarNota(String titulo) {
        return collection.find(new Document("titulo", new Document("$regex", titulo).append("$options", "i"))).first();
    }

    // Listar todas las notas
    public List<Document> listarNotas() {
        return collection.find().into(new ArrayList<>());
    }

    // Marcar nota como completada
    public void marcarComoCompletada(String titulo) {
        collection.updateOne(new Document("titulo", titulo),
                new Document("$set", new Document("estado", "completada")));
    }

    public void compartirNotaConPersona(String notaId, String nombrePersona) {
        collection.updateOne(new Document("_id", new org.bson.types.ObjectId(notaId)),
                new Document("$push", new Document("compartidoCon", nombrePersona)));
    }

    public List<Document> buscarNotasPorContenido(String palabraClave) {
        return MongoDBConnection.getDatabase()
                .getCollection("Notas")
                .find(Filters.regex("contenido", ".*" + palabraClave + ".*", "i"))
                .into(new ArrayList<>());
    }

    public void eliminarNota(String notaId) {
        MongoDBConnection.getDatabase()
                .getCollection("Notas")
                .deleteOne(new Document("_id", new ObjectId(notaId)));

        MongoDBConnection.getDatabase()
                .getCollection("Personas")
                .updateMany(new Document(),
                        new Document("$pull", new Document("notasPropias", notaId)));

        MongoDBConnection.getDatabase()
                .getCollection("Personas")
                .updateMany(new Document(),
                        new Document("$pull", new Document("notasCompartidas", notaId)));

        System.out.println("Nota eliminada correctamente.");
    }



}
