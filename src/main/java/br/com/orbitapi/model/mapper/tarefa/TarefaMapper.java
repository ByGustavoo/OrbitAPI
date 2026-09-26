package br.com.orbitapi.model.mapper.tarefa;

import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.model.dto.tarefa.RecorrenciaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaEnvioDTO;
import br.com.orbitapi.model.entity.tarefa.SerieRecorrencia;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface TarefaMapper {

    @Mapping(target = "serieId", source = "tarefa.serie.id")
    @Mapping(target = "recorrencia", source = "tarefa.serie")
    TarefaDTO toDTO(Tarefa tarefa, Prazo prazo);

    RecorrenciaDTO toRecorrenciaDTO(SerieRecorrencia serieRecorrencia);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serie", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "atividade", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "dataConclusao", ignore = true)
    Tarefa toEntity(TarefaEnvioDTO tarefaEnvioDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "serie", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "atividade", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "dataConclusao", ignore = true)
    void updateEntity(TarefaEnvioDTO tarefaEnvioDTO, @MappingTarget Tarefa tarefa);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "geradaAte", ignore = true)
    @Mapping(target = "dataInicial", ignore = true)
    SerieRecorrencia toSerie(RecorrenciaDTO recorrenciaDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "data", source = "data")
    @Mapping(target = "criadoEm", source = "agora")
    @Mapping(target = "atualizadoEm", source = "agora")
    @Mapping(target = "dataConclusao", ignore = true)
    @Mapping(target = "situacao", constant = "PENDENTE")
    Tarefa toOcorrencia(Tarefa tarefa, LocalDate data, Instant agora);
}