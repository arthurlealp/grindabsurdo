package voke.voke.aplicacao.fidelidade;

import voke.voke.dominio.fidelidade.sugestao.Sugestao;
import voke.voke.dominio.fidelidade.sugestao.SugestaoId;
import voke.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;

import java.util.Objects;
import java.util.UUID;

public class AvaliarSugestaoCasoDeUso {

    private final SugestaoRepositorio repositorio;

    public AvaliarSugestaoCasoDeUso(SugestaoRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public void executar(UUID sugestaoId, boolean aprovada) {
        Sugestao sugestao = repositorio.buscarPorId(new SugestaoId(sugestaoId))
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada"));
        if (aprovada) {
            sugestao.aprovar();
        } else {
            sugestao.rejeitar();
        }
        repositorio.salvar(sugestao);
    }
}
