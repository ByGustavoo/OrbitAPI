package br.com.orbitapi.exceptions.handler;

import br.com.orbitapi.exceptions.*;
import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.exceptions.dto.MethodArgumentNotValidResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Set<String> CAMPO_AUSENTE = Set.of("NotNull", "NotBlank");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest pHttpServletRequest) {

        var errors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, Function.identity(), GlobalExceptionHandler::maisRelevante, LinkedHashMap::new))
                .values()
                .stream()
                .map(fieldError -> new MethodArgumentNotValidResponseDTO(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList();

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/validation-error",
                "Revise os campos destacados e tente de novo!",
                errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/unreadable-message",
                "O corpo da requisição está malformado ou tem um valor em formato inválido!");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetros Inválidos!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/invalid-parameters",
                "O parâmetro '" + ex.getName() + "' foi informado em um formato inválido!");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro Ausente!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/missing-parameter",
                "Informe o parâmetro '" + ex.getParameterName() + "'!");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(PeriodoInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handlePeriodoInvalidoException(PeriodoInvalidoException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Período Inválido!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/periodo-invalido",
                ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SemanaInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSemanaInvalidaException(SemanaInvalidaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Semana Inválida!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/semana-invalida",
                ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest pHttpServletRequest) {

        log.warn("Argumento inválido em {}", pHttpServletRequest.getRequestURI(), ex);

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/illegal-argument",
                "A requisição contém dados inválidos!");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CategoriaIndisponivelException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoriaIndisponivelException(CategoriaIndisponivelException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/validation-error",
                "Revise os campos destacados e tente de novo!",
                List.of(new MethodArgumentNotValidResponseDTO("categoriaId", ex.getMessage())));

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AtividadeIndisponivelException.class)
    public ResponseEntity<ErrorResponseDTO> handleAtividadeIndisponivelException(AtividadeIndisponivelException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/validation-error",
                "Revise os campos destacados e tente de novo!",
                List.of(new MethodArgumentNotValidResponseDTO("atividadeId", ex.getMessage())));

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AlteracaoRecorrenciaException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlteracaoRecorrenciaException(AlteracaoRecorrenciaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/validation-error",
                "Revise os campos destacados e tente de novo!",
                List.of(new MethodArgumentNotValidResponseDTO("frequencia", ex.getMessage())));

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoriaNaoEncontradaException(CategoriaNaoEncontradaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Categoria não encontrada!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/categoria-nao-encontrada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AtividadeNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleAtividadeNaoEncontradaException(AtividadeNaoEncontradaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Atividade não encontrada!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/atividade-nao-encontrada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleTarefaNaoEncontradaException(TarefaNaoEncontradaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Tarefa não encontrada!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/tarefa-nao-encontrada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSessaoNaoEncontradaException(SessaoNaoEncontradaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Sessão não encontrada!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/sessao-nao-encontrada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RegistroHistoricoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleRegistroHistoricoNaoEncontradoException(RegistroHistoricoNaoEncontradoException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Registro do histórico não encontrado!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/registro-historico-nao-encontrado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Registro não encontrado!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/entity-not-found",
                "Não foi possível localizar um registro com o ID informado!");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/resource-not-found",
                "O endpoint informado não existe!");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Método Não Permitido!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/method-not-allowed",
                "O método '" + ex.getMethod() + "' não é aceito neste endpoint!");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(CategoriaDuplicadaException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoriaDuplicadaException(CategoriaDuplicadaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "Categoria Duplicada!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/categoria-duplicada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AtividadeDuplicadaException.class)
    public ResponseEntity<ErrorResponseDTO> handleAtividadeDuplicadaException(AtividadeDuplicadaException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "Atividade Duplicada!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/atividade-duplicada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AtividadeComSessoesException.class)
    public ResponseEntity<ErrorResponseDTO> handleAtividadeComSessoesException(AtividadeComSessoesException ex, HttpServletRequest pHttpServletRequest) {

        var response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "Atividade com Sessões!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/atividade-com-sessoes",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest pHttpServletRequest) {

        log.warn("Conflito de integridade em {}", pHttpServletRequest.getRequestURI(), ex);

        var response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/data-integrity-violation",
                "A operação conflita com dados já gravados!");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerErrorException(Exception ex, HttpServletRequest pHttpServletRequest) {

        log.error("Erro inesperado em {}", pHttpServletRequest.getRequestURI(), ex);

        var response = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno no Servidor!",
                pHttpServletRequest.getRequestURI(),
                "/OrbitAPI/problems/internal-server-error",
                "Ocorreu um erro inesperado no servidor!");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private static FieldError maisRelevante(FieldError atual, FieldError outro) {
        return CAMPO_AUSENTE.contains(outro.getCode()) ? outro : atual;
    }
}