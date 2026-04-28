package voke.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import voke.voke.dominio.compartilhado.*;
import voke.voke.dominio.pessoa.organizador.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarOrganizadorSteps {

    private OrganizadorRepositorio repositorio;
    private OrganizadorServico servico;
    private Organizador organizador;
    private Exception excecao;
    private final Map<OrganizadorId, Organizador> banco = new HashMap<>();

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
        };
    }

    @Dado("que um usuário deseja se cadastrar como organizador")
    public void queUmUsuarioDesejaSeCadastrarComoOrganizador() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new OrganizadorServico(repositorio);
        excecao = null;
        organizador = null;
    }

    @Quando("ele preenche os dados com CPF válido, e-mail único e data de nascimento comprovando 18 anos ou mais")
    public void elePreencheComDadosValidos() {
        try {
            organizador = servico.cadastrar(
                    new NomeCompleto("Carlos Organizador"),
                    new Cpf("529.982.247-25"),
                    new Email("carlos@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1))
            );
        } catch (Exception e) { excecao = e; }
    }

    @Então("a conta de organizador é criada com sucesso")
    public void aContaDeOrganizadorECriadaComSucesso() {
        assertNull(excecao);
        assertNotNull(organizador);
    }

    @Quando("ele preenche os dados com uma data de nascimento indicando menos de 18 anos")
    public void elePreencheComMenosDe18Anos() {
        try {
            organizador = servico.cadastrar(
                    new NomeCompleto("Jovem Organizador"),
                    new Cpf("529.982.247-25"),
                    new Email("jovem@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.now().minusYears(16))
            );
        } catch (Exception e) { excecao = e; }
    }

    @Quando("ele preenche os dados com um CPF inválido")
    public void elePreencheComCpfInvalidoOrg() {
        try {
            organizador = servico.cadastrar(
                    new NomeCompleto("Carlos Organizador"),
                    new Cpf("000.000.000-00"),
                    new Email("carlos@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1))
            );
        } catch (Exception e) { excecao = e; }
    }

    @Quando("ele informa um e-mail que já existe no sistema")
    public void eleInformaEmailJaExistente() {
        try {
            servico.cadastrar(new NomeCompleto("Primeiro"), new Cpf("529.982.247-25"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1990, 1, 1)));
            organizador = servico.cadastrar(new NomeCompleto("Segundo"), new Cpf("418.236.780-90"),
                    new Email("existente@email.com"), new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(1991, 1, 1)));
        } catch (Exception e) { excecao = e; }
    }

    @Dado("que o organizador está autenticado no sistema")
    public void queOOrganizadorEstaAutenticado() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new OrganizadorServico(repositorio);
        excecao = null;
        organizador = servico.cadastrar(
                new NomeCompleto("Carlos Organizador"),
                new Cpf("529.982.247-25"),
                new Email("carlos@email.com"),
                new Senha("Senha@123"),
                new DataNascimento(LocalDate.of(1990, 1, 1))
        );
    }

    @Quando("ele altera campos permitidos como nome ou telefone")
    public void eleAlteraCamposPermitidosOrganizador() {
        try {
            servico.atualizarDados(organizador.getId(), new NomeCompleto("Carlos Atualizado"), new Email("novo@email.com"));
        } catch (Exception e) { excecao = e; }
    }

    @Quando("ele tenta alterar sua data de nascimento")
    public void eleTentaAlterarDataNascimentoOrg() {
        try {
            organizador.alterarDataNascimento(new DataNascimento(LocalDate.of(1985, 1, 1)));
        } catch (Exception e) { excecao = e; }
    }

    @Quando("ele solicita a remoção da sua conta")
    public void eleSolicitaRemocaoOrg() {
        try {
            servico.remover(organizador.getId());
        } catch (Exception e) { excecao = e; }
    }

    @Então("a conta é removida do sistema")
    public void aContaERemovidaDoSistemaOrg() {
        assertNull(excecao);
        assertFalse(repositorio.buscarPorId(organizador.getId()).isPresent());
    }

    @E("o organizador não consegue mais fazer login")
    public void oOrganizadorNaoConsegueMaisFazerLogin() {
        assertTrue(!banco.containsKey(organizador.getId()));
    }
}
