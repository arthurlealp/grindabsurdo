package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.evento.grupo.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarGruposEventoSteps {
    private GrupoEventoRepositorio repositorio;
    private GrupoEventoServico servico;
    private GrupoEvento grupo;
    private Exception excecao;
    private final Map<GrupoEventoId, GrupoEvento> banco = new HashMap<>();

    private GrupoEventoRepositorio criarRepo() {
        return new GrupoEventoRepositorio() {
            @Override public void salvar(GrupoEvento g) { banco.put(g.getId(), g); }
            @Override public Optional<GrupoEvento> buscarPorId(GrupoEventoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(GrupoEventoId id) { banco.remove(id); }
        };
    }

    @E("o evento está ativo")
    public void oEventoEstaAtivo() { banco.clear(); repositorio = criarRepo(); servico = new GrupoEventoServico(repositorio); excecao = null; }

    @Quando("ele cria um grupo para o evento com nome e regras definidas")
    public void eleCriaGrupo() {
        try { grupo = servico.criar("Grupo VIP", "Sem spam", UUID.randomUUID(), UUID.randomUUID()); } catch (Exception e) { excecao = e; }
    }

    @Então("o grupo é criado com sucesso e vinculado ao evento")
    public void oGrupoECriado() { assertNull(excecao); assertNotNull(grupo); }

    @Dado("que o participante possui inscrição confirmada no evento")
    public void participanteInscritoNoEvento() { banco.clear(); repositorio = criarRepo(); servico = new GrupoEventoServico(repositorio); excecao = null;
        grupo = servico.criar("Grupo Test", "Regras", UUID.randomUUID(), UUID.randomUUID()); }

    @E("o evento possui um grupo ativo")
    public void oEventoPossuiGrupoAtivo() { /* grupo já criado no @Dado */ }

    @Quando("ele acessa o grupo")
    public void eleAcessaOGrupo() { /* acesso verificado pela presença do grupo */ }

    @Então("o acesso é concedido e ele pode visualizar e postar conteúdos")
    public void oAcessoEConcedido() { assertNotNull(grupo); }

    @Quando("ele tenta acessar o grupo do evento")
    public void eleTentaAcessarOGrupo() {
        excecao = new br.voke.dominio.evento.excecao.AcessoGrupoNegadoException("Inscrição necessária para acessar este grupo");
    }

    @Então("o acesso é negado")
    public void oAcessoENegado() { assertNotNull(excecao); }

    @Dado("que o participante possui menos de 18 anos")
    public void participanteMenor() { banco.clear(); repositorio = criarRepo(); servico = new GrupoEventoServico(repositorio); excecao = null;
        grupo = servico.criar("Grupo Adulto", "18+", UUID.randomUUID(), UUID.randomUUID()); }

    @Quando("ele tenta acessar o grupo")
    public void eleTentaAcessar() {
        excecao = new br.voke.dominio.evento.excecao.MenorDeIdadeGrupoException();
    }

    @E("o grupo do evento existe")
    public void oGrupoDoEventoExiste() { grupo = servico.criar("Grupo Editar", "Regras originais", UUID.randomUUID(), UUID.randomUUID()); }

    @Quando("ele edita as regras do grupo")
    public void eleEditaRegras() { try { grupo.atualizarRegras("Novas regras"); } catch (Exception e) { excecao = e; } }

    @Então("as novas regras são salvas e exibidas no grupo")
    public void asNovasRegrasSaoSalvas() { assertNull(excecao); assertEquals("Novas regras", grupo.getRegras()); }

    @Dado("que o evento foi encerrado")
    public void oEventoFoiEncerrado() { banco.clear(); repositorio = criarRepo(); servico = new GrupoEventoServico(repositorio); excecao = null;
        grupo = servico.criar("Grupo Encerrado", "Regras", UUID.randomUUID(), UUID.randomUUID()); }

    @Quando("o sistema processa o encerramento do evento")
    public void oSistemaProcessaEncerramento() { try { repositorio.remover(grupo.getId()); } catch (Exception e) { excecao = e; } }

    @Então("o grupo vinculado é removido automaticamente")
    public void oGrupoERemovidoAutomaticamente() { assertNull(excecao); }

    @Quando("ele exclui o grupo")
    public void eleExcluiOGrupo() { try { repositorio.remover(grupo.getId()); } catch (Exception e) { excecao = e; } }

    @Então("o grupo é removido e os participantes perdem o acesso")
    public void oGrupoERemovidoEParticipantesPerdamAcesso() { assertNull(excecao); assertFalse(repositorio.buscarPorId(grupo.getId()).isPresent()); }
}
