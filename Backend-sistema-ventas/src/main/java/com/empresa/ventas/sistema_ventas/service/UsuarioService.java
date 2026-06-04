package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.dto.request.RegisterRequest;
import com.empresa.ventas.sistema_ventas.entity.auth.Rol;
import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.RolRepository;
import com.empresa.ventas.sistema_ventas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<Usuario> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    @Transactional
    public Usuario crear(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }

        Rol rolVendedor = rolRepository.findByNombre("VENDEDOR")
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", "VENDEDOR"));

        Usuario usuario = Usuario.builder()
                .uuid(UUID.randomUUID())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .roles(Set.of(rolVendedor))
                .build();

        usuario.setActivo(true);
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, RegisterRequest request) {
        Usuario usuario = buscarPorId(id);

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setDni(request.getDni());
        usuario.setTelefono(request.getTelefono());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void toggleActivo(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void asignarRol(Long usuarioId, Long rolId) {
        Usuario usuario = buscarPorId(usuarioId);
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));
        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }
}