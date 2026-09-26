package br.com.orbitapi.validation;

import br.com.orbitapi.model.dto.tarefa.TarefaEnvioDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TarefaConsistenteValidator implements ConstraintValidator<TarefaConsistente, TarefaEnvioDTO> {

    private static final Set<Integer> LEMBRETES = Set.of(0, 5, 15, 30, 60);
    private static final DateTimeFormatter HORARIO = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA_CURTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public boolean isValid(TarefaEnvioDTO tarefa, ConstraintValidatorContext context) {
        var erros = new LinkedHashMap<String, String>();

        validarHorarios(tarefa, erros);
        validarLembrete(tarefa, erros);
        validarRecorrencia(tarefa, erros);

        context.disableDefaultConstraintViolation();
        erros.forEach((campo, mensagem) -> context.buildConstraintViolationWithTemplate(mensagem)
                .addPropertyNode(campo)
                .addConstraintViolation());

        return erros.isEmpty();
    }

    private void validarHorarios(TarefaEnvioDTO tarefa, Map<String, String> erros) {
        if (tarefa.horarioFim() == null) {
            return;
        }

        if (tarefa.horarioInicio() == null) {
            erros.put("horarioFim", "Informe o horário de início antes do fim!");
        } else if (!tarefa.horarioFim().isAfter(tarefa.horarioInicio())) {
            erros.put("horarioFim", "O fim precisa ser depois do início (%s)!".formatted(HORARIO.format(tarefa.horarioInicio())));
        }
    }

    private void validarLembrete(TarefaEnvioDTO tarefa, Map<String, String> erros) {
        if (tarefa.lembreteMinutosAntes() != null && !LEMBRETES.contains(tarefa.lembreteMinutosAntes())) {
            erros.put("lembreteMinutosAntes", "Escolha um dos lembretes da lista!");
        }
    }

    private void validarRecorrencia(TarefaEnvioDTO tarefa, Map<String, String> erros) {
        var recorrencia = tarefa.recorrencia();

        if (recorrencia == null) {
            return;
        }

        if (recorrencia.frequencia() == null) {
            erros.put("frequencia", "Escolha uma das frequências da lista!");
        }

        if (recorrencia.diasSemana() != null && recorrencia.diasSemana().isEmpty()) {
            erros.put("diasSemana", "Escolha pelo menos um dia da semana!");
        }

        if (recorrencia.dataFim() != null && recorrencia.dataFim().isBefore(tarefa.data())) {
            erros.put("dataFim", "O término precisa ser a partir da primeira ocorrência (%s)!".formatted(DATA_CURTA.format(tarefa.data())));
        }
    }
}