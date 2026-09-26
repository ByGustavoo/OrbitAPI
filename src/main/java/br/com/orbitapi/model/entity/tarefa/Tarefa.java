package br.com.orbitapi.model.entity.tarefa;

import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.entity.categoria.Categoria;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "tarefas", schema = "orbitapi")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    private LocalDate data;

    @Column(name = "dia_inteiro", nullable = false)
    private boolean diaInteiro;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Situacao situacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id")
    private AtividadeEstudo atividade;

    @Column(name = "lembrete_minutos_antes")
    private Integer lembreteMinutosAntes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private SerieRecorrencia serie;

    @Column(name = "data_conclusao")
    private Instant dataConclusao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}