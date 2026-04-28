package voke.voke.aplicacao.pessoa;

import voke.voke.dominio.compartilhado.Email;
import voke.voke.dominio.compartilhado.NomeCompleto;
import voke.voke.dominio.pessoa.organizador.OrganizadorId;
import voke.voke.dominio.pessoa.organizador.OrganizadorServico;

import java.util.Objects;
import java.util.UUID;

public class EditarOrganizadorCasoDeUso {

    private final OrganizadorServico servico;

    public EditarOrganizadorCasoDeUso(OrganizadorServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public void executar(UUID organizadorId, String novoNome, String novoEmail) {
        servico.atualizarDados(
                new OrganizadorId(organizadorId),
                new NomeCompleto(novoNome),
                new Email(novoEmail)
        );
    }
}
