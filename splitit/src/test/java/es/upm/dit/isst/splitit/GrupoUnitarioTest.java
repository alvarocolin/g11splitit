package es.upm.dit.isst.splitit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;

import java.util.Set;

@SpringBootTest

public class GrupoUnitarioTest {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    final void testGrupo() {
        Grupo grupo = new Grupo();
        Usuario admin = new Usuario();

        // Crear usuario como admin del grupo
        admin.setNombre("Ana Julia");
        admin.setEmail("anajulia@email.com");
        admin.setPassword("1234");
        admin.setAuth("ROLE_USER");
        usuarioRepository.save(admin);

        Set<Usuario> miembros = new HashSet<>();
        miembros.add(admin);

        // Crear grupo con ese usuario como admin y único miembro
        grupo.setNombre("Viaje Madrid");
        grupo.setAdmin(admin);
        grupo.setMiembros(miembros);
        grupo.setNumGastos(10L);
        grupo.setTotal(10.0);
        grupo.setSaldado(false);

        Grupo guardado = grupoRepository.save(grupo);

        // Verificar que se guardó correctamente
        Optional<Grupo> encontrado = grupoRepository.findById(guardado.getId());
        assertTrue(encontrado.isPresent());

        Grupo grupoRecuperado = encontrado.get();
        assertEquals("Viaje Madrid", grupoRecuperado.getNombre());
        assertEquals(10L, grupoRecuperado.getNumGastos());
        assertEquals(10.0, grupoRecuperado.getTotal());
        assertEquals(admin.getEmail(), grupoRecuperado.getAdmin().getEmail());
        assertFalse(grupoRecuperado.isSaldado());

        grupoRepository.delete(grupoRecuperado);
        usuarioRepository.delete(admin);
    }

}
