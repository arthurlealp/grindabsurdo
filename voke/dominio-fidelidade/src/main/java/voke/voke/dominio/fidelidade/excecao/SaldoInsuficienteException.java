package voke.voke.dominio.fidelidade.excecao;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente");
    }
}
