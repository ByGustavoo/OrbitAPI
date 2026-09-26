package br.com.orbitapi.model.mapper.sessao;

import br.com.orbitapi.model.dto.sessao.SessaoEnvioDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEstudoDTO;
import br.com.orbitapi.model.entity.sessao.SessaoEstudo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SessaoMapper {

    SessaoEstudoDTO toDTO(SessaoEstudo sessaoEstudo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tarefa", ignore = true)
    @Mapping(target = "atividade", ignore = true)
    SessaoEstudo toEntity(SessaoEnvioDTO sessaoEnvioDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modo", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "tarefa", ignore = true)
    @Mapping(target = "atividade", ignore = true)
    @Mapping(target = "ciclosConcluidos", ignore = true)
    void updateEntity(SessaoEnvioDTO sessaoEnvioDTO, @MappingTarget SessaoEstudo sessaoEstudo);
}