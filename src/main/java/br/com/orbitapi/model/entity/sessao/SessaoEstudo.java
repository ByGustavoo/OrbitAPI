package br.com.orbitapi.model.entity.sessao;

import br.com.orbitapi.enums.ModoCronometro;
import br.com.orbitapi.enums.OrigemSessao;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sessoes", schema = "orbitapi")
public class SessaoEstudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atividade_id")
    private AtividadeEstudo atividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id")
    private Tarefa tarefa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ModoCronometro modo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrigemSessao origem;

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Column(name = "duracao_segundos", nullable = false)
    private int duracaoSegundos;

    @Column(name = "ciclos_concluidos")
    private Integer ciclosConcluidos;

    @Column(length = 500)
    private String observacao;
}