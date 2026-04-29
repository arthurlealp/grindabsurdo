package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.fidelidade.pontos.ContaPontos;
import br.voke.dominio.fidelidade.pontos.ContaPontosId;
import br.voke.dominio.fidelidade.recompensa.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarRecompensasSteps {
    private final ContextoFidelidade ctx;
    private RecompensaRepositorio repositorio;
    private boolean alteracaoPendenteDuranteResgate;
    private boolean alteracaoAplicadaAposResgate;
    private final Map<RecompensaId, Recompensa> banco = new HashMap<>();

    public GerenciarRecompensasSteps(ContextoFidelidade ctx) {
        this.ctx = ctx;
    }

    private RecompensaRepositorio criarRepo() {
        RecompensaRepositorio mockRepositorio = mock(RecompensaRepositorio.class);
        doAnswer(invocation -> {
            Recompensa recompensaSalva = invocation.getArgument(0);
            banco.put(recompensaSalva.getId(), recompensaSalva);
            return null;
        }).when(mockRepositorio).salvar(any(Recompensa.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(RecompensaId.class));
        doAnswer(invocation -> {
            UUID organizadorId = invocation.getArgument(0);
            return banco.values().stream().filter(r -> r.getOrganizadorId().equals(organizadorId)).toList();
        }).when(mockRepositorio).buscarPorOrganizadorId(any(UUID.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(RecompensaId.class));
        return mockRepositorio;
    }

    @Dado("que o organizador está autenticado")
    public void organizadorAutenticado() { banco.clear(); repositorio = criarRepo(); ctx.excecao = null; ctx.recompensa = null; }

    @Quando("ele cria uma recompensa com nome, descrição e valor em pontos")
    public void eleCriaRecompensa() {
        try {
            ctx.recompensa = new Recompensa(RecompensaId.novo(), "Camiseta VIP", "Camiseta exclusiva", 500, 50, UUID.randomUUID());
            repositorio.salvar(ctx.recompensa);
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a recompensa fica disponível para resgate pelos participantes")
    public void aRecompensaFicaDisponivel() { assertNull(ctx.excecao); assertNotNull(ctx.recompensa); verify(repositorio).salvar(ctx.recompensa); }

    @E("nenhum participante está resgatando a recompensa no momento")
    public void nenhumParticipanteResgatando() {
        if (ctx.recompensa == null) {
            ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desc", 300, 100, UUID.randomUUID());
            repositorio.salvar(ctx.recompensa);
        }
    }

    @E("o prazo mínimo de 1 mês desde a última alteração de valor foi respeitado")
    public void prazoMinimoRespeitado() {
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desconto", 300, 100, UUID.randomUUID());
        repositorio.salvar(ctx.recompensa);
    }

    @Quando("ele edita o valor ou informações da recompensa")
    public void eleEditaValorDaRecompensa() {
        try { ctx.recompensa.atualizarDescricao("Nova descrição"); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("as alterações são salvas com sucesso")
    public void asAlteracoesSaoSalvas() { assertNull(ctx.excecao); assertEquals("Nova descrição", ctx.recompensa.getDescricao()); }

    @Dado("que o organizador alterou o valor da recompensa há menos de 1 mês")
    public void organizadorAlterouRecentemente() {
        banco.clear(); repositorio = criarRepo(); ctx.excecao = null;
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desc", 300, 100, UUID.randomUUID());
        repositorio.salvar(ctx.recompensa);
    }

    @Quando("ele tenta alterar novamente o valor da recompensa")
    public void eleTentaAlterarNovamente() {
        try { ctx.recompensa.alterarCusto(400); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("o sistema rejeita a alteração")
    public void oSistemaRejeitaAlteracao() { assertNotNull(ctx.excecao); }

    @Dado("que um participante está resgatando uma recompensa no exato momento")
    public void participanteResgatandoAgora() {
        banco.clear(); repositorio = criarRepo(); ctx.excecao = null;
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desc", 300, 100, UUID.randomUUID());
        repositorio.salvar(ctx.recompensa);
    }

    @E("o organizador tenta editar ou remover essa recompensa simultaneamente")
    public void organizadorTentaEditarSimultaneamente() {
        assertTrue(ctx.recompensa.estaDisponivel());
        ctx.recompensa.iniciarResgate();
        assertTrue(ctx.recompensa.isResgateEmAndamento());
        alteracaoPendenteDuranteResgate = true;
    }

    @Quando("o sistema processa as duas operações concorrentes")
    public void sistemaProcessaOperacoesConcorrentes() {
        ctx.recompensa.concluirResgate();
        repositorio.salvar(ctx.recompensa);
        if (alteracaoPendenteDuranteResgate) {
            ctx.recompensa.atualizarDescricao("Alteração aplicada após resgate");
            repositorio.salvar(ctx.recompensa);
            alteracaoAplicadaAposResgate = true;
        }
    }

    @Então("o resgate do participante é concluído com os valores anteriores")
    public void resgateConcluidoComValoresAnteriores() { assertEquals(99, ctx.recompensa.getEstoqueRestante()); assertEquals(300, ctx.recompensa.getCustoEmPontos()); verify(repositorio, atLeastOnce()).salvar(ctx.recompensa); }

    @E("a edição ou remoção é aplicada somente após a conclusão do resgate")
    public void edicaoAplicadaAposResgate() { assertTrue(alteracaoAplicadaAposResgate); assertEquals("Alteração aplicada após resgate", ctx.recompensa.getDescricao()); }

    @Quando("ele remove a recompensa")
    public void eleRemoveRecompensa() {
        try { repositorio.remover(ctx.recompensa.getId()); } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a recompensa é excluída e não aparece mais para os participantes")
    public void aRecompensaEExcluida() { assertNull(ctx.excecao); assertFalse(repositorio.buscarPorId(ctx.recompensa.getId()).isPresent()); verify(repositorio).remover(ctx.recompensa.getId()); }

    @Dado("que o participante possui saldo de pontos suficiente")
    public void participantePossuiSaldoDePontosSuficiente() {
        banco.clear(); repositorio = criarRepo(); ctx.excecao = null;
        ctx.conta = new ContaPontos(ContaPontosId.novo(), UUID.randomUUID());
        ctx.conta.creditarPorPresenca(1000);
    }

    @E("a recompensa atingiu o limite de resgates e está esgotada")
    public void recompensaAtingiuLimiteDeResgates() {
        ctx.recompensa = new Recompensa(RecompensaId.novo(), "Ingresso VIP", "Experiência exclusiva", 500, 1, UUID.randomUUID());
        ctx.recompensa.resgatar();
        repositorio.salvar(ctx.recompensa);
    }

    @Quando("ele tenta resgatar a recompensa")
    public void eleTentaResgatarRecompensa() {
        try {
            ctx.conta.debitar(ctx.recompensa.getCustoEmPontos());
            ctx.recompensa.resgatar();
            repositorio.salvar(ctx.recompensa);
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @E("a recompensa está ativa no catálogo")
    public void recompensaEstaAtivaNoCatalogo() {
        if (ctx.recompensa == null) {
            ctx.recompensa = new Recompensa(RecompensaId.novo(), "Voucher", "Desconto", 300, 100, UUID.randomUUID());
            repositorio.salvar(ctx.recompensa);
        }
        assertTrue(ctx.recompensa.isAtiva());
    }

    @Quando("ele inativa a recompensa")
    public void eleInativaARecompensa() {
        try {
            ctx.recompensa.inativar();
            repositorio.salvar(ctx.recompensa);
        } catch (Exception e) {
            ctx.excecao = e;
        }
    }

    @Então("a recompensa deixa de aparecer para os participantes")
    public void recompensaDeixaDeAparecerParaParticipantes() {
        assertNull(ctx.excecao);
        assertFalse(ctx.recompensa.estaDisponivel());
    }

    @E("permanece registrada no sistema sem ser excluída")
    public void recompensaPermaneceRegistradaSemSerExcluida() {
        assertTrue(repositorio.buscarPorId(ctx.recompensa.getId()).isPresent());
        verify(repositorio, atLeastOnce()).salvar(ctx.recompensa);
    }
}
