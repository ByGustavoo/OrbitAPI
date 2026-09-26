package br.com.orbitapi.repository.revisao;

import br.com.orbitapi.model.entity.revisao.NotaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface NotaSemanaRepository extends JpaRepository<NotaSemana, LocalDate> {
}