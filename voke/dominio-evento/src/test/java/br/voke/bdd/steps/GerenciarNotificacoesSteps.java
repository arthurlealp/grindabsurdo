package br.voke.bdd.steps;

import br.voke.dominio.evento.notificacao.Notificacao;
import br.voke.dominio.evento.notificacao.NotificacaoId;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GerenciarNotificacoesSteps {
    private final ContextoEvento contexto;
    private final Map<NotificacaoId, Notificacao> banco = new HashMap<>();
    private NotificacaoRepositorio repositorio;
    private NotificacaoServico servico;
    private Notificacao notificacao;
    private UUID participanteId;

    public GerenciarNotificacoesSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private NotificacaoRepositorio criarRepo() {
        NotificacaoRepositorio mockRepositorio = mock(NotificacaoRepositorio.class);
        doAnswer(invocation -> {
            Notificacao notificacaoSalva = invocation.getArgument(0);
            banco.put(notificacaoSalva.getId(), notificacaoSalva);
            return null;
        }).when(mockRepositorio).salvar(any(Notificacao.class));
        doAnswer(invocation -> java.util.Optional.ofNullable(banco.get(invocation.getArgument(0))))
                .when(mockRepositorio).buscarPorId(any(NotificacaoId.class));
        doAnswer(invocation -> {
            UUID eventoId = invocation.getArgument(0);
            return banco.values().stream()
                    .filter(notificacao -> notificacao.getEventoId().equals(eventoId))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        }).when(mockRepositorio).buscarPorEventoId(any(UUID.class));
        doAnswer(invocation -> {
            UUID participante = invocation.getArgument(0);
            return banco.values().stream()
                    .filter(notificacao -> notificacao.foiEnviadaPara(participante))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        }).when(mockRepositorio).buscarPorParticipanteId(any(UUID.class));
        doAnswer(invocation -> {
            banco.remove(invocation.getArgument(0));
            return null;
        }).when(mockRepositorio).remover(any(NotificacaoId.class));
        return mockRepositorio;
    }

    @E("o evento está ativo e possui participantes inscritos")
    public void eventoAtivoComInscritos() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = null;
    }

    @Quando("ele cria e envia uma notificação")
    public void eleCriaEEnviaNotificacao() {
        try {
            notificacao = servico.enviar(UUID.randomUUID(), "Aviso importante!", true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("todos os inscritos recebem a notificação")
    public void todosOsInscritosRecebem() {
        assertNull(contexto.excecao);
        assertNotNull(notificacao);
        assertFalse(repositorio.buscarPorEventoId(notificacao.getEventoId()).isEmpty());
        verify(repositorio, atLeastOnce()).salvar(notificacao);
    }

    @E("o evento foi cancelado")
    public void oEventoFoiCancelado() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = null;
    }

    @Quando("ele tenta criar e enviar uma notificação")
    public void eleTentaCriarNotificacao() {
        try {
            notificacao = servico.enviar(UUID.randomUUID(), "Tentativa", false);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita o envio")
    public void oSistemaRejeitaEnvio() {
        assertNotNull(contexto.excecao);
    }

    @E("uma notificação foi enviada anteriormente")
    public void notificacaoEnviadaAnteriormente() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        notificacao = servico.enviar(UUID.randomUUID(), "Notificação original", true);
    }

    @Quando("ele edita o conteúdo da notificação")
    public void eleEditaConteudo() {
        try {
            servico.editar(notificacao.getId(), "Conteúdo atualizado");
            notificacao = repositorio.buscarPorId(notificacao.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a notificação atualizada é reenviada para os inscritos")
    public void aNotificacaoAtualizada() {
        assertNull(contexto.excecao);
        assertEquals("Conteúdo atualizado", notificacao.getConteudo());
        verify(repositorio, atLeastOnce()).salvar(notificacao);
    }

    @E("é exibida com o indicador de {string} no sistema")
    public void exibidaComIndicador(String indicador) {
        assertTrue(notificacao.isEditada());
    }

    @Quando("ele remove a notificação")
    public void eleRemoveNotificacao() {
        try {
            servico.remover(notificacao.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a notificação é removida do sistema")
    public void aNotificacaoERemovidaDoSistema() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(notificacao.getId()).isPresent());
        verify(repositorio).remover(notificacao.getId());
    }

    @Dado("que o participante tinha inscrição no evento antes do cancelamento")
    public void participanteTinhaInscricao() {
        banco.clear();
        repositorio = criarRepo();
        servico = new NotificacaoServico(repositorio);
        contexto.excecao = null;
        participanteId = UUID.randomUUID();
        notificacao = servico.enviar(UUID.randomUUID(), "Antes do cancelamento", true, java.util.Set.of(participanteId));
    }

    @E("o evento foi cancelado após o envio de notificações")
    public void eventoFoiCanceladoAposEnvio() {
        assertNotNull(notificacao);
    }

    @Quando("o ex-inscrito acessa suas notificações")
    public void exInscritoAcessaNotificacoes() {
        assertFalse(servico.listarPorParticipante(participanteId).isEmpty());
    }

    @Então("ele consegue visualizar as notificações enviadas antes do cancelamento")
    public void eleConsegueVisualizar() {
        assertNotNull(notificacao);
        assertTrue(servico.listarPorParticipante(participanteId).contains(notificacao));
    }
}
