package voke.voke.dominio.pessoa.excecao;

public class MaioridadeObrigatoriaException extends RuntimeException {
    public MaioridadeObrigatoriaException() {
        super("Organizador deve ter pelo menos 18 anos");
    }
}
