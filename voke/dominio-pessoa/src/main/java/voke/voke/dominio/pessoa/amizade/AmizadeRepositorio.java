package voke.voke.dominio.pessoa.amizade;

import voke.voke.dominio.pessoa.participante.ParticipanteId;

import java.util.List;
import java.util.Optional;

public interface AmizadeRepositorio {
    void salvar(Amizade amizade);
    Optional<Amizade> buscarPorId(AmizadeId id);
    List<Amizade> buscarPorParticipante(ParticipanteId participanteId);
    List<Amizade> buscarAtivasPorParticipante(ParticipanteId participanteId);
    boolean existeEntreParticipantes(ParticipanteId participanteA, ParticipanteId participanteB);
    void remover(AmizadeId id);
}
