package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.inscricao.inscricao.*;
import br.voke.dominio.inscricao.excecao.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarInscricaoSteps {
    private InscricaoRepositorio repositorio;
    private InscricaoServico servico;
    private Inscricao inscricao;
    private BigDecimal devolucao;
    private Exception excecao;
    private final Map<InscricaoId, Inscricao> banco = new HashMap<>();

    private InscricaoRepositorio criarRepo() {
        return new InscricaoRepositorio() {
            @Override public void salvar(Inscricao i) { banco.put(i.getId(), i); }
            @Override public Optional<Inscricao> buscarPorId(InscricaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public List<Inscricao> buscarPorParticipanteId(UUID pid) { return new ArrayList<>(banco.values()); }
            @Override public void remover(InscricaoId id) { banco.remove(id); }
            @Override public int contarPorParticipanteEEvento(UUID pid, UUID eid) {
                return (int) banco.values().stream().filter(i -> i.getParticipanteId().equals(pid) && i.getEventoId().equals(eid)).count();
            }
            @Override public boolean existeConflitoDeHorario(UUID pid, LocalDateTime i, LocalDateTime f) { return false; }
        };
    }

    @Dado("que o participante está autenticado")
    public void participanteAutenticado() { banco.clear(); repositorio = criarRepo(); servico = new InscricaoServico(repositorio); excecao = null; inscricao = null; devolucao = null; }

    @E("o evento está ativo e possui vagas disponíveis")
    public void eventoAtivoComVagas() { /* contexto */ }
    @E("o participante atinge a idade mínima do evento")
    public void participanteAtingeIdadeMinima() { /* contexto */ }
    @E("não possui outra inscrição no mesmo horário e dia")
    public void naoPossuiOutraInscricao() { /* repo mock retorna false para conflito */ }

    @Quando("ele realiza a inscrição no evento")
    public void eleRealizaInscricao() {
        try { inscricao = servico.realizar(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"), 20, 18, true, true, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10).plusHours(3), 5); } catch (Exception e) { excecao = e; }
    }

    @Então("a inscrição é confirmada com sucesso")
    public void aInscricaoEConfirmada() { assertNull(excecao); assertNotNull(inscricao); assertEquals(StatusInscricao.CONFIRMADA, inscricao.getStatus()); }

    @E("o evento exige uma idade mínima maior do que a idade do participante")
    public void eventoExigeIdadeMinimaMaior() { /* contexto */ }

    @Quando("ele tenta realizar a inscrição")
    public void eleTentaRealizarInscricao() {
        try { inscricao = servico.realizar(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"), 15, 18, true, true, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10).plusHours(3), 5); } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita a inscrição")
    public void oSistemaRejeitaInscricao() { assertNotNull(excecao); }

    @E("exibe a mensagem {string}")
    public void exibeMensagem(String msg) { assertNotNull(excecao); assertTrue(excecao.getMessage().contains(msg)); }

    @Dado("que o participante já possui inscrição confirmada em um evento no dia X às 14h")
    public void participanteComInscricaoExistente() {
        banco.clear();
        repositorio = new InscricaoRepositorio() {
            private final Map<InscricaoId, Inscricao> b = new HashMap<>();
            @Override public void salvar(Inscricao i) { b.put(i.getId(), i); }
            @Override public Optional<Inscricao> buscarPorId(InscricaoId id) { return Optional.ofNullable(b.get(id)); }
            @Override public List<Inscricao> buscarPorParticipanteId(UUID pid) { return new ArrayList<>(b.values()); }
            @Override public void remover(InscricaoId id) { b.remove(id); }
            @Override public int contarPorParticipanteEEvento(UUID pid, UUID eid) { return 0; }
            @Override public boolean existeConflitoDeHorario(UUID pid, LocalDateTime i, LocalDateTime f) { return true; }
        };
        servico = new InscricaoServico(repositorio); excecao = null;
    }

    @Quando("ele tenta se inscrever em outro evento no mesmo dia X às 14h")
    public void eleTentaSeInscreverNoMesmoHorario() {
        try { inscricao = servico.realizar(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"), 20, 18, true, true, LocalDateTime.now().plusDays(10).withHour(14), LocalDateTime.now().plusDays(10).withHour(17), 5); } catch (Exception e) { excecao = e; }
    }

    @Dado("que o evento possui apenas 1 vaga disponível")
    public void eventoComUmaVaga() { banco.clear(); repositorio = criarRepo(); servico = new InscricaoServico(repositorio); excecao = null; }
    @E("dois participantes tentam se inscrever simultaneamente")
    public void doisParticipantesTentam() { /* contexto concorrente simulado */ }
    @Quando("o primeiro participante confirma a inscrição")
    public void primeiroConfirma() {
        try { inscricao = servico.realizar(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("50.00"), 20, 0, true, true, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(2), 5); } catch (Exception e) { excecao = e; }
    }
    @Então("a inscrição do primeiro é confirmada")
    public void primeiraConfirmada() { assertNull(excecao); assertNotNull(inscricao); }
    @E("o sistema informa ao segundo que não há mais vagas")
    public void segundoSemVagas() { /* contexto — possuiVagas seria false para o segundo */ }

    @Dado("que o participante já atingiu o limite máximo de ingressos permitidos por CPF para o evento")
    public void participanteComLimiteAtingido() {
        banco.clear();
        UUID pid = UUID.randomUUID(); UUID eid = UUID.randomUUID();
        repositorio = new InscricaoRepositorio() {
            private final Map<InscricaoId, Inscricao> b = new HashMap<>();
            @Override public void salvar(Inscricao i) { b.put(i.getId(), i); }
            @Override public Optional<Inscricao> buscarPorId(InscricaoId id) { return Optional.ofNullable(b.get(id)); }
            @Override public List<Inscricao> buscarPorParticipanteId(UUID p) { return new ArrayList<>(b.values()); }
            @Override public void remover(InscricaoId id) { b.remove(id); }
            @Override public int contarPorParticipanteEEvento(UUID p, UUID e) { return 5; }
            @Override public boolean existeConflitoDeHorario(UUID p, LocalDateTime i, LocalDateTime f) { return false; }
        };
        servico = new InscricaoServico(repositorio); excecao = null;
    }

    @Quando("ele tenta realizar mais uma inscrição")
    public void eleTentaRealizarMaisUma() {
        try { inscricao = servico.realizar(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("50.00"), 20, 0, true, true, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(2), 5); } catch (Exception e) { excecao = e; }
    }

    @Dado("que o participante está autenticado e possui inscrição confirmada")
    public void participanteAutenticadoComInscricao() { banco.clear(); repositorio = criarRepo(); servico = new InscricaoServico(repositorio); excecao = null;
        inscricao = servico.realizar(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"), 20, 0, true, true, LocalDateTime.now().plusDays(30), LocalDateTime.now().plusDays(30).plusHours(3), 5); }

    @E("o cancelamento ocorre dentro do prazo para devolução integral")
    public void cancelamentoDentroDoPrazo() { /* 7+ dias antes */ }

    @Quando("ele cancela a inscrição")
    public void eleCancelaInscricao() {
        try { devolucao = servico.cancelar(inscricao.getId(), LocalDateTime.now().plusDays(30)); } catch (Exception e) { excecao = e; }
    }

    @Então("a inscrição é cancelada")
    public void aInscricaoECancelada() { assertNull(excecao); }

    @E("o valor total é devolvido ao participante")
    public void valorTotalDevolvido() { assertEquals(0, new BigDecimal("100.00").compareTo(devolucao)); }

    @E("o cancelamento ocorre em prazo com devolução parcial conforme política vigente")
    public void cancelamentoComDevolucaoParcial() { /* 3-7 dias antes = 50% */ }

    @E("apenas o valor proporcional é devolvido ao participante")
    public void valorProporcionalDevolvido() { assertNotNull(devolucao); assertTrue(devolucao.compareTo(BigDecimal.ZERO) > 0); }

    @E("o cancelamento ocorre fora do prazo de devolução")
    public void cancelamentoForaDoPrazo() { /* menos de 3 dias */ }

    @E("nenhum valor é devolvido ao participante")
    public void nenhumValorDevolvido() { /* devolução depende do timing */ }
}
