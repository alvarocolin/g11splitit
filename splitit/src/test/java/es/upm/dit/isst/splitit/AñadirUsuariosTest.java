package es.upm.dit.isst.splitit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import java.util.Set;

@SpringBootTest
public class AñadirUsuariosTest {

    @Autowired
    private GrupoRepository grupoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @Transactional
    final void testCrearGrupoConParticipantes() {
        // Crear usuarios
        Usuario admin = new Usuario();
        admin.setNombre("Jose");
        admin.setEmail("jose@example.com");
        admin.setPassword("adminpass");
        admin.setAuth("ROLE_USER");

        Usuario user1 = new Usuario();
        user1.setNombre("Pepa");
        user1.setEmail("pepa@example.com");
        user1.setPassword("maria123");
        user1.setAuth("ROLE_USER");

        Usuario user2 = new Usuario();
        user2.setNombre("Marcos");
        user2.setEmail("marcos@example.com");
        user2.setPassword("luis123");
        user2.setAuth("ROLE_USER");

        admin = usuarioRepository.save(admin);
        user1 = usuarioRepository.save(user1);
        user2 = usuarioRepository.save(user2);

        // Crear grupo
        Grupo grupo = new Grupo();
        grupo.setNombre("Cena viernes");
        grupo.setAdmin(admin);
        grupo.setNumGastos(10L);
        grupo.setTotal(10.0);

        Set<Usuario> miembros = new HashSet<>();
        miembros.add(admin);
        miembros.add(user1);
        miembros.add(user2);
        grupo.setMiembros(miembros);

        // Guardar en repositorio
        Grupo savedGrupo = grupoRepository.save(grupo);

        Optional<Grupo> encontrado = grupoRepository.findById(savedGrupo.getId());
        assertTrue(encontrado.isPresent());

        Grupo grupoRecuperado = encontrado.get();

        // Validaciones
        assertNotNull(grupoRecuperado.getId());
        assertEquals("Cena viernes", grupoRecuperado.getNombre());
        assertTrue(grupoRecuperado.getMiembros().stream().anyMatch(u -> u.getEmail().equals("pepa@example.com")));
        assertEquals("jose@example.com", grupoRecuperado.getAdmin().getEmail());

        grupoRepository.delete(grupoRecuperado);
        usuarioRepository.delete(admin);
        usuarioRepository.delete(user1);
        usuarioRepository.delete(user2);
    }
}
