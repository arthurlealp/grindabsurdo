package br.voke.aplicacao.evento;

import br.voke.dominio.evento.evento.Evento;
import br.voke.dominio.evento.evento.EventoId;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.excecao.ColisaoDeEspacoException;
import br.voke.dominio.evento.excecao.NomeDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CriarEventoCasoDeUsoTest {

    private EventoRepositorio repositorio;
    private CriarEventoCasoDeUso casoDeUso;

    @BeforeEach
    void setUp() {
        repositorio = mock(EventoRepositorio.class);
        casoDeUso = new CriarEventoCasoDeUso(new EventoServico(repositorio));
    }

    @Test
    void criaEventoComLoteInicialPassandoPeloServicoDeDominio() {
        Evento evento = casoDeUso.executar("Recife Tech", "Conferencia", "Sala 1",
                amanha(9), amanha(12), 100, UUID.randomUUID(), BigDecimal.valueOf(80), 50, 16);

        assertEquals("Recife Tech", evento.getNome());
        assertEquals(50, evento.getLoteAtual().getQuantidadeTotal());

        ArgumentCaptor<Evento> eventoSalvo = ArgumentCaptor.forClass(Evento.class);
        verify(repositorio).existePorNome("Recife Tech");
        verify(repositorio).buscarPorLocalEPeriodo(eq("Sala 1"), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(repositorio).salvar(eventoSalvo.capture());
        assertEquals("Recife Tech", eventoSalvo.getValue().getNome());
    }

    @Test
    void rejeitaEventoComNomeDuplicado() {
        when(repositorio.existePorNome("Recife Tech")).thenReturn(true);

        assertThrows(NomeDuplicadoException.class, () -> casoDeUso.executar("Recife Tech", "Conferencia", "Sala 1",
                amanha(9), amanha(12), 100, UUID.randomUUID(), BigDecimal.valueOf(80), 50, 16));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void rejeitaEventoComColisaoDeEspaco() {
        when(repositorio.buscarPorLocalEPeriodo(eq("Sala 1"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Evento(EventoId.novo(), "Outro", "Evento", "Sala 1",
                        amanha(10), amanha(11), 100, UUID.randomUUID(),
                        new br.voke.dominio.evento.evento.Lote(1, BigDecimal.TEN, 10), 16)));

        assertThrows(ColisaoDeEspacoException.class, () -> casoDeUso.executar("Recife Tech", "Conferencia", "Sala 1",
                amanha(9), amanha(12), 100, UUID.randomUUID(), BigDecimal.valueOf(80), 50, 16));

        verify(repositorio, never()).salvar(any());
    }

    private LocalDateTime amanha(int hora) {
        return LocalDateTime.now().plusDays(1).withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }
}
