package br.com.orbitapi.model.entity.atividade;

import br.com.orbitapi.enums.Cor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "atividades", schema = "orbitapi")
public class AtividadeEstudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Cor cor;

    @Column(name = "meta_semanal_minutos")
    private Integer metaSemanalMinutos;

    @Column(nullable = false)
    private boolean arquivada;
}