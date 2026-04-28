package br.voke.aplicacao.pessoa;

import br.voke.dominio.pessoa.amizade.Amizade;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Objects;
import java.util.UUID;

public class SolicitarAmizadeCasoDeUso {

    private final AmizadeServico servico;

    public SolicitarAmizadeCasoDeUso(AmizadeServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Amizade executar(UUID solicitanteId, UUID receptorId) {
        return servico.solicitar(
                new ParticipanteId(solicitanteId),
                new ParticipanteId(receptorId)
        );
    }
}
