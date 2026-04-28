package voke.voke.dominio.fidelidade.sugestao;

import voke.voke.dominio.compartilhado.EntidadeBase;

import java.util.Objects;
import java.util.UUID;

public class Sugestao extends EntidadeBase<SugestaoId> {

    private final UUID participanteId;
    private final UUID eventoId;
    private StatusSugestao status;

    public Sugestao(SugestaoId id, UUID participanteId, UUID eventoId) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(eventoId, "Evento é obrigatório");
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.status = StatusSugestao.PENDENTE;
    }

    public void aprovar() {
        this.status = StatusSugestao.APROVADA;
    }

    public void rejeitar() {
        this.status = StatusSugestao.REJEITADA;
    }

    public void expirar() {
        this.status = StatusSugestao.EXPIRADA;
    }

    public boolean estaPendente() { return status == StatusSugestao.PENDENTE; }

    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public StatusSugestao getStatus() { return status; }
}
