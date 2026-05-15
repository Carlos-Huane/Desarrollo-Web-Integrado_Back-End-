package com.grupo6.intranet.controllers;

import com.grupo6.intranet.models.SlaConfig;
import com.grupo6.intranet.services.SlaConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sla")
@CrossOrigin(origins = "*")
public class SlaConfigController {

    @Autowired
    private SlaConfigService slaConfigService;

    @GetMapping
    public List<SlaConfig> listarTodos() {
        return slaConfigService.listarTodos();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlaConfig> actualizar(@PathVariable Long id, @RequestBody SlaConfig datos) {
        return slaConfigService.actualizar(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
