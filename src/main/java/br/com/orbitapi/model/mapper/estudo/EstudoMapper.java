package br.com.orbitapi.model.mapper.estudo;

import br.com.orbitapi.model.dto.estudo.AtividadeMaisEstudadaDTO;
import br.com.orbitapi.model.dto.estudo.MinutosAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.ProgressoMetaDTO;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstudoMapper {

    @Mapping(target = "atividade", source = "atividadeEstudo")
    @Mapping(target = "metaMinutos", source = "atividadeEstudo.metaSemanalMinutos")
    ProgressoMetaDTO toProgressoMeta(AtividadeEstudo atividadeEstudo, long minutosRealizados);

    @Mapping(target = "id", source = "atividade.id")
    @Mapping(target = "cor", source = "atividade.cor")
    @Mapping(target = "nome", source = "atividade.nome")
    AtividadeMaisEstudadaDTO toAtividadeMaisEstudada(MinutosAtividadeDTO minutosAtividadeDTO);
}