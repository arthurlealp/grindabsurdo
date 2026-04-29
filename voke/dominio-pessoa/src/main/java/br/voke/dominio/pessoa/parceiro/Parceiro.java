package br.voke.dominio.pessoa.parceiro;

import br.voke.dominio.compartilhado.EntidadeBase;
import br.voke.dominio.pessoa.excecao.LimiteAtividadesException;
import br.voke.dominio.pessoa.organizador.OrganizadorId;
import br.voke.dominio.pessoa.participante.ParticipanteId;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Parceiro extends EntidadeBase<ParceiroId> {

    public static final int MAX_ATIVIDADES = 5;

    private final ParticipanteId participanteId;
    private final OrganizadorId organizadorId;
    private final Set<AtividadeParceiro> atividades;
    private BigDecimal saldoComissao;

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
        this.saldoComissao = BigDecimal.ZERO;
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

    public void creditarComissao(BigDecimal valorCompra, BigDecimal percentualComissao) {
        Objects.requireNonNull(valorCompra, "Valor da compra Ã© obrigatÃ³rio");
        Objects.requireNonNull(percentualComissao, "Percentual de comissÃ£o Ã© obrigatÃ³rio");
        if (valorCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da compra deve ser positivo");
        }
        if (percentualComissao.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Percentual de comissÃ£o nÃ£o pode ser negativo");
        }
        this.saldoComissao = this.saldoComissao.add(valorCompra.multiply(percentualComissao));
    }

    public ParticipanteId getParticipanteId() { return participanteId; }
    public OrganizadorId getOrganizadorId() { return organizadorId; }
    public Set<AtividadeParceiro> getAtividades() { return Collections.unmodifiableSet(atividades); }
    public BigDecimal getSaldoComissao() { return saldoComissao; }
}
