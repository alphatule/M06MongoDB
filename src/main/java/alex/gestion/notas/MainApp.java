package mongodb;

import org.bson.Document;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PersonaDAO personaDAO = new PersonaDAO();
        NotaDAO notaDAO = new NotaDAO();

        int opcion;
        do {
            System.out.println("\n=== MENÚ ===");
            System.out.println("1. Registrar Persona");
            System.out.println("2. Crear Nota");
            System.out.println("3. Buscar Nota");
            System.out.println("4. Listar Notas");
            System.out.println("5. Marcar Nota como Completada");
            System.out.println("6. Compartir Nota");
            System.out.println("7. Listar Notas de una Persona");
            System.out.println("8. Buscar Nota por Contenido");
            System.out.println("10. Eliminar Nota");
            System.out.println("10. Salir");

            System.out.print("Elige una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    personaDAO.registrarPersona(nombre, email);
                    break;
                case 2:
                    System.out.print("Título de la nota: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Contenido: ");
                    String contenido = scanner.nextLine();
                    System.out.print("Autor de la nota (nombre): ");
                    String autor = scanner.nextLine();
                    System.out.print("Etiquetas (separadas por comas): ");
                    List<String> etiquetas = Arrays.asList(scanner.nextLine().split(","));
                    System.out.print("Prioridad (alta, media, baja): ");
                    String prioridad = scanner.nextLine();
                    String notaId = notaDAO.crearNota(titulo, contenido, autor, etiquetas, prioridad);
                    personaDAO.agregarNotaAPropietario(autor, notaId);
                    break;
                case 3:
                    System.out.print("Introduce el título de la nota a buscar: ");
                    String tituloBuscado = scanner.nextLine();
                    Document nota = notaDAO.buscarNota(tituloBuscado);
                    if (nota != null) {
                        System.out.println("Nota encontrada: " + nota.toJson());
                    } else {
                        System.out.println("Nota no encontrada.");
                    }
                    break;
                case 4:
                    List<Document> notas = notaDAO.listarNotas();
                    if (notas.isEmpty()) {
                        System.out.println("No hay notas registradas.");
                    } else {
                        System.out.println("Lista de notas:");
                        for (Document doc : notas) {
                            System.out.println(doc.toJson());
                        }
                    }
                    break;
                case 5:
                    System.out.print("Introduce el título de la nota a marcar como completada: ");
                    String tituloCompletar = scanner.nextLine();
                    notaDAO.marcarComoCompletada(tituloCompletar);
                    System.out.println("Nota marcada como completada.");
                    break;
                case 6:
                    System.out.print("Nombre de la persona con quien compartir: ");
                    String personaCompartir = scanner.nextLine();
                    System.out.print("ID de la nota a compartir: ");
                    String notaCompartirId = scanner.nextLine();
                    personaDAO.compartirNota(personaCompartir, notaCompartirId);
                    notaDAO.compartirNotaConPersona(notaCompartirId, personaCompartir);
                    System.out.println("Nota compartida con éxito.");
                    break;
                case 7:
                    System.out.print("Nombre de la persona: ");
                    String nombrePersona = scanner.nextLine();
                    List<Document> notasPersona = personaDAO.listarNotasDePersona(nombrePersona);
                    for (Document doc : notasPersona) {
                        System.out.println(doc.toJson());
                    }
                    break;
                case 8:
                    System.out.print("Introduce palabra clave: ");
                    String keyword = scanner.nextLine();
                    List<Document> notasEncontradas = notaDAO.buscarNotasPorContenido(keyword);
                    for (Document n : notasEncontradas) {
                        System.out.println(n.toJson());
                    }
                    break;
                case 10:
                    System.out.print("ID de la nota a eliminar: ");
                    String idEliminar = scanner.nextLine();
                    notaDAO.eliminarNota(idEliminar);
                    break;
                case 11:
                    MongoDBConnection.closeConnection();
                    System.out.println("Conexión cerrada. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida, intenta de nuevo.");
            }
        } while (opcion != 6);

        scanner.close();
    }
}


/*

No, eso es todo por el momento. Aunque me gustaria poder hacer una interfaz grafica para esta app. Me podrias guiar para hacer con spring una pequeña web en la que poder ver/añadir/editar/eliminar todo lo que hemos hecho?

Te vuelvo a pasar mi proyecto de nuevo entero en el fichero, si consideras que deberia de crear un proyecto desde 0 lo podemos hacer y luego solo seria copiar y pegar estas clases que ya tenemos creadas, para la implementación en el nuevo proyecto.
 */