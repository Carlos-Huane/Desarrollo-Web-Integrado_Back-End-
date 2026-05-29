package com.grupo6.intranet.services;

import com.grupo6.intranet.dtos.CategoriaRankingDto;
import com.grupo6.intranet.dtos.TecnicoRankingDto;
import com.grupo6.intranet.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<CategoriaRankingDto> rankingCategorias() {
        return ticketRepository.rankingCategoriasConTiempo().stream()
                .map(row -> new CategoriaRankingDto(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2],
                        row[3] != null ? ((Number) row[3]).doubleValue() : 0.0))
                .collect(Collectors.toList());
    }

    public List<CategoriaRankingDto> topCategorias(int limite) {
        return rankingCategorias().stream().limit(limite).collect(Collectors.toList());
    }

    public List<TecnicoRankingDto> rankingTecnicos() {
        return ticketRepository.rankingTecnicosConTiempo().stream()
                .map(row -> new TecnicoRankingDto(
                        (Long) row[0],
                        row[1] + " " + row[2],
                        (Long) row[3],
                        row[4] != null ? ((Number) row[4]).doubleValue() : 0.0))
                .collect(Collectors.toList());
    }

    public List<TecnicoRankingDto> topTecnicos(int limite) {
        return rankingTecnicos().stream().limit(limite).collect(Collectors.toList());
    }
}
