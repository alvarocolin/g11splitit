package es.upm.dit.isst.splitit;

import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegistroUsuarioTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testCrearUsuario() {
        System.out.println("🚀 Iniciando test: testCrearUsuario");

        Usuario u = new Usuario();
        u.setNombre("Juan");
        u.setEmail("juan@example.com");
        u.setPassword(passwordEncoder.encode("1234"));
        u.setAuth("ROLE_USER");

        usuarioRepository.save(u);
        System.out.println("✅ Usuario 'Juan' guardado en la base de datos");

        Usuario guardado = usuarioRepository.findByEmail("juan@example.com")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        assertNotNull(guardado);
        System.out.println("🔍 Usuario encontrado por email");

        assertTrue(passwordEncoder.matches("1234", guardado.getPassword()));
        System.out.println("🔐 Contraseña validada correctamente con BCrypt");

        assertEquals("ROLE_USER", guardado.getAuth());
        System.out.println("🎯 Rol del usuario es correcto");

        System.out.println("🎉 TEST testCrearUsuario COMPLETADO CON ÉXITO\n");
    }

    @Test
    public void testEmailDuplicado() {
        System.out.println("🚀 Iniciando test: testEmailDuplicado");

        Usuario u1 = new Usuario();
        u1.setNombre("Ana");
        u1.setEmail("ana@example.com");
        u1.setPassword(passwordEncoder.encode("abcd"));
        u1.setAuth("ROLE_USER");

        usuarioRepository.save(u1);
        System.out.println("✅ Primer usuario 'Ana' guardado correctamente");

        Usuario u2 = new Usuario();
        u2.setNombre("Otra Ana");
        u2.setEmail("ana@example.com"); // Email duplicado
        u2.setPassword(passwordEncoder.encode("efgh"));
        u2.setAuth("ROLE_USER");

        try {
            usuarioRepository.save(u2);
            fail("❌ ERROR: ¡Se guardó un usuario con email duplicado!");
        } catch (Exception e) {
            System.out.println("✅ EXCEPCIÓN ESPERADA: No se pudo guardar email duplicado");
        }

        System.out.println("🎉 TEST testEmailDuplicado COMPLETADO CON ÉXITO\n");
    }

    @Test
    public void testSimulacionInicioSesion() {
        System.out.println("🚀 Iniciando test: testSimulacionInicioSesion");

        String email = "lucia@example.com";
        String passwordPlano = "secreto123";

        Usuario u = new Usuario();
        u.setNombre("Lucía");
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(passwordPlano));
        u.setAuth("ROLE_USER");

        usuarioRepository.save(u);
        System.out.println("✅ Usuario 'Lucía' registrado con contraseña cifrada");

        Usuario recuperado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        assertNotNull(recuperado);
        System.out.println("🔍 Usuario 'Lucía' recuperado por email");

        boolean passwordCorrecta = passwordEncoder.matches(passwordPlano, recuperado.getPassword());
        assertTrue(passwordCorrecta);
        System.out.println("🔐 Contraseña introducida coincide con la cifrada");

        System.out.println("🎉 TEST testSimulacionInicioSesion COMPLETADO CON ÉXITO\n");
    }
}