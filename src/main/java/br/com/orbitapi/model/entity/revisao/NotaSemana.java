package br.com.orbitapi.model.entity.revisao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "notas_semana", schema = "orbitapi")
public class NotaSemana {

    @Id
    @Column(name = "inicio_semana")
    private LocalDate inicioSemana;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}