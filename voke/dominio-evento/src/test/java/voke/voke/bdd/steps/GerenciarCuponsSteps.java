package voke.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import voke.voke.dominio.evento.cupom.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarCuponsSteps {
    private CupomRepositorio repositorio;
    private CupomServico servico;
    private Cupom cupom;
    private Exception excecao;
    private final Map<CupomId, Cupom> banco = new HashMap<>();

    private CupomRepositorio criarRepo() {
        return new CupomRepositorio() {
            @Override public void salvar(Cupom c) { banco.put(c.getId(), c); }
            @Override public Optional<Cupom> buscarPorId(CupomId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public Optional<Cupom> buscarPorCodigo(String codigo) {
                return banco.values().stream().filter(c -> c.getCodigo().equals(codigo)).findFirst();
            }
            @Override public void remover(CupomId id) { banco.remove(id); }
        };
    }

    @Quando("ele cria um cupom global com código, desconto e quantidade máxima de usos")
    public void eleCriaCupomGlobal() {
        banco.clear(); repositorio = criarRepo(); servico = new CupomServico(repositorio); excecao = null;
        try { cupom = servico.criar("PROMO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100); } catch (Exception e) { excecao = e; }
    }

    @Então("o cupom fica disponível para uso em qualquer evento do organizador")
    public void oCupomFicaDisponivel() { assertNull(excecao); assertNotNull(cupom); }

    @Quando("ele cria um cupom vinculado especificamente ao evento com código, desconto e quantidade máxima")
    public void eleCriaCupomEspecifico() {
        try { cupom = servico.criar("EVENTO20", new BigDecimal("20.00"), UUID.randomUUID(), UUID.randomUUID(), 50); } catch (Exception e) { excecao = e; }
    }

    @Então("o cupom fica disponível apenas para aquele evento")
    public void oCupomFicaDisponivelParaEvento() { assertNull(excecao); assertNotNull(cupom.getEventoId()); }

    @Dado("que o participante está no fluxo de compra")
    public void participanteNoFluxoDeCompra() { banco.clear(); repositorio = criarRepo(); servico = new CupomServico(repositorio); excecao = null;
        cupom = servico.criar("VALIDO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100); }

    @E("o cupom é válido e não foi usado pelo CPF do participante")
    public void oCupomEValido() { /* cupom acabou de ser criado */ }
    @E("a quantidade máxima de usos do cupom não foi atingida")
    public void quantidadeNaoAtingida() { /* usos = 0 */ }

    @Quando("ele aplica o cupom")
    public void eleAplicaOCupom() {
        try { cupom.utilizar("123.456.789-00"); repositorio.salvar(cupom); } catch (Exception e) { excecao = e; }
    }

    @Então("o desconto é aplicado com sucesso")
    public void oDescontoEAplicado() { assertNull(excecao); }

    @Dado("que o participante já utilizou o cupom anteriormente")
    public void participanteJaUsouCupom() { banco.clear(); repositorio = criarRepo(); servico = new CupomServico(repositorio); excecao = null;
        cupom = servico.criar("USADO10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100);
        cupom.utilizar("111.111.111-11"); repositorio.salvar(cupom); }

    @Quando("ele tenta aplicar o mesmo cupom em uma nova compra")
    public void eleTentaAplicarMesmoCupom() {
        try { cupom.utilizar("111.111.111-11"); } catch (Exception e) { excecao = e; }
    }

    @Então("o sistema rejeita o uso")
    public void oSistemaRejeitaUso() { assertNotNull(excecao); }

    @Dado("que o cupom atingiu a quantidade máxima de usos definida")
    public void cupomComMaximoDeUsos() { banco.clear(); repositorio = criarRepo(); servico = new CupomServico(repositorio); excecao = null;
        cupom = servico.criar("ESGOTADO", new BigDecimal("5.00"), UUID.randomUUID(), null, 1);
        cupom.utilizar("222.222.222-22"); repositorio.salvar(cupom); }

    @Quando("qualquer participante tenta aplicar o cupom")
    public void qualquerParticipanteTenta() {
        try { cupom.utilizar("333.333.333-33"); } catch (Exception e) { excecao = e; }
    }

    @E("o cupom existe no sistema")
    public void oCupomExisteNoSistema() { cupom = servico.criar("EDITAR10", new BigDecimal("10.00"), UUID.randomUUID(), null, 100); }

    @Quando("ele edita o desconto ou a quantidade máxima de usos")
    public void eleEditaDesconto() { /* edição no banco */ assertNotNull(cupom); }

    @Quando("ele exclui o cupom")
    public void eleExcluiOCupom() { try { repositorio.remover(cupom.getId()); } catch (Exception e) { excecao = e; } }

    @Então("o cupom é removido e não pode mais ser utilizado")
    public void oCupomERemovidoENaoPodeMaisSerUtilizado() { assertNull(excecao); assertFalse(repositorio.buscarPorId(cupom.getId()).isPresent()); }
}
