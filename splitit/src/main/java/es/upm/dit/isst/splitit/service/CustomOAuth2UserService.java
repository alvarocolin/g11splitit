package es.upm.dit.isst.splitit.service;

import es.upm.dit.isst.splitit.model.Usuario;
import es.upm.dit.isst.splitit.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Mostrar todos los atributos recibidos de Google
        Map<String, Object> attributes = oauth2User.getAttributes();
        System.out.println("🔍 Atributos recibidos de Google:");
        attributes.forEach((key, value) -> System.out.println("   " + key + ": " + value));

        // Extraer campos
        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("name");

        System.out.println("📩 Email detectado: " + email);
        System.out.println("🙍 Nombre detectado: " + nombre);

        // Verificar si ya existe
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            System.out.println("🆕 Usuario no existe. Creando uno nuevo...");

            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setPassword(passwordEncoder.encode("oauth2_placeholder"));
            usuario.setAuth("ROLE_USER");

            usuarioRepository.save(usuario);
            System.out.println("✅ Usuario creado correctamente.");
        } else {
            System.out.println("✔️ Usuario ya registrado. No se crea uno nuevo.");
        }

        return oauth2User;
    }
}


