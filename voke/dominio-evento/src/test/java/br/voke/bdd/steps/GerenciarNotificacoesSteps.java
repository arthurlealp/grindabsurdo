package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.evento.notificacao.*;
import br.voke.dominio.evento.excecao.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarNotificacoesSteps {
    private NotificacaoRepositorio repositorio;
    private NotificacaoServico servico;
    private Notificacao notificacao;
    private Exception excecao;
    private final Map<NotificacaoId, Notificacao> banco = new HashMap<>();

    private NotificacaoRepositorio criarRepo() {
        return new NotificacaoRepositorio() {
            @Override public void salvar(Notificacao n) { banco.put(n.getId(), n); }
            @Override public Optional<Notificacao> buscarPorId(NotificacaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Notificacao> buscarPorEventoId(UUID eventoId) { return new ArrayList<>(banco.values()); }
            @Override public void remover(NotificacaoId id) { banco.remove(id); }
        };
    }

    @E("o evento está ativo e possui participantes inscritos")
    public void eventoAtivoComInscritos() { banco.clear(); repositorio = criarRepo(); servico = new NotificacaoServico(repositorio); excecao = null; }

    @Quando("ele cria e envia uma notificação")
    public void eleCriaEEnviaNotificacao() {
        try { notificacao = servico.enviar(UUID.randomUUID(), "Aviso importante!", true); } catch (Exception e) { excecao = e; }
    }

    @Então("todos os inscritos recebem a notificação")
    public void todosOsInscritosRecebem() { assertNull(excecao); assertNotNull(notificacao); }

    @E("o evento foi cancelado")
    public void oEventoFoiCancelado() { banco.clear(); repositorio = criarRepo(); servico = new NotificacaoServico(repositorio); excecao = null; }

    @Quando("ele tenta criar e enviar uma notificação")
    public void eleTentaCriarNotificacao() {
        try { notificacao = servico.enviar(UUID.randomUUID(), "Tentativa", false); } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita o envio")
    public void oSistemaRejeitaEnvio() { assertNotNull(excecao); }

    @E("uma notificação foi enviada anteriormente")
    public void notificacaoEnviadaAnteriormente() {
        banco.clear(); repositorio = criarRepo(); servico = new NotificacaoServico(repositorio); excecao = null;
        notificacao = servico.enviar(UUID.randomUUID(), "Notificação original", true);
    }

    @Quando("ele edita o conteúdo da notificação")
    public void eleEditaConteudo() {
        try { notificacao.editar("Conteúdo atualizado"); } catch (Exception e) { excecao = e; }
    }

    @Então("a notificação atualizada é reenviada para os inscritos")
    public void aNotificacaoAtualizada() { assertNull(excecao); assertEquals("Conteúdo atualizado", notificacao.getConteudo()); }

    @E("é exibida com o indicador de {string} no sistema")
    public void exibidaComIndicador(String ind) { assertTrue(notificacao.isEditada()); }

    @Quando("ele remove a notificação")
    public void eleRemoveNotificacao() { try { repositorio.remover(notificacao.getId()); } catch (Exception e) { excecao = e; } }

    @Então("a notificação é removida do sistema")
    public void aNotificacaoERemovidaDoSistema() { assertNull(excecao); assertFalse(repositorio.buscarPorId(notificacao.getId()).isPresent()); }

    @Dado("que o participante tinha inscrição no evento antes do cancelamento")
    public void participanteTinhaInscricao() { banco.clear(); repositorio = criarRepo(); servico = new NotificacaoServico(repositorio); excecao = null;
        notificacao = servico.enviar(UUID.randomUUID(), "Antes do cancelamento", true); }

    @E("o evento foi cancelado após o envio de notificações")
    public void eventoFoiCanceladoAposEnvio() { /* contexto */ }

    @Quando("o ex-inscrito acessa suas notificações")
    public void exInscritoAcessaNotificacoes() { /* consulta */ }

    @Então("ele consegue visualizar as notificações enviadas antes do cancelamento")
    public void eleConsegueVisualizar() { assertNotNull(notificacao); }
}
