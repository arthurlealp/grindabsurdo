package voke.voke.dominio.pessoa.parceiro;

import voke.voke.dominio.compartilhado.EntidadeBase;
import voke.voke.dominio.pessoa.excecao.LimiteAtividadesException;
import voke.voke.dominio.pessoa.organizador.OrganizadorId;
import voke.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Parceiro extends EntidadeBase<ParceiroId> {

    public static final int MAX_ATIVIDADES = 5;

    private final ParticipanteId participanteId;
    private final OrganizadorId organizadorId;
    private final Set<AtividadeParceiro> atividades;

    public Parceiro(ParceiroId id, ParticipanteId participanteId, OrganizadorId organizadorId,
                    Set<AtividadeParceiro> atividadesIniciais) {
        super(id);
        Objects.requireNonNull(participanteId, "Participante é obrigatório");
        Objects.requireNonNull(organizadorId, "Organizador é obrigatório");
        Objects.requireNonNull(atividadesIniciais, "Atividades são obrigatórias");
        if (atividadesIniciais.size() > MAX_ATIVIDADES) {
            throw new LimiteAtividadesException();
        }
        this.participanteId = participanteId;
        this.organizadorId = organizadorId;
        this.atividades = EnumSet.copyOf(atividadesIniciais.isEmpty()
                ? EnumSet.noneOf(AtividadeParceiro.class)
                : atividadesIniciais);
    }

    public void adicionarAtividade(AtividadeParceiro atividade) {
        Objects.requireNonNull(atividade, "Atividade é obrigatória");
        if (atividades.size() >= MAX_ATIVIDADES) {
            throw new LimiteAtividadesException();
        }
        atividades.add(atividade);
    }

    public void removerAtividade(AtividadeParceiro atividade) {
        atividades.remove(atividade);
    }

    public ParticipanteId getParticipanteId() { return participanteId; }
    public OrganizadorId getOrganizadorId() { return organizadorId; }
    public Set<AtividadeParceiro> getAtividades() { return Collections.unmodifiableSet(atividades); }
}
