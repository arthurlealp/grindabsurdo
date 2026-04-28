package voke.voke.dominio.pessoa.parceiro;

import voke.voke.dominio.pessoa.organizador.OrganizadorId;
import voke.voke.dominio.pessoa.participante.ParticipanteId;

public interface PresencaConsulta {
    int contarEventosConfirmados(ParticipanteId participanteId, OrganizadorId organizadorId);
}
