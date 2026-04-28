package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.evento.avaliacao.*;
import br.voke.dominio.evento.excecao.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarAvaliacaoSteps {

    private AvaliacaoRepositorio repositorio;
    private AvaliacaoServico servico;
    private Avaliacao avaliacao;
    private Exception excecao;
    private final Map<AvaliacaoId, Avaliacao> banco = new HashMap<>();

    private AvaliacaoRepositorio criarRepositorioEmMemoria() {
        return new AvaliacaoRepositorio() {
            @Override public void salvar(Avaliacao a) { banco.put(a.getId(), a); }
            @Override public Optional<Avaliacao> buscarPorId(AvaliacaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(AvaliacaoId id) { banco.remove(id); }
            @Override public boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId) {
                return banco.values().stream().anyMatch(a ->
                    a.getParticipanteId().equals(participanteId) && a.getEventoId().equals(eventoId));
            }
        };
    }

    @Dado("que o participante possui inscrição confirmada")
    public void participanteComInscricaoConfirmada() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        excecao = null;
        avaliacao = null;
    }

    @E("o evento foi finalizado")
    public void oEventoFoiFinalizado() { /* contexto: evento encerrado */ }

    @Quando("ele submete uma avaliação com nota e comentário")
    public void eleSubmeteAvaliacao() {
        try {
            avaliacao = servico.avaliar(UUID.randomUUID(), UUID.randomUUID(), 5, "Excelente evento!", true, true);
        } catch (Exception e) { excecao = e; }
    }

    @Então("a avaliação é registrada com sucesso")
    public void aAvaliacaoERegistrada() { assertNull(excecao); assertNotNull(avaliacao); }

    @E("o evento ainda está em andamento ou ativo")
    public void oEventoEmAndamento() { /* contexto: evento não finalizado */ }

    @Quando("ele tenta submeter uma avaliação")
    public void eleTentaSubmeterAvaliacao() {
        try {
            avaliacao = servico.avaliar(UUID.randomUUID(), UUID.randomUUID(), 4, "Bom", false, true);
        } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita a avaliação")
    public void oSistemaRejeitaAAvaliacao() { assertNotNull(excecao); }

    @Dado("que o participante não possui inscrição confirmada no evento")
    public void participanteSemInscricao() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        excecao = null;
    }

    @Dado("que o participante já submeteu uma avaliação para o evento")
    public void participanteJaSubmeteuAvaliacao() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        excecao = null;
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        avaliacao = servico.avaliar(participanteId, eventoId, 4, "Legal", true, true);
    }

    @Quando("ele tenta submeter uma nova avaliação para o mesmo evento")
    public void eleTentaSubmeterNovaAvaliacao() {
        try {
            servico.avaliar(avaliacao.getParticipanteId(), avaliacao.getEventoId(), 5, "Ótimo", true, true);
        } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita a segunda avaliação")
    public void oSistemaRejeitaSegundaAvaliacao() { assertNotNull(excecao); }

    @Dado("que o participante já submeteu uma avaliação")
    public void participanteJaSubmeteu() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        excecao = null;
        avaliacao = servico.avaliar(UUID.randomUUID(), UUID.randomUUID(), 3, "OK", true, true);
    }

    @Quando("ele edita a nota ou o comentário")
    public void eleEditaNotaOuComentario() {
        try {
            avaliacao.editar(5, "Atualizei minha opinião - foi ótimo!");
        } catch (Exception e) { excecao = e; }
    }

    @Então("a avaliação é atualizada com sucesso")
    public void aAvaliacaoEAtualizada() { assertNull(excecao); assertEquals(5, avaliacao.getNota()); }

    @Quando("ele solicita a exclusão da avaliação")
    public void eleSolicitaExclusaoDaAvaliacao() {
        try {
            repositorio.remover(avaliacao.getId());
        } catch (Exception e) { excecao = e; }
    }

    @Então("a avaliação é removida do sistema")
    public void aAvaliacaoERemovidaDoSistema() {
        assertNull(excecao);
        assertFalse(repositorio.buscarPorId(avaliacao.getId()).isPresent());
    }
}
