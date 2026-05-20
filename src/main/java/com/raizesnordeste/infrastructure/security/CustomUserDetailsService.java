package com.raizesnordeste.infrastructure.security;

import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        if (!usuario.isAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())))
                .build();
    }
}
