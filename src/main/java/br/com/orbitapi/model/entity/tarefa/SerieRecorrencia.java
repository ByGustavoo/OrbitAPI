package br.com.orbitapi.model.entity.tarefa;

import br.com.orbitapi.enums.DiaSemana;
import br.com.orbitapi.enums.Frequencia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "series_recorrencia", schema = "orbitapi")
public class SerieRecorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_inicial", nullable = false)
    private LocalDate dataInicial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Frequencia frequencia;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    @CollectionTable(name = "series_recorrencia_dias", schema = "orbitapi", joinColumns = @JoinColumn(name = "serie_id"))
    private Set<DiaSemana> diasSemana = new HashSet<>();

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "gerada_ate", nullable = false)
    private LocalDate geradaAte;
}