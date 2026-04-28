package br.voke.aplicacao.inscricao;

import br.voke.dominio.inscricao.carrinho.Carrinho;
import br.voke.dominio.inscricao.carrinho.CarrinhoId;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.excecao.LimiteEventosCarrinhoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdicionarAoCarrinhoCasoDeUsoTest {

    private InMemoryCarrinhoRepositorio repositorio;
    private AdicionarAoCarrinhoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = new InMemoryCarrinhoRepositorio();
        casoDeUso = new AdicionarAoCarrinhoCasoDeUso(new CarrinhoServico(repositorio));
    }

    @Test
    void criaCarrinhoQuandoParticipanteAindaNaoPossuiCarrinho() {
        UUID participanteId = UUID.randomUUID();

        Carrinho carrinho = casoDeUso.executar(participanteId, UUID.randomUUID(), "Recife Jazz", 1, BigDecimal.TEN);

        assertEquals(participanteId, carrinho.getParticipanteId());
        assertEquals(1, carrinho.getItens().size());
        assertEquals(1, repositorio.carrinhos.size());
    }

    @Test
    void reaproveitaCarrinhoExistenteEAplicaLimiteDeEventosDoDominio() {
        UUID participanteId = UUID.randomUUID();
        casoDeUso.executar(participanteId, UUID.randomUUID(), "Evento 1", 1, BigDecimal.TEN);
        casoDeUso.executar(participanteId, UUID.randomUUID(), "Evento 2", 1, BigDecimal.TEN);

        assertThrows(LimiteEventosCarrinhoException.class,
                () -> casoDeUso.executar(participanteId, UUID.randomUUID(), "Evento 3", 1, BigDecimal.TEN));
    }

    private static final class InMemoryCarrinhoRepositorio implements CarrinhoRepositorio {
        private final Map<CarrinhoId, Carrinho> carrinhos = new HashMap<>();

        @Override
        public void salvar(Carrinho carrinho) {
            carrinhos.put(carrinho.getId(), carrinho);
        }

        @Override
        public Optional<Carrinho> buscarPorId(CarrinhoId id) {
            return Optional.ofNullable(carrinhos.get(id));
        }

        @Override
        public Optional<Carrinho> buscarPorParticipanteId(UUID participanteId) {
            return carrinhos.values().stream()
                    .filter(carrinho -> carrinho.getParticipanteId().equals(participanteId))
                    .findFirst();
        }

        @Override
        public void remover(CarrinhoId id) {
            carrinhos.remove(id);
        }
    }
}
