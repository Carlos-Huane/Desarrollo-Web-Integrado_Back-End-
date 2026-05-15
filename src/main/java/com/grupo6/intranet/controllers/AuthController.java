package com.grupo6.intranet.controllers;

import com.grupo6.intranet.config.JwtUtil;
import com.grupo6.intranet.dtos.LoginRequest;
import com.grupo6.intranet.dtos.LoginResponse;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return usuarioService.buscarPorEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .filter(Usuario::getActivo)
                .map(u -> {
                    String token = jwtUtil.generarToken(u.getEmail(), u.getRol().name());
                    return ResponseEntity.ok(new LoginResponse(
                            token,
                            u.getEmail(),
                            u.getRol().name(),
                            u.getNombre() + " " + u.getApellido()
                    ));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
