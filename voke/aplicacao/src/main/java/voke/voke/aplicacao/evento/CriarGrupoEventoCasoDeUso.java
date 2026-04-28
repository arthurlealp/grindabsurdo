package voke.voke.aplicacao.evento;

import voke.voke.dominio.evento.grupo.GrupoEvento;
import voke.voke.dominio.evento.grupo.GrupoEventoServico;

import java.util.Objects;
import java.util.UUID;

public class CriarGrupoEventoCasoDeUso {

    private final GrupoEventoServico servico;

    public CriarGrupoEventoCasoDeUso(GrupoEventoServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public GrupoEvento executar(String nome, String regras, UUID eventoId, UUID organizadorId) {
        return servico.criar(nome, regras, eventoId, organizadorId);
    }
}
