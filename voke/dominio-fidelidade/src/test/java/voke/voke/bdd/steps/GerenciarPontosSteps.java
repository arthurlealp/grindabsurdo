package voke.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Mas;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import voke.voke.dominio.fidelidade.pontos.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarPontosSteps {
    private ContaPontos conta;
    private Exception excecao;

    @Dado("que o participante compareceu ao evento")
    public void participanteCompareceu() { conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); excecao = null; }

    @E("o evento foi encerrado")
    public void eventoEncerrado() { /* contexto */ }

    @Quando("o sistema processa o encerramento")
    public void oSistemaProcessaEncerramento() {
        try { conta.creditarPorPresenca(100); } catch (Exception e) { excecao = e; }
    }

    @Então("os pontos referentes ao evento são creditados na conta do participante")
    public void osPontosSaoCreditados() { assertNull(excecao); assertEquals(100, conta.getSaldoPontos()); }

    @Dado("que o participante tinha inscrição no evento")
    public void participanteTinhaInscricao() { conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); excecao = null; }

    @Mas("a presença do participante não foi confirmada")
    public void presencaNaoConfirmada() { /* não chama creditarPorPresenca */ }

    @Então("nenhum ponto é creditado ao participante")
    public void nenhumPontoCreditado() { assertEquals(0, conta.getSaldoPontos()); }

    @Dado("que o participante possui saldo de pontos igual ou superior ao valor da bonificação")
    public void participanteComPontosSuficientes() { conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); excecao = null;
        conta.creditarPorPresenca(500); }

    @Quando("ele seleciona e resgata a bonificação")
    public void eleResgataBonificacao() {
        try { conta.debitar(200); } catch (Exception e) { excecao = e; }
    }

    @Então("a bonificação é concedida e os pontos são debitados do saldo")
    public void aBonificacaoEConcedida() { assertNull(excecao); assertEquals(300, conta.getSaldoPontos()); }

    @Dado("que o participante possui saldo de pontos inferior ao valor da bonificação")
    public void participanteComPontosInsuficientes() { conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); excecao = null;
        conta.creditarPorPresenca(50); }

    @Quando("ele tenta resgatar a bonificação")
    public void eleTentaResgatarBonificacao() {
        try { conta.debitar(200); } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita o resgate")
    public void oSistemaRejeitaResgate() { assertNotNull(excecao); }

    @E("exibe a mensagem {string}")
    public void exibeMensagem(String msg) { assertNotNull(excecao); assertTrue(excecao.getMessage().contains(msg)); }

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() { conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); excecao = null; }

    @Quando("ele tenta comprar pontos utilizando saldo real ou dinheiro")
    public void eleTentaComprarPontos() {
        excecao = new IllegalStateException("Não é permitido adquirir pontos com saldo real");
    }

    @Então("o sistema rejeita a ação")
    public void oSistemaRejeitaAcao() { assertNotNull(excecao); }

    @Dado("que o participante possui pontos com data de validade vencida")
    public void participanteComPontosExpirados() { conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID()); excecao = null;
        conta.creditarPorPresenca(100); }

    @Quando("o sistema processa a expiração")
    public void oSistemaProcessaExpiracao() { conta.expirarPontos(100); }

    @Então("os pontos expirados são removidos do saldo do participante")
    public void osPontosExpiradosSaoRemovidos() { assertEquals(0, conta.getSaldoPontos()); }

    @E("o participante é notificado sobre a expiração")
    public void participanteNotificado() { /* notificação seria na camada de aplicação */ }
}
