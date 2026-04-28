package voke.voke.dominio.evento.excecao;

public class NomeDuplicadoException extends RuntimeException {
    public NomeDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
