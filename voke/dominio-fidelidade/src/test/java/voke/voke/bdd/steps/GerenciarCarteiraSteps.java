package voke.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import voke.voke.dominio.fidelidade.carteira.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarCarteiraSteps {
    private CarteiraVirtual carteira;
    private Exception excecao;

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() { carteira = new CarteiraVirtual(CarteiraVirtualId.novo(), UUID.randomUUID()); excecao = null; }

    @E("o total inserido no dia não atingiu o limite diário")
    public void totalNaoAtingiuLimiteDiario() { /* carteira nova */ }

    @Quando("ele adiciona um valor dentro do limite permitido")
    public void eleAdicionaValorPermitido() {
        try { carteira.adicionarSaldo(new BigDecimal("100.00")); } catch (Exception e) { excecao = e; }
    }

    @Então("o saldo é creditado na carteira com sucesso")
    public void oSaldoECreditado() { assertNull(excecao); assertEquals(0, new BigDecimal("100.00").compareTo(carteira.getSaldo())); }

    @E("o total inserido no dia já atingiu o limite diário")
    public void totalAtingiuLimiteDiario() {
        carteira.adicionarSaldo(new BigDecimal("500.00")); // Atinge ou excede o limite
    }

    @Quando("ele tenta adicionar mais saldo")
    public void eleTentaAdicionarMaisSaldo() {
        try { carteira.adicionarSaldo(new BigDecimal("600.00")); } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita a operação")
    public void oSistemaRejeitaOperacao() { assertNotNull(excecao); }

    @E("exibe a mensagem {string}")
    public void exibeMensagem(String msg) { assertNotNull(excecao); assertTrue(excecao.getMessage().contains(msg)); }

    @E("o saldo disponível na carteira é suficiente")
    public void saldoSuficiente() { carteira.adicionarSaldo(new BigDecimal("200.00")); }

    @E("o valor a remover está dentro do limite de remoção")
    public void valorDentroDoLimiteDeRemocao() { /* contexto */ }

    @Quando("ele solicita a remoção de saldo")
    public void eleSolicitaRemocao() {
        try { carteira.removerSaldo(new BigDecimal("50.00")); } catch (Exception e) { excecao = e; }
    }

    @Então("o valor é debitado da carteira com sucesso")
    public void oValorEDebitado() { assertNull(excecao); assertTrue(carteira.getSaldo().compareTo(BigDecimal.ZERO) > 0); }

    @Quando("ele tenta remover um valor acima do limite de remoção permitido")
    public void eleTentaRemoverAcimaDoLimite() {
        try { carteira.removerSaldo(new BigDecimal("99999.00")); } catch (Exception e) { excecao = e; }
    }

    @E("o saldo disponível na carteira é insuficiente para o valor solicitado")
    public void saldoInsuficiente() { /* carteira recém-criada tem saldo 0 */ }

    @Quando("ele tenta remover saldo")
    public void eleTentaRemoverSaldo() {
        try { carteira.removerSaldo(new BigDecimal("50.00")); } catch (Exception e) { excecao = e; }
    }
}
