package es.upm.dit.isst.splitit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;

@SpringBootApplication
public class SplititApplication {

	@Bean
	public CommandLineRunner loadData(UsuarioRepository usuarioRepository) {
		return args -> {
			if (usuarioRepository.findByEmail("alvarocolin@example.com") == null) {
				Usuario u1 = new Usuario();
				u1.setNombre("Alvaro");
				u1.setEmail("alvarocolin@example.com");
				usuarioRepository.save(u1);
			}
			if (usuarioRepository.findByEmail("juan@example.com") == null) {
				Usuario u2 = new Usuario();
				u2.setNombre("Juan");
				u2.setEmail("juan@example.com");
				usuarioRepository.save(u2);
			}
			if (usuarioRepository.findByEmail("maria@example.com") == null) {
				Usuario u3 = new Usuario();
				u3.setNombre("Maria");
				u3.setEmail("maria@example.com");
				usuarioRepository.save(u3);
			}
			if (usuarioRepository.findByEmail("luis@example.com") == null) {
				Usuario u4 = new Usuario();
				u4.setNombre("Luis");
				u4.setEmail("luis@example.com");
				usuarioRepository.save(u4);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(SplititApplication.class, args);
	}

}
