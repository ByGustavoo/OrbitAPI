package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrazoService {

    private final TarefaRepository tarefaRepository;
    private final FusoHorarioService fusoHorarioService;
    private static final List<Situacao> EM_ABERTO = List.of(Situacao.PENDENTE, Situacao.EM_ANDAMENTO);
    private static final Comparator<Tarefa> ORDEM_NA_SERIE = Comparator.comparing(Tarefa::getData, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(tarefa -> Objects.requireNonNullElse(tarefa.getHorarioInicio(), LocalTime.MIDNIGHT));

    public Prazo calcular(Tarefa tarefa, Instant agora) {
        var fuso = fusoHorarioService.obter();
        var prazo = calcular(tarefa, agora, fuso);

        if (prazo != Prazo.ATRASADA || tarefa.getSerie() == null) {
            return prazo;
        }

        var temAtrasadaPosterior = tarefaRepository.findBySerieIdAndSituacaoInAndDataBetween(tarefa.getSerie().getId(), EM_ABERTO, tarefa.getData(), LocalDate.ofInstant(agora, fuso))
                .stream()
                .filter(ocorrencia -> ORDEM_NA_SERIE.compare(ocorrencia, tarefa) > 0)
                .anyMatch(ocorrencia -> calcular(ocorrencia, agora, fuso) == Prazo.ATRASADA);

        return temAtrasadaPosterior ? Prazo.NAO_REALIZADA : prazo;
    }

    private Prazo calcular(Tarefa tarefa, Instant agora, ZoneId fuso) {
        var limite = limite(tarefa, fuso);

        if (tarefa.getSituacao() == Situacao.CONCLUIDA) {
            var concluidaNoPrazo = limite == null || tarefa.getDataConclusao() == null || !tarefa.getDataConclusao().isAfter(limite);
            return concluidaNoPrazo ? Prazo.CONCLUIDA_NO_PRAZO : Prazo.CONCLUIDA_COM_ATRASO;
        }

        if (limite == null) {
            return Prazo.SEM_DATA;
        }

        if (tarefa.getSituacao() == Situacao.CANCELADA) {
            return Prazo.NO_PRAZO;
        }

        return agora.isBefore(limite) ? Prazo.NO_PRAZO : Prazo.ATRASADA;
    }

    private Instant limite(Tarefa tarefa, ZoneId fuso) {
        if (tarefa.getData() == null) {
            return null;
        }

        if (tarefa.isDiaInteiro() || tarefa.getHorarioFim() == null) {
            return tarefa.getData().plusDays(1).atStartOfDay(fuso).toInstant();
        }

        return tarefa.getData().atTime(tarefa.getHorarioFim()).atZone(fuso).toInstant();
    }
}