package com.grupo6.intranet.controllers;

import com.grupo6.intranet.config.JwtUtil;
import com.grupo6.intranet.dtos.LoginRequest;
import com.grupo6.intranet.dtos.LoginResponse;
import com.grupo6.intranet.dtos.RegistroClienteRequest;
import com.grupo6.intranet.models.Rol;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroClienteRequest req, BindingResult result) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            result.addError(new FieldError(
                    "registroClienteRequest",
                    "confirmPassword",
                    "Las contraseñas no coinciden"));
        }

        if (usuarioService.buscarPorEmail(req.getEmail()).isPresent()) {
            result.addError(new FieldError(
                    "registroClienteRequest",
                    "email",
                    "El email ya está registrado"));
        }

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("timestamp", LocalDateTime.now());
            respuesta.put("status", 400);
            respuesta.put("error", "Errores de validación");
            respuesta.put("errores", errores);
            return ResponseEntity.badRequest().body(respuesta);
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(req.getNombre());
        nuevo.setApellido(req.getApellido());
        nuevo.setEmail(req.getEmail());
        nuevo.setPassword(req.getPassword());
        nuevo.setTelefono(req.getTelefono());
        nuevo.setRol(Rol.CLIENTE);
        nuevo.setActivo(true);

        Usuario creado = usuarioService.crear(nuevo);
        String token = jwtUtil.generarToken(creado.getEmail(), creado.getRol().name());

        return ResponseEntity.ok(new LoginResponse(
                token,
                creado.getEmail(),
                creado.getRol().name(),
                creado.getNombre() + " " + creado.getApellido()));
    }
}
