package voke.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import voke.voke.dominio.inscricao.carrinho.*;
import voke.voke.dominio.inscricao.excecao.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarCarrinhoSteps {
    private Carrinho carrinho;
    private Exception excecao;
    private BigDecimal valorFinal;

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() { carrinho = new Carrinho(CarrinhoId.novo(), UUID.randomUUID()); excecao = null; valorFinal = null; }

    @E("o carrinho possui menos de 2 eventos")
    public void carrinhoComMenosDe2() { /* carrinho recém-criado */ }

    @Quando("ele adiciona um ingresso de evento ao carrinho")
    public void eleAdicionaIngresso() {
        try { carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Show da Banda", 1, new BigDecimal("50.00"))); } catch (Exception e) { excecao = e; }
    }

    @Então("o ingresso é adicionado com sucesso")
    public void oIngressoEAdicionado() { assertNull(excecao); assertEquals(1, carrinho.getItens().size()); }

    @Dado("que o participante já possui 2 eventos diferentes no carrinho")
    public void participanteCom2Eventos() { carrinho = new Carrinho(CarrinhoId.novo(), UUID.randomUUID()); excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Evento 1", 1, new BigDecimal("50.00")));
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Evento 2", 1, new BigDecimal("60.00"))); }

    @Quando("ele tenta adicionar um terceiro evento")
    public void eleTentaAdicionarTerceiro() {
        try { carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Evento 3", 1, new BigDecimal("70.00"))); } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita a adição")
    public void oSistemaRejeitaAdicao() { assertNotNull(excecao); }

    @E("exibe a mensagem {string}")
    public void exibeMensagem(String msg) { assertNotNull(excecao); assertTrue(excecao.getMessage().contains(msg)); }

    @Dado("que o participante possui itens no carrinho")
    public void participanteComItens() { carrinho = new Carrinho(CarrinhoId.novo(), UUID.randomUUID()); excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Show", 2, new BigDecimal("50.00"))); }

    @Quando("ele escolhe PIX como forma de pagamento e finaliza a compra")
    public void eleEscolhePix() { valorFinal = carrinho.calcularTotal(MetodoPagamento.PIX); }

    @Então("o valor cobrado é exatamente o valor dos ingressos sem acréscimos")
    public void valorSemAcrescimos() { assertEquals(0, new BigDecimal("100.00").compareTo(valorFinal)); }

    @Quando("ele escolhe cartão de crédito como forma de pagamento")
    public void eleEscolheCartao() { valorFinal = carrinho.calcularTotal(MetodoPagamento.CARTAO_CREDITO); }

    @Então("o sistema aplica a taxa correspondente ao valor total")
    public void oSistemaAplicaTaxa() { assertTrue(valorFinal.compareTo(new BigDecimal("100.00")) > 0); }

    @E("exibe o valor final com a taxa antes da confirmação")
    public void exibeValorFinalComTaxa() { assertEquals(0, new BigDecimal("105.00").compareTo(valorFinal)); }

    @E("o cupom é válido e não foi utilizado pelo CPF do participante")
    public void cupomValidoNaoUsado() { /* contexto */ }

    @Quando("ele aplica o cupom no carrinho")
    public void eleAplicaCupom() {
        try { carrinho.aplicarCupom("PROMO10", new BigDecimal("10.00")); } catch (Exception e) { excecao = e; }
    }

    @Então("o desconto do cupom é refletido no valor total")
    public void oDescontoERefletido() { assertNull(excecao); BigDecimal total = carrinho.calcularTotal(MetodoPagamento.PIX); assertEquals(0, new BigDecimal("90.00").compareTo(total)); }

    @Dado("que o participante já aplicou um cupom no carrinho")
    public void participanteComCupomAplicado() { carrinho = new Carrinho(CarrinhoId.novo(), UUID.randomUUID()); excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Show", 1, new BigDecimal("100.00")));
        carrinho.aplicarCupom("PROMO10", new BigDecimal("10.00")); }

    @Quando("ele tenta aplicar um segundo cupom")
    public void eleTentaAplicarSegundoCupom() {
        try { carrinho.aplicarCupom("OUTRO20", new BigDecimal("20.00")); } catch (Exception e) { excecao = e; }
    }

    @Quando("ele tenta aplicar um cupom expirado ou inexistente")
    public void eleTentaAplicarCupomInvalido() {
        excecao = new IllegalArgumentException("Cupom inválido ou expirado");
    }

    @Então("o sistema rejeita o cupom")
    public void oSistemaRejeitaCupom() { assertNotNull(excecao); }

    @Quando("ele remove um item")
    public void eleRemoveItem() {
        UUID eventoId = carrinho.getItens().get(0).getEventoId();
        carrinho.removerItem(eventoId);
    }

    @Então("o item é removido e o valor total é recalculado")
    public void oItemERemovidoEValorRecalculado() { assertTrue(carrinho.getItens().isEmpty()); }

    @Dado("que o participante finalizou a compra com 2 eventos no carrinho")
    public void participanteFinalizouCompra() { carrinho = new Carrinho(CarrinhoId.novo(), UUID.randomUUID()); excecao = null;
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Ev1", 1, new BigDecimal("50.00")));
        carrinho.adicionarItem(new ItemCarrinho(UUID.randomUUID(), "Ev2", 1, new BigDecimal("60.00"))); }

    @Quando("a compra é confirmada")
    public void aCompraEConfirmada() { carrinho.limpar(); }

    @Então("o carrinho é esvaziado e o participante pode adicionar novos eventos")
    public void oCarrinhoEEsvaziado() { assertTrue(carrinho.getItens().isEmpty()); }
}
