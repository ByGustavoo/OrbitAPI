package br.com.orbitapi.model.mapper.revisao;

import br.com.orbitapi.model.dto.revisao.NotaSemanaDTO;
import br.com.orbitapi.model.entity.revisao.NotaSemana;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotaSemanaMapper {

    NotaSemanaDTO toDTO(NotaSemana notaSemana);
}