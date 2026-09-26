package br.com.orbitapi.repository.atividade;

import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtividadeRepository extends JpaRepository<AtividadeEstudo, Long> {

    Optional<AtividadeEstudo> findByNomeIgnoreCaseAndArquivadaFalse(String nome);

    List<AtividadeEstudo> findByArquivadaFalseAndMetaSemanalMinutosNotNull();
}