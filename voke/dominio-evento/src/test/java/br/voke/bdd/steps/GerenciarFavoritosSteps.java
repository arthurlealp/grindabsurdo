package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.evento.favorito.*;
import br.voke.dominio.evento.excecao.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarFavoritosSteps {
    private FavoritoRepositorio repositorio;
    private FavoritoServico servico;
    private Favorito favorito;
    private Exception excecao;
    private final Map<FavoritoId, Favorito> banco = new HashMap<>();

    private FavoritoRepositorio criarRepo() {
        return new FavoritoRepositorio() {
            @Override public void salvar(Favorito f) { banco.put(f.getId(), f); }
            @Override public Optional<Favorito> buscarPorId(FavoritoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(FavoritoId id) { banco.remove(id); }
            @Override public boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId) {
                return banco.values().stream().anyMatch(f -> f.getParticipanteId().equals(participanteId) && f.getEventoId().equals(eventoId));
            }
        };
    }

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() { banco.clear(); repositorio = criarRepo(); servico = new FavoritoServico(repositorio); excecao = null; favorito = null; }

    @E("o evento possui status {string}")
    public void oEventoPossuiStatus(String status) { /* contexto de teste */ }

    @Quando("ele adiciona o evento à lista de favoritos")
    public void eleAdicionaAosFavoritos() {
        try { favorito = servico.adicionar(UUID.randomUUID(), UUID.randomUUID(), "PUBLICADO"); } catch (Exception e) { excecao = e; }
    }

    @Então("o evento aparece na lista de favoritos do participante")
    public void oEventoApareceFavoritos() { assertNull(excecao); assertNotNull(favorito); }

    @E("o evento possui status diferente de {string} ou {string} \\(ex.: Cancelado, Encerrado)")
    public void eventoStatusInvalido(String s1, String s2) { /* contexto */ }

    @Quando("ele tenta adicionar o evento à lista de favoritos")
    public void eleTentaAdicionarFavoritos() {
        try { favorito = servico.adicionar(UUID.randomUUID(), UUID.randomUUID(), "CANCELADO"); } catch (Exception e) { excecao = e; }
    }

    @Dado("que o participante já possui o evento na sua lista de favoritos")
    public void participanteJaPossuiFavorito() {
        banco.clear(); repositorio = criarRepo(); servico = new FavoritoServico(repositorio); excecao = null;
        UUID pId = UUID.randomUUID(); UUID eId = UUID.randomUUID();
        favorito = servico.adicionar(pId, eId, "PUBLICADO");
    }

    @Quando("ele tenta adicionar o mesmo evento novamente")
    public void eleTentaAdicionarNovamente() {
        try { servico.adicionar(favorito.getParticipanteId(), favorito.getEventoId(), "PUBLICADO"); } catch (Exception e) { excecao = e; }
    }

    @Dado("que o participante possui um evento na lista de favoritos")
    public void participantePossuiFavorito() {
        banco.clear(); repositorio = criarRepo(); servico = new FavoritoServico(repositorio); excecao = null;
        favorito = servico.adicionar(UUID.randomUUID(), UUID.randomUUID(), "PUBLICADO");
    }

    @Quando("ele remove o evento da lista")
    public void eleRemoveDaLista() { try { repositorio.remover(favorito.getId()); } catch (Exception e) { excecao = e; } }

    @Então("o evento deixa de aparecer na lista de favoritos")
    public void oEventoDeixaDeAparecer() { assertNull(excecao); assertFalse(repositorio.buscarPorId(favorito.getId()).isPresent()); }
}
