package alex.gestion.notas;

import com.alphatule.model.Nota;
import com.alphatule.model.Usuario;
import com.alphatule.repository.NotaRepository;
import com.alphatule.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotaRepository notaRepository;

    // Ver todos los usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios";
    }

    // Formulario para crear un nuevo usuario
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "formUsuario";
    }

    // Guardar usuario
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    // Editar usuario
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable String id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        usuario.ifPresent(value -> model.addAttribute("usuario", value));
        return "formUsuario";
    }

    // Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable String id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }

    // Ver notas del usuario
    @GetMapping("/{id}/notas")
    public String verNotasUsuario(@PathVariable String id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            model.addAttribute("notas", usuario.get().getNotas());
            return "notas";
        } else {
            return "redirect:/usuarios";
        }
    }

    // Añadir nota a un usuario
    @GetMapping("/{id}/notas/nueva")
    public String nuevaNota(@PathVariable String id, Model model) {
        Nota nota = new Nota();
        nota.setIdUsuario(id);
        model.addAttribute("nota", nota);
        return "formNota";
    }

    @PostMapping("/{id}/notas/guardar")
    public String guardarNota(@PathVariable String id, @ModelAttribute Nota nota) {
        nota.setIdUsuario(id);
        notaRepository.save(nota);

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        usuarioOptional.ifPresent(usuario -> {
            usuario.getNotas().add(nota);
            usuarioRepository.save(usuario);
        });

        return "redirect:/usuarios/" + id + "/notas";
    }

    @GetMapping("/{idUsuario}/notas/eliminar/{idNota}")
    public String eliminarNota(@PathVariable String idUsuario, @PathVariable String idNota) {
        notaRepository.deleteById(idNota);
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        usuarioOptional.ifPresent(usuario -> {
            usuario.getNotas().removeIf(n -> n.getId().equals(idNota));
            usuarioRepository.save(usuario);
        });

        return "redirect:/usuarios/" + idUsuario + "/notas";
    }
}
