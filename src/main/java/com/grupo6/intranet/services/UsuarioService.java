package com.grupo6.intranet.services;

import com.grupo6.intranet.models.Rol;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Optional<Usuario> actualizar(Long id, Usuario datosNuevos) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombre(datosNuevos.getNombre());
            u.setApellido(datosNuevos.getApellido());
            u.setTelefono(datosNuevos.getTelefono());
            u.setRol(datosNuevos.getRol());
            return usuarioRepository.save(u);
        });
    }

    @Transactional
    public Optional<Usuario> cambiarEstado(Long id, boolean activo) {
        return usuarioRepository.findById(id).map(u -> {
            u.setActivo(activo);
            return usuarioRepository.save(u);
        });
    }
}
