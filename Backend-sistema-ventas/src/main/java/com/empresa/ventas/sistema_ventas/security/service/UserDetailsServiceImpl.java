package com.empresa.ventas.sistema_ventas.security.service;

import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        return usuario;
    }
}