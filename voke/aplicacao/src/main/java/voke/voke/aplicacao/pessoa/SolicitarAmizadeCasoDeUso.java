package voke.voke.aplicacao.pessoa;

import voke.voke.dominio.pessoa.amizade.Amizade;
import voke.voke.dominio.pessoa.amizade.AmizadeServico;
import voke.voke.dominio.pessoa.participante.ParticipanteId;

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
