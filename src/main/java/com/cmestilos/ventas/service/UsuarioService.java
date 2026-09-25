package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.UsuarioRequest;
import com.cmestilos.ventas.dto.UsuarioResponse;
import com.cmestilos.ventas.entity.TipoRol;
import com.cmestilos.ventas.entity.Usuario;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Ya existe un usuario con ese nombre");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(TipoRol.fromValor(request.getRol()));
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        usuarioRepository.delete(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getIdUsuario(), usuario.getUsername(), usuario.getRol().getValor());
    }
}
