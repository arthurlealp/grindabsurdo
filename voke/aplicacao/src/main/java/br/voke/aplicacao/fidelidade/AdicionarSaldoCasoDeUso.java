package br.voke.aplicacao.fidelidade;

import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AdicionarSaldoCasoDeUso {

    private final CarteiraVirtualRepositorio repositorio;

    public AdicionarSaldoCasoDeUso(CarteiraVirtualRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public void executar(UUID participanteId, BigDecimal valor) {
        CarteiraVirtual carteira = repositorio.buscarPorParticipanteId(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Carteira não encontrada"));
        carteira.adicionarSaldo(valor);
        repositorio.salvar(carteira);
    }
}
