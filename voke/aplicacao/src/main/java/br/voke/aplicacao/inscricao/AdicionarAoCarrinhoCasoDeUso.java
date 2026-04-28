package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoId;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.ItemCarrinho;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AdicionarAoCarrinhoCasoDeUso {

    private final CarrinhoRepositorio repositorio;

    public AdicionarAoCarrinhoCasoDeUso(CarrinhoRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public Carrinho executar(UUID participanteId, UUID eventoId, String nomeEvento,
                             int quantidade, BigDecimal precoUnitario) {
        Carrinho carrinho = repositorio.buscarPorParticipanteId(participanteId)
                .orElseGet(() -> new Carrinho(CarrinhoId.novo(), participanteId));
        ItemCarrinho item = new ItemCarrinho(eventoId, nomeEvento, quantidade, precoUnitario);
        carrinho.adicionarItem(item);
        repositorio.salvar(carrinho);
        return carrinho;
    }
}
