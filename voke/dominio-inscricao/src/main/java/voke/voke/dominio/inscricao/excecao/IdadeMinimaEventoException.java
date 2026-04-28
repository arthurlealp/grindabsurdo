package voke.voke.dominio.inscricao.excecao;

public class IdadeMinimaEventoException extends RuntimeException {
    public IdadeMinimaEventoException() {
        super("Idade mínima não atingida para este evento");
    }
}
