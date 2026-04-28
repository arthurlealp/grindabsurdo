package voke.voke.aplicacao.fidelidade;

import voke.voke.dominio.fidelidade.recompensa.Recompensa;
import voke.voke.dominio.fidelidade.recompensa.RecompensaId;
import voke.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;

import java.util.Objects;
import java.util.UUID;

public class CadastrarRecompensaCasoDeUso {

    private final RecompensaRepositorio repositorio;

    public CadastrarRecompensaCasoDeUso(RecompensaRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public Recompensa executar(String nome, String descricao, int custoEmPontos,
                               int estoqueTotal, UUID organizadorId) {
        Recompensa recompensa = new Recompensa(
                RecompensaId.novo(), nome, descricao, custoEmPontos, estoqueTotal, organizadorId
        );
        repositorio.salvar(recompensa);
        return recompensa;
    }
}
