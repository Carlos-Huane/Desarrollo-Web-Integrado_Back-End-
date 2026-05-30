package com.grupo6.intranet.controllers;

import com.grupo6.intranet.dtos.CategoriaRankingDto;
import com.grupo6.intranet.dtos.TecnicoRankingDto;
import com.grupo6.intranet.services.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping("/categorias")
    public List<CategoriaRankingDto> rankingCategorias() {
        return rankingService.rankingCategorias();
    }

    @GetMapping("/categorias/top/{limite}")
    public List<CategoriaRankingDto> topCategorias(@PathVariable int limite) {
        return rankingService.topCategorias(limite);
    }

    @GetMapping("/tecnicos")
    public List<TecnicoRankingDto> rankingTecnicos() {
        return rankingService.rankingTecnicos();
    }

    @GetMapping("/tecnicos/top/{limite}")
    public List<TecnicoRankingDto> topTecnicos(@PathVariable int limite) {
        return rankingService.topTecnicos(limite);
    }
}
