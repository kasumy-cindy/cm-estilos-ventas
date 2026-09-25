package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.AuthResponse;
import com.cmestilos.ventas.dto.LoginRequest;
import com.cmestilos.ventas.entity.Usuario;
import com.cmestilos.ventas.repository.UsuarioRepository;
import com.cmestilos.ventas.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()));
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales no válidas"));
        return new AuthResponse(jwtService.generarToken(usuario), "Bearer", usuario.getIdUsuario(),
                usuario.getUsername(), usuario.getRol().getValor());
    }
}
