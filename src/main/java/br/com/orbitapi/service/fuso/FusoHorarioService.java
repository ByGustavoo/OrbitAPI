package br.com.orbitapi.service.fuso;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Log4j2
@Service
public class FusoHorarioService {

    public ZoneId obter() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(atributos -> ((ServletRequestAttributes) atributos).getRequest().getHeader("X-Fuso-Horario"))
                .flatMap(this::converter)
                .orElse(ZoneId.of("America/Sao_Paulo"));
    }

    public Instant inicioDoDia(LocalDate data) {
        return data == null ? null : data.atStartOfDay(obter()).toInstant();
    }

    private Optional<ZoneId> converter(String fusoHorario) {
        if (ZoneId.getAvailableZoneIds().contains(fusoHorario)) {
            return Optional.of(ZoneId.of(fusoHorario));
        }

        log.warn("Fuso horário inválido no cabeçalho X-Fuso-Horario: [{}]", fusoHorario);
        return Optional.empty();
    }
}