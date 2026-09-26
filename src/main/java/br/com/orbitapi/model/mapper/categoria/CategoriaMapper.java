package br.com.orbitapi.model.mapper.categoria;

import br.com.orbitapi.model.dto.categoria.CategoriaDTO;
import br.com.orbitapi.model.dto.categoria.CategoriaEnvioDTO;
import br.com.orbitapi.model.entity.categoria.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaDTO toDTO(Categoria categoria, long quantidadeTarefas);

    @Mapping(target = "id", ignore = true)
    Categoria toEntity(CategoriaEnvioDTO categoriaEnvioDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CategoriaEnvioDTO categoriaEnvioDTO, @MappingTarget Categoria categoria);
}