package br.com.orbitapi.model.entity.tarefa;

import br.com.orbitapi.enums.TipoEventoTarefa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "eventos_tarefa", schema = "orbitapi")
public class EventoTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEventoTarefa tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarefa_id")
    private Tarefa tarefa;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(name = "ocorrido_em", nullable = false)
    private Instant ocorridoEm;

    @Column(length = 15)
    private String anterior;

    @Column(length = 15)
    private String novo;
}