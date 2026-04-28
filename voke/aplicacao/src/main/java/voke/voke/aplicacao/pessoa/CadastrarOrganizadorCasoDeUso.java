package voke.voke.aplicacao.pessoa;

import voke.voke.dominio.compartilhado.Cpf;
import voke.voke.dominio.compartilhado.DataNascimento;
import voke.voke.dominio.compartilhado.Email;
import voke.voke.dominio.compartilhado.NomeCompleto;
import voke.voke.dominio.compartilhado.Senha;
import voke.voke.dominio.pessoa.organizador.Organizador;
import voke.voke.dominio.pessoa.organizador.OrganizadorServico;

import java.time.LocalDate;
import java.util.Objects;

public class CadastrarOrganizadorCasoDeUso {

    private final OrganizadorServico servico;

    public CadastrarOrganizadorCasoDeUso(OrganizadorServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Organizador executar(String nome, String cpf, String email, String senha, LocalDate dataNascimento) {
        return servico.cadastrar(
                new NomeCompleto(nome),
                new Cpf(cpf),
                new Email(email),
                new Senha(senha),
                new DataNascimento(dataNascimento)
        );
    }
}
