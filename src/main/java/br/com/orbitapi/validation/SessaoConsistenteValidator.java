package br.com.orbitapi.validation;

import br.com.orbitapi.model.dto.sessao.SessaoEnvioDTO;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SessaoConsistenteValidator implements ConstraintValidator<SessaoConsistente, SessaoEnvioDTO> {

    private final Clock clock;
    private final FusoHorarioService fusoHorarioService;

    @Override
    public boolean isValid(SessaoEnvioDTO sessao, ConstraintValidatorContext context) {
        var erros = new LinkedHashMap<String, String>();
        var limiteFuturo = Instant.now(clock).plusSeconds(60);

        if (sessao.inicio() != null && sessao.inicio().isAfter(limiteFuturo)) {
            erros.put("inicio", "A sessão não pode começar depois de agora!");
        }

        validarDuracao(sessao, limiteFuturo, erros);

        context.disableDefaultConstraintViolation();
        erros.forEach((campo, mensagem) -> context.buildConstraintViolationWithTemplate(mensagem)
                .addPropertyNode(campo)
                .addConstraintViolation());

        return erros.isEmpty();
    }

    private void validarDuracao(SessaoEnvioDTO sessao, Instant limiteFuturo, Map<String, String> erros) {
        var duracao = sessao.duracaoSegundos();

        if (duracao == null || duracao < 60 || duracao > 86400) {
            return;
        }

        if (sessao.fim() == null) {
            erros.put("duracaoSegundos", "Informe a duração da sessão!");
        } else if (sessao.inicio() == null) {
            return;
        } else if (sessao.fim().isBefore(sessao.inicio())) {
            erros.put("duracaoSegundos", "O fim da sessão precisa ser depois do início!");
        } else if (duracao * 1000L > Duration.between(sessao.inicio(), sessao.fim()).toMillis() + 1000) {
            erros.put("duracaoSegundos", "A duração não pode passar do tempo entre o início e o fim!");
        } else if (sessao.fim().isAfter(limiteFuturo) && !erros.containsKey("inicio")) {
            var horario = DateTimeFormatter.ofPattern("HH:mm").withZone(fusoHorarioService.obter()).format(sessao.fim());
            erros.put("duracaoSegundos", "Com essa duração, a sessão terminaria às %s, depois de agora. Ajuste o início ou a duração!".formatted(horario));
        }
    }
}