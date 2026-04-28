package br.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import br.voke.dominio.compartilhado.*;
import br.voke.dominio.pessoa.organizador.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarOrganizadorSteps {

    private final ContextoPessoa ctx;
    private final Map<OrganizadorId, Organizador> banco = new HashMap<>();

    public GerenciarOrganizadorSteps(ContextoPessoa ctx) {
        this.ctx = ctx;
    }

    private OrganizadorRepositorio criarRepositorioEmMemoria() {
        return new OrganizadorRepositorio() {
            @Override public void salvar(Organizador o) { banco.put(o.getId(), o); }
            @Override public Optional<Organizador> buscarPorId(OrganizadorId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(OrganizadorId id) { banco.remove(id); }
            @Override public boolean existePorCpf(Cpf cpf) {
                return banco.values().stream().anyMatch(o -> o.getCpf().equals(cpf));
            }
            @Override public boolean existePorEmail(Email email) {
                return banco.values().stream().anyMatch(o -> o.getEmail().equals(email));
            }
            @Override public Optional<Organizador> buscarPorEmail(Email email) {
                return banco.values().stream().filter(o -> o.getEmail().equals(email)).findFirst();
            }
            @Override public Optional<Organizador> buscarPorCpf(Cpf cpf) {
                return banco.values().stream().filter(o -> o.getCpf().equals(cpf)).findFirst();
            }
        };
    }

    private void inicializar() {
        banco.clear();
        ctx.repoOrganizador = criarRepositorioEmMemoria();
        ctx.servicoOrganizador = new OrganizadorServico(ctx.repoOrganizador);
        ctx.atorAtual = ContextoPessoa.Ator.ORGANIZADOR;
        ctx.excecao = null;
        ctx.organizador = null;
    }

    @Dado("que um usuário deseja se cadastrar como organizador")
    public void queUmUsuarioDesejaSeCadastrarComoOrganizador() { inicializar(); }

    @Quando("ele preenche os dados com CPF válido, e-mail único e data de nascimento comprovando 18 anos ou mais")
    public void elePreencheComDadosValidos() {
        try {
            ctx.organizador = ctx.servicoOrganizador.cadastrar(
                    new NomeCompleto("Carlos Organizador"),
                    new Cpf("529.982.247-25"),
                    new Email("carlos@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Então("a conta de organizador é criada com sucesso")
    public void aContaDeOrganizadorECriadaComSucesso() {
        assertNull(ctx.excecao);
        assertNotNull(ctx.organizador);
    }

    @Quando("ele preenche os dados com uma data de nascimento indicando menos de 18 anos")
    public void elePreencheComMenosDe18Anos() {
        try {
            ctx.organizador = ctx.servicoOrganizador.cadastrar(
                    new NomeCompleto("Jovem Organizador"),
                    new Cpf("529.982.247-25"),
                    new Email("jovem@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.now().minusYears(16)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Quando("ele informa um e-mail que já existe no sistema")
    public void eleInformaEmailJaExistente() {
        try {
            ctx.servicoOrganizador.cadastrar(new NomeCompleto("Primeiro"), new Cpf("529.982.247-25"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1)));
            ctx.organizador = ctx.servicoOrganizador.cadastrar(new NomeCompleto("Segundo"), new Cpf("111.444.777-35"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1991, 1, 1)));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @Dado("que o organizador está autenticado no sistema")
    public void queOOrganizadorEstaAutenticado() {
        inicializar();
        ctx.organizador = ctx.servicoOrganizador.cadastrar(
                new NomeCompleto("Carlos Organizador"),
                new Cpf("529.982.247-25"),
                new Email("carlos@email.com"),
                new Senha("Senha@123"),
                new DataNascimento(LocalDate.of(1990, 1, 1)));
    }

    @Quando("ele altera campos permitidos como nome ou telefone")
    public void eleAlteraCamposPermitidosOrganizador() {
        try {
            ctx.servicoOrganizador.atualizarDados(ctx.organizador.getId(),
                    new NomeCompleto("Carlos Atualizado"), new Email("novo@email.com"));
        } catch (Exception e) { ctx.excecao = e; }
    }

    @E("o organizador não consegue mais fazer login")
    public void oOrganizadorNaoConsegueMaisFazerLogin() {
        assertFalse(ctx.repoOrganizador.buscarPorId(ctx.organizador.getId()).isPresent());
    }
}
