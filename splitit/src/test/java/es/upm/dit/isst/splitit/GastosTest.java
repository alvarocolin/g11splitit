package es.upm.dit.isst.splitit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.upm.dit.isst.splitit.model.Gasto;
import es.upm.dit.isst.splitit.model.Grupo;
import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.GastoRepository;
import es.upm.dit.isst.splitit.repository.GrupoRepository;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import java.util.Set;

@SpringBootTest
public class GastosTest {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private GastoRepository gastoRepository;

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
        grupo.setNumGastos(0L);
        grupo.setTotal(0.0);

        Set<Usuario> miembros = new HashSet<>();
        miembros.add(admin);
        miembros.add(user1);
        miembros.add(user2);
        grupo.setMiembros(miembros);

        // Guardar en repositorio
        grupoRepository.save(grupo);
        
        Gasto gasto1 = new Gasto();
        gasto1.setGrupo(grupo);
        gasto1.setCantidad(50.0);
        gasto1.setConcepto("Comida");
        Date date = new Date();
        gasto1.setFecha(date);
        gasto1.setPagador(user1);
        Gasto gastoGuardado = gastoRepository.save(gasto1);

        grupo.addGasto(gasto1.getCantidad());
        grupoRepository.save(grupo);

        Optional<Gasto> encontrado = gastoRepository.findById(gastoGuardado.getId());
        assertTrue(encontrado.isPresent());

        Gasto gastoRecuperado = encontrado.get();

        assertEquals("Comida", gastoRecuperado.getConcepto());
        assertEquals(50.0, gastoRecuperado.getCantidad());
        assertEquals(date, gastoRecuperado.getFecha());
        assertEquals(user1, gastoRecuperado.getPagador());
        assertEquals(1L, grupo.getNumGastos());
        assertEquals(50.0, grupo.getTotal());

        grupoRepository.delete(grupo);
        usuarioRepository.delete(admin);
        usuarioRepository.delete(user1);
        usuarioRepository.delete(user2);
        gastoRepository.delete(gastoRecuperado);
    }
}
