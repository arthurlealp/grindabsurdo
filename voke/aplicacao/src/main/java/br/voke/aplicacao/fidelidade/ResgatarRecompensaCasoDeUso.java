package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.recompensa.Recompensa;
import br.voke.dominio.fidelidade.recompensa.RecompensaId;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;

import java.util.Objects;
import java.util.UUID;

public class ResgatarRecompensaCasoDeUso {

    private final ContaPontosRepositorio contaPontosRepositorio;
    private final RecompensaRepositorio recompensaRepositorio;

    public ResgatarRecompensaCasoDeUso(ContaPontosRepositorio contaPontosRepositorio,
                                       RecompensaRepositorio recompensaRepositorio) {
        Objects.requireNonNull(contaPontosRepositorio);
        Objects.requireNonNull(recompensaRepositorio);
        this.contaPontosRepositorio = contaPontosRepositorio;
        this.recompensaRepositorio = recompensaRepositorio;
    }

    public void executar(UUID participanteId, UUID recompensaId) {
        ContaPontos conta = contaPontosRepositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        Recompensa recompensa = recompensaRepositorio.buscarPorId(new RecompensaId(recompensaId))
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));
        conta.debitar(recompensa.getCustoEmPontos());
        recompensa.resgatar();
        contaPontosRepositorio.salvar(conta);
        recompensaRepositorio.salvar(recompensa);
    }
}
