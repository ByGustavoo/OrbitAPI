package br.com.orbitapi.model.mapper.atividade;

import br.com.orbitapi.model.dto.atividade.AtividadeEnvioDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEstudoDTO;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AtividadeMapper {

    AtividadeEstudoDTO toDTO(AtividadeEstudo atividadeEstudo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "arquivada", ignore = true)
    AtividadeEstudo toEntity(AtividadeEnvioDTO atividadeEnvioDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "arquivada", ignore = true)
    void updateEntity(AtividadeEnvioDTO atividadeEnvioDTO, @MappingTarget AtividadeEstudo atividadeEstudo);
}