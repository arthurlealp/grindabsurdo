package voke.voke.aplicacao.fidelidade;

import voke.voke.dominio.fidelidade.pontos.ContaPontos;
import voke.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;

import java.util.Objects;
import java.util.UUID;

public class CreditarPontosCasoDeUso {

    private final ContaPontosRepositorio repositorio;

    public CreditarPontosCasoDeUso(ContaPontosRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public void executar(UUID participanteId, int pontos, boolean eventoEncerrado, boolean checkInRealizado) {
        if (!eventoEncerrado || !checkInRealizado) {
            throw new IllegalStateException("Pontos só podem ser creditados após check-in em evento encerrado");
        }
        ContaPontos conta = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de pontos não encontrada"));
        conta.creditarPorPresenca(pontos);
        repositorio.salvar(conta);
    }
}
