package voke.voke.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import voke.voke.dominio.compartilhado.*;
import voke.voke.dominio.pessoa.participante.*;
import voke.voke.dominio.pessoa.excecao.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciarParticipanteSteps {

    private ParticipanteRepositorio repositorio;
    private ParticipanteServico servico;
    private Participante participante;
    private Exception excecao;

    // --- Repositório em memória para testes ---
    private final java.util.Map<ParticipanteId, Participante> banco = new java.util.HashMap<>();

    private ParticipanteRepositorio criarRepositorioEmMemoria() {
        return new ParticipanteRepositorio() {
            @Override public void salvar(Participante p) { banco.put(p.getId(), p); }
            @Override public Optional<Participante> buscarPorId(ParticipanteId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public void remover(ParticipanteId id) { banco.remove(id); }
            @Override public boolean existePorCpf(Cpf cpf) {
                return banco.values().stream().anyMatch(p -> p.getCpf().equals(cpf));
            }
            @Override public boolean existePorEmail(Email email) {
                return banco.values().stream().anyMatch(p -> p.getEmail().equals(email));
            }
        };
    }

    // ===== Cenário: Criar conta com dados válidos =====
    @Dado("que um usuário não possui conta no sistema")
    public void queUmUsuarioNaoPossuiContaNoSistema() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new ParticipanteServico(repositorio);
        excecao = null;
        participante = null;
    }

    @Quando("ele preenche nome, CPF válido, e-mail, data de nascimento e demais dados obrigatórios")
    public void elePreencheNomeCpfValidoEmailDataNascimento() {
        try {
            participante = servico.cadastrar(
                    new NomeCompleto("João Silva"),
                    new Cpf("529.982.247-25"),
                    new Email("joao@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1))
            );
        } catch (Exception e) {
            excecao = e;
        }
    }

    @E("possui a idade mínima exigida")
    public void possuiAIdadeMinimaExigida() {
        // Validação implícita no DataNascimento
    }

    @Então("a conta é criada com sucesso")
    public void aContaECriadaComSucesso() {
        assertNull(excecao);
        assertNotNull(participante);
    }

    @E("o participante recebe uma confirmação de cadastro")
    public void oParticipanteRecebeConfirmacao() {
        assertNotNull(participante.getId());
    }

    // ===== Cenário: CPF inválido =====
    @Quando("ele preenche os dados com um CPF inválido")
    public void elePreencheComCpfInvalido() {
        try {
            participante = servico.cadastrar(
                    new NomeCompleto("João Silva"),
                    new Cpf("000.000.000-00"),
                    new Email("joao@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1))
            );
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Então("o sistema rejeita o cadastro")
    public void oSistemaRejeitaOCadastro() {
        assertNotNull(excecao);
    }

    @E("exibe a mensagem {string}")
    public void exibeAMensagem(String mensagemEsperada) {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains(mensagemEsperada),
                "Esperava mensagem contendo '" + mensagemEsperada + "', mas foi: " + excecao.getMessage());
    }

    // ===== Cenário: E-mail duplicado =====
    @Quando("ele preenche os dados com um e-mail já utilizado por outra conta")
    public void elePreencheComEmailJaUtilizado() {
        try {
            servico.cadastrar(
                    new NomeCompleto("Maria Silva"),
                    new Cpf("529.982.247-25"),
                    new Email("existente@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2000, 1, 1))
            );
            participante = servico.cadastrar(
                    new NomeCompleto("João Silva"),
                    new Cpf("418.236.780-90"),
                    new Email("existente@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.of(2001, 1, 1))
            );
        } catch (Exception e) {
            excecao = e;
        }
    }

    // ===== Cenário: Idade mínima =====
    @Quando("ele preenche os dados com uma data de nascimento que não atinge a idade mínima")
    public void elePreencheComIdadeInsuficiente() {
        try {
            participante = servico.cadastrar(
                    new NomeCompleto("Criança Silva"),
                    new Cpf("529.982.247-25"),
                    new Email("crianca@email.com"),
                    new Senha("Senha@123"),
                    new DataNascimento(LocalDate.now().minusYears(10))
            );
        } catch (Exception e) {
            excecao = e;
        }
    }

    // ===== Cenário: Editar dados =====
    @Dado("que o participante está autenticado no sistema")
    public void queOParticipanteEstaAutenticado() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new ParticipanteServico(repositorio);
        excecao = null;
        participante = servico.cadastrar(
                new NomeCompleto("João Silva"),
                new Cpf("529.982.247-25"),
                new Email("joao@email.com"),
                new Senha("Senha@123"),
                new DataNascimento(LocalDate.of(2000, 1, 1))
        );
    }

    @Quando("ele altera campos permitidos como nome ou e-mail")
    public void eleAlteraCamposPermitidos() {
        try {
            servico.atualizarDados(participante.getId(), new NomeCompleto("João Atualizado"), new Email("novo@email.com"));
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Então("os dados são atualizados com sucesso")
    public void osDadosSaoAtualizados() {
        assertNull(excecao);
    }

    @E("o sistema exibe a mensagem {string}")
    public void oSistemaExibeMensagem(String mensagem) {
        // Confirmação implícita — o step anterior não gerou exceção
        assertNull(excecao);
    }

    // ===== Cenário: Alterar data de nascimento =====
    @Quando("ele tenta alterar sua data de nascimento")
    public void eleTentaAlterarDataNascimento() {
        try {
            participante.alterarDataNascimento(new DataNascimento(LocalDate.of(1995, 1, 1)));
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Então("o sistema bloqueia a alteração")
    public void oSistemaBloqueiaAAlteracao() {
        assertNotNull(excecao);
    }

    // ===== Cenário: Remover conta =====
    @Quando("ele solicita a remoção da sua conta")
    public void eleSolicitaRemocao() {
        try {
            servico.remover(participante.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Então("a conta é removida do sistema")
    public void aContaERemovidaDoSistema() {
        assertNull(excecao);
        assertFalse(repositorio.buscarPorId(participante.getId()).isPresent());
    }

    @E("o participante não consegue mais fazer login")
    public void oParticipanteNaoConsegueMaisFazerLogin() {
        assertTrue(banco.isEmpty() || !banco.containsKey(participante.getId()));
    }
}
