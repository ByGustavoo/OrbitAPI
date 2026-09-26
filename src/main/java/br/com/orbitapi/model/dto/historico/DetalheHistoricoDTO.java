package br.com.orbitapi.model.dto.historico;

import br.com.orbitapi.model.dto.sessao.SessaoEstudoDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registro da linha do tempo com a tarefa e a sessão como estão agora")
public record DetalheHistoricoDTO(

        @Schema(description = "O registro, igual ao da linha do tempo")
        RegistroHistoricoDTO registro,

        @Schema(description = "Tarefa atual, com o prazo calculado; null quando o registro não tem tarefa")
        TarefaDTO tarefa,

        @Schema(description = "Sessão atual; só em SESSAO_ESTUDO")
        SessaoEstudoDTO sessao

) {}