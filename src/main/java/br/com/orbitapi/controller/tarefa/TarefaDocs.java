package br.com.orbitapi.controller.tarefa;

import br.com.orbitapi.enums.EscopoAlteracao;
import br.com.orbitapi.enums.OrdenacaoTarefas;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.tarefa.DiaCalendarioDTO;
import br.com.orbitapi.model.dto.tarefa.ReagendamentoDTO;
import br.com.orbitapi.model.dto.tarefa.SituacaoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaEnvioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Tarefa", description = "Endpoints relacionados às tarefas")
public interface TarefaDocs {

    @GetMapping
    @Operation(
            summary = "Lista as tarefas com filtros, ordenação e paginação",
            description = """
                    Os filtros se combinam com E. O prazo é calculado no banco, no fuso do cabeçalho \
                    X-Fuso-Horario, antes de filtrar: numa série, só a ocorrência atrasada mais \
                    recente é ATRASADA, e as anteriores em aberto são NAO_REALIZADA.

                    DATA ordena por data crescente com as sem data no fim, no mesmo dia as sem \
                    horário primeiro, depois o horário de início e a prioridade decrescente. \
                    PRIORIDADE ordena por prioridade decrescente e depois como DATA. ATUALIZACAO \
                    ordena pela última alteração, da mais recente para a mais antiga.

                    Página negativa vira 0 e tamanho fora de 1 a 100 é ajustado para o limite mais \
                    próximo. Antes de ler, estende as séries recorrentes que precisam.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de tarefas retornada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro em formato inválido: data, enum ou número!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PaginaDTO<TarefaDTO>> listarTarefas(
            @Parameter(description = "Só tarefas deste dia", example = "2026-09-25")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,

            @Parameter(description = "Só tarefas a partir deste dia; exclui as sem data", example = "2026-09-26")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Só tarefas até este dia; exclui as sem data", example = "2026-10-02")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,

            @Parameter(description = "true para só tarefas sem data", example = "false")
            @RequestParam(defaultValue = "false") boolean semData,

            @Parameter(description = "Situações aceitas; repita o parâmetro para mais de uma", example = "PENDENTE")
            @RequestParam(required = false) List<Situacao> situacao,

            @Parameter(description = "Prioridades aceitas; repita o parâmetro para mais de uma", example = "URGENTE")
            @RequestParam(required = false) List<Prioridade> prioridade,

            @Parameter(description = "Prazo calculado", example = "ATRASADA")
            @RequestParam(required = false) Prazo prazo,

            @Parameter(description = "Id da categoria", example = "3")
            @RequestParam(required = false) Long categoriaId,

            @Parameter(description = "Texto contido no título ou na descrição, sem diferenciar maiúsculas", example = "carro")
            @RequestParam(required = false) String busca,

            @Parameter(description = "Ordenação", example = "DATA")
            @RequestParam(defaultValue = "DATA") OrdenacaoTarefas ordenacao,

            @Parameter(description = "Página, a partir de 0", example = "0")
            @RequestParam(defaultValue = "0") int pagina,

            @Parameter(description = "Itens por página, de 1 a 100", example = "20")
            @RequestParam(defaultValue = "20") int tamanho);

    @GetMapping("/resumo-calendario")
    @Operation(
            summary = "Resume a carga de tarefas por dia",
            description = """
                    Devolve, em ordem de data, só os dias do período com pelo menos uma tarefa não \
                    cancelada: a quantidade, a maior prioridade (inclusive das concluídas), quantas \
                    estão atrasadas e quantas estão concluídas. O prazo segue a mesma regra da \
                    lista, no fuso do cabeçalho X-Fuso-Horario.

                    Com dataInicial depois de dataFinal, a lista vem vazia. Antes de ler, estende \
                    as séries recorrentes que precisam.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumo retornado com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "dataInicial ou dataFinal ausente ou em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<DiaCalendarioDTO>> buscarResumoCalendario(
            @Parameter(description = "Primeiro dia", example = "2026-08-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Último dia", example = "2026-10-10")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal);

