package voke.voke.dominio.pessoa.excecao;

public class AmizadeNaoEncontradaException extends RuntimeException {
    public AmizadeNaoEncontradaException() {
        super("Solicitação de amizade não encontrada");
    }
}