    @GetMapping("/{id}")
    @Operation(
            summary = "Busca uma tarefa",
            description = """
                    Devolve a tarefa no estado atual, com o prazo calculado no fuso do cabeçalho \
                    X-Fuso-Horario. Numa série, uma ocorrência atrasada que tenha outra atrasada \
                    mais recente na mesma série sai como NAO_REALIZADA.

                    Antes de ler, estende as séries recorrentes cuja janela gerada acaba em menos \
                    de 3 meses, de novo até hoje + 12 meses.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefa retornada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TarefaDTO> buscarTarefa(
            @Parameter(description = "Id da tarefa", example = "42")
            @PathVariable Long id);

    @PostMapping
    @Operation(
            summary = "Cria uma tarefa, simples ou recorrente",
            description = """
                    Normaliza o corpo antes de validar: sem data, a tarefa perde horários, lembrete e \
                    recorrência; dia inteiro perde os horários; com data e sem horário vira dia \
                    inteiro; sem horário de início o lembrete é descartado.

                    Registra o evento TAREFA_CRIADA e, se a tarefa já nasce concluída, preenche a \
                    data de conclusão e registra TAREFA_CONCLUIDA. Com recorrência, cria a série e \
                    gera as ocorrências de data + 1 até o menor entre o término e hoje + 12 meses, \
                    todas pendentes. "Hoje" e o prazo seguem o fuso do cabeçalho X-Fuso-Horario, ou \
                    America/Sao_Paulo quando ele falta ou é inválido.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa criada com sucesso; se recorrente, a primeira ocorrência!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Campo inválido, categoria inexistente ou atividade indisponível!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TarefaDTO> salvarTarefa(@RequestBody @Valid TarefaEnvioDTO tarefaEnvioDTO);

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualiza uma tarefa",
            description = """
                    Substitui os campos da tarefa, com a mesma normalização e as mesmas validações da \
                    criação. Numa tarefa avulsa, o escopo é ignorado, e uma recorrência no corpo a \
                    torna a primeira ocorrência de uma série nova.

                    Numa ocorrência, SOMENTE_ESTA altera só ela e recusa mudar a regra de repetição. \
                    ESTA_E_PROXIMAS sem mudar a regra nem a data leva título, descrição, horários, \
                    prioridade, categoria, atividade e lembrete para as ocorrências seguintes; a \
                    situação muda só nesta. Mudando a regra ou a data, encerra a série na véspera, \
                    exclui as seguintes em aberto, preserva as concluídas e canceladas e, se houver \
                    recorrência, começa uma série nova sem ocupar as datas preservadas.

                    Registra PRIORIDADE_ALTERADA, DATA_ALTERADA e os eventos de situação só da \
                    tarefa editada.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefa atualizada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Campo inválido, categoria ou atividade indisponível, ou regra de repetição alterada com SOMENTE_ESTA!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TarefaDTO> atualizarTarefa(
            @Parameter(description = "Id da tarefa", example = "42")
            @PathVariable Long id,

            @Parameter(description = "Alcance da alteração numa série", example = "SOMENTE_ESTA")
            @RequestParam(defaultValue = "SOMENTE_ESTA") EscopoAlteracao escopo,

            @RequestBody @Valid TarefaEnvioDTO tarefaEnvioDTO);

    @PatchMapping("/{id}/situacao")
    @Operation(
            summary = "Altera a situação de uma tarefa",
            description = """
                    Conclui, reabre, inicia, cancela ou desfaz uma conclusão; qualquer transição é \
                    permitida. CONCLUIDA preenche a data de conclusão com o instante atual, e as \
                    outras situações a limpam.

                    Registra TAREFA_CONCLUIDA ao concluir, TAREFA_CANCELADA ao cancelar e \
                    TAREFA_REABERTA, com a situação antiga e a nova, ao voltar de CONCLUIDA ou \
                    CANCELADA para PENDENTE ou EM_ANDAMENTO. Pedir a situação atual responde 200 sem \
                    mudar nada, nem a data de atualização.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Situação alterada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Situação ausente ou fora da lista!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<TarefaDTO> alterarSituacaoTarefa(
            @Parameter(description = "Id da tarefa", example = "42")
            @PathVariable Long id,

            @RequestBody @Valid SituacaoTarefaDTO situacaoTarefaDTO);

    @PostMapping("/reagendamentos")
    @Operation(
            summary = "Reagenda várias tarefas de uma vez",
            description = """
                    Troca só a data das tarefas informadas, numa única transação: se algum id não \
                    existe, nenhuma tarefa muda. Usado pelo "Mover todas para hoje" e pelo \
                    desfazer dele, que reenvia as datas antigas.

                    Cada tarefa que muda de data registra DATA_ALTERADA com a data antiga e a nova \
                    e atualiza a data de alteração; as que já estão na data pedida ficam como \
                    estão. A resposta traz as tarefas na ordem em que foram enviadas, sem \
                    repetições, com o prazo recalculado.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefas reagendadas com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Lista ausente ou vazia, item sem id ou sem data, ou data inválida!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Uma das tarefas não existe mais!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<TarefaDTO>> reagendarTarefas(@RequestBody @Valid ReagendamentoDTO reagendamentoDTO);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Exclui uma tarefa",
            description = """
                    Remove a tarefa de forma definitiva, com todos os seus eventos, e responde sem \
                    corpo. As sessões de estudo ligadas a ela continuam, sem a tarefa.

                    Com escopo ESTA_E_PROXIMAS numa ocorrência recorrente com data, exclui também \
                    as ocorrências seguintes pendentes ou em andamento e encerra a série na véspera; \
                    as seguintes concluídas ou canceladas continuam. Se não sobra nenhuma ocorrência \
                    anterior, a série deixa de existir e as que sobraram viram tarefas avulsas.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Tarefa excluída com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id ou escopo em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> deletarTarefa(
            @Parameter(description = "Id da tarefa", example = "42")
            @PathVariable Long id,

            @Parameter(description = "Alcance da exclusão numa série", example = "SOMENTE_ESTA")
            @RequestParam(defaultValue = "SOMENTE_ESTA") EscopoAlteracao escopo);
}