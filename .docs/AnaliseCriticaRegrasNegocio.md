# Análise crítica das regras de negócio

Fonte principal: `.docs/Funcionalidades.md`.

Base analisada: módulos de domínio, features BDD e steps de teste do projeto `voke`.

## Visão geral

O projeto tem boa aderência estrutural ao documento de funcionalidades: todas as 16 funcionalidades possuem pelo menos uma feature BDD correspondente, e os domínios principais estão separados de forma coerente. A principal conclusão crítica, porém, é que a aprovação dos testes não significa cobertura plena das regras de negócio. Em várias funcionalidades, os cenários validam o comportamento nominal do agregado ou simulam pré-condições nos steps, mas ainda não provam aspectos sistêmicos como persistência real, transações, concorrência, auditoria, soft delete, integração entre bounded contexts, notificações assíncronas, agenda, carteira e infraestrutura de pagamento.

Classificação usada:

- **Atendida:** a regra aparece implementada no domínio e coberta por BDD de forma razoável.
- **Parcial:** há parte da regra no domínio ou nos testes, mas falta integração, persistência, granularidade ou algum caso importante.
- **Não atendida:** a regra está no documento, mas não aparece implementada de forma material no domínio atual.

## Matriz por funcionalidade

| # | Funcionalidade | Cobertura geral | Leitura crítica |
|---|---|---|---|
| 1 | Gerenciar Participante | Parcial | Cadastro, CPF, idade mínima, unicidade e data de nascimento estão bem encaminhados. Faltam soft delete/LGPD, recuperação de senha, hash real de senha e dados voláteis mais ricos. |
| 2 | Gerenciar Organizador | Parcial | Maioridade, CPF, e-mail e imutabilidade da data aparecem. Faltam upgrade participante->organizador, trava por eventos ativos, banco, auditoria e dados profissionais. |
| 3 | Gerenciar Evento | Parcial | Colisão, nome duplicado, lote único e capacidade máxima existem. Cancelamento em cascata/estorno ainda é comentário/simulação, não regra integrada. |
| 4 | Gerenciar Inscrição | Parcial | Idade, conflito, limite por CPF e política de devolução existem no serviço. Concorrência de última vaga é simulada; não há lock/transação real nem baixa de lote. |
| 5 | Gerenciar Grupos de Evento | Parcial | Idade mínima, inscrição obrigatória e remoção do grupo aparecem. Falta validar dono organizador/evento, expulsão automática após cancelamento e rotina real pós-encerramento. |
| 6 | Gerenciar Avaliação | Parcial | Bloqueio por evento não finalizado, inscrição confirmada e duplicidade existem. A regra documental diz que segunda avaliação deveria virar edição; o código rejeita. Não há controle de permissão do organizador. |
| 7 | Gerenciar Notificações | Parcial | Bloqueio para evento cancelado e persistência de mensagens antigas aparecem. Público-alvo restrito é simulado; não há destinatários reais nem leitura por participante. |
| 8 | Gerenciar Favoritos | Parcial | Duplicidade e status permitido aparecem. Falta toggle, persistência/índice composto real e atualização reativa quando evento muda de status. |
| 9 | Gerenciar Carteira Virtual | Parcial | Limite diário, limite de retirada e saldo insuficiente existem. Faltam saldo bloqueado, atomicidade com compra, taxas por método de depósito e trilha financeira. |
| 10 | Gerenciar Amigos e Comunidades | Parcial | Consentimento, idade mínima e pré-requisito de amizade aparecem. Faltam validação dinâmica com evento, ausência de chat como restrição modelada e remoção em cascata completa. |
| 11 | Gerenciar Parceiros | Parcial | Pré-requisito de presença e limite de atividades aparecem. RBAC é muito simplificado e comissão/carteira são simuladas ou ausentes. |
| 12 | Gerenciar Cupons | Parcial | Uso único por CPF, limite de uso, escopo global/específico e trava de edição após uso existem. Falta validade temporal e validação real de escopo no checkout. |
| 13 | Gerenciar Carrinho | Parcial | Taxa por cartão, PIX sem taxa, máximo de dois eventos, cupom único e abate a zero existem. Faltam TTL de reserva, virada de lote, validação real de cupom e integração com inscrição/pagamento. |
| 14 | Gerenciar Pontos e Fidelidade | Parcial | Crédito por presença, proibição de compra direta, saldo insuficiente e expiração manual existem. Falta validar evento encerrado + check-in no próprio domínio e rotina de expiração real. |
| 15 | Gerenciar Recompensas com Pontos | Parcial | Estoque, esgotamento e congelamento de preço existem. Concorrência e atomicidade com débito de pontos são simuladas/ausentes. Soft delete histórico é apenas inativação simples. |
| 16 | Gerenciar Sugestões do Sistema | Parcial fraca | Há entidade e estados básicos. O motor de recomendação, match por interesses, teto semanal, filtros e expiração automática não estão materialmente implementados. |

## Análise detalhada por funcionalidade

### 1. Gerenciar Participante

**Regras bem representadas**

- **RN01 - Unicidade de e-mail e CPF:** `ParticipanteServico` consulta o repositório por CPF e e-mail antes de cadastrar.
- **RN02 - Validação matemática de CPF:** `Cpf` remove máscara, rejeita sequências repetidas e calcula os dígitos verificadores.
- **RN03 - Idade mínima:** `Participante` bloqueia cadastro abaixo de 16 anos.
- **RN04 - Imutabilidade da data de nascimento:** `alterarDataNascimento` lança exceção.
- **RN06 - Força mínima de senha:** `Senha` exige 8 caracteres, letra e número.

**Lacunas**

- A senha é validada, mas continua representada como valor em memória; não há hash criptográfico no domínio nem contrato que force armazenamento seguro.
- Remoção de conta é `remover` físico no repositório. Não há soft delete, anonimização, desativação de login ou verificação de histórico financeiro.
- Visualizar perfil, recuperar senha, telefone, endereço, foto e nome social não aparecem como modelo de domínio.

**Criticidade:** média. Para trabalho de domínio, a base está boa; para aderência integral ao documento, LGPD e senha segura são os maiores buracos.

### 2. Gerenciar Organizador

**Regras bem representadas**

- **RN02 - Maioridade:** `Organizador` exige 18 anos.
- **RN03 - Validação de CPF:** reaproveita o value object `Cpf`.
- **RN05 - Imutabilidade de data de nascimento:** há exceção específica.
- Parte de **RN01 - Unicidade:** e-mail e CPF são bloqueados no serviço.

**Lacunas**

- O documento pede migração/upgrade quando um CPF de participante vira organizador. O domínio atual trata participante e organizador como cadastros separados e não demonstra uma política de identidade única.
- Não há trava de exclusão por eventos ativos, repasses pendentes ou ingressos vendidos.
- Não há conta bancária, validação de titularidade, logs administrativos, dados profissionais, minibiografia ou links.

**Criticidade:** alta para regras financeiras e administrativas. O cadastro básico está ok, mas o organizador ainda não carrega as responsabilidades reais descritas.

### 3. Gerenciar Evento

**Regras bem representadas**

- **RN01 - Colisão física/temporal:** `EventoServico` consulta `buscarPorLocalEPeriodo` na criação e edição.
- **RN02 - Nome duplicado:** o serviço rejeita nome já existente.
- **RN03 - Um lote ativo por vez:** `Evento.criarNovoLote` rejeita novo lote se o lote atual ainda está ativo.
- **RN05 - Capacidade máxima:** o construtor e `criarNovoLote` impedem lote com quantidade acima da capacidade.

**Lacunas**

- **RN04 - Cancelamento em cascata e estorno:** `Evento.cancelar` muda status e encerra lote, mas não emite evento de domínio, não alcança inscrições e não inicia estorno.
- A regra de nome duplicado no documento fala em evento já existente e "Ativo". O código usa `existePorNome(nome)` sem considerar status, então pode bloquear reutilização de nome de evento cancelado/encerrado.
- A criação sequencial de lote não valida expiração por tempo nem esgotamento; apenas usa `isAtivo`.

**Criticidade:** média-alta. As invariantes internas do evento estão boas; os efeitos interdomínio do cancelamento ainda não existem.

### 4. Gerenciar Inscrição

**Regras bem representadas**

- **RN01 - Idade mínima:** `InscricaoServico.realizar` recebe idade do participante e idade mínima do evento e bloqueia quando necessário.
- **RN02 - Conflito de agenda:** consulta `existeConflitoDeHorario`.
- **RN03 - Limite por CPF:** consulta `contarPorParticipanteEEvento`.
- **RN05 - Devolução dinâmica:** `Inscricao.calcularDevolucao` implementa 100%, 50% e 0% conforme dias até o evento.

**Lacunas**

- A validação de idade usa inteiros passados por parâmetro; não cruza diretamente `DataNascimento` do participante com a classificação do evento.
- A regra de limite fala em CPF, mas o contrato usa `participanteId`. A equivalência participante/CPF fica implícita.
- **RN04 - Última vaga concorrente:** os testes simulam o segundo usuário sem vaga, mas não há lock otimista/pessimista, versão, transação ou baixa atômica de estoque do lote.
- O serviço recebe `possuiVagas` como boolean externo, então a ocupação real do lote não é alterada dentro da inscrição.

**Criticidade:** alta no ponto de concorrência. Para domínio puro está aceitável, mas o caso mais sensível comercialmente ainda não está garantido.

### 5. Gerenciar Grupos de Evento

**Regras bem representadas**

- **RN01 - Maioridade:** `GrupoEventoServico.adicionarMembro` bloqueia idade abaixo de 18.
- **RN02 - Inscrição obrigatória:** o mesmo método bloqueia quando `possuiInscricao` é falso.
- **RN03 - Encerramento:** há cenário que remove o grupo após encerramento.

**Lacunas**

- A inscrição é um boolean externo, não valida um ticket real nem consulta o domínio de inscrição.
- Não há expulsão automática quando a inscrição é cancelada.
- A remoção após encerramento é acionada no step; não há rotina, job ou evento de domínio.
- **RN04 - privilégio do organizador dono:** `criar`, `editarRegras` e `remover` não validam se o ator é o organizador dono do evento.

**Criticidade:** média-alta. A regra de acesso individual existe, mas as regras de propriedade e ciclo de vida ainda estão frágeis.

### 6. Gerenciar Avaliação

**Regras bem representadas**

- **RN01 - Unicidade:** o serviço consulta `existePorParticipanteEEvento`.
- **RN02 - Evento finalizado e inscrição confirmada:** `avaliar` exige ambos como pré-condições.
- A nota é validada entre 1 e 5 na entidade.

**Lacunas**

- O documento diz que uma segunda avaliação deve ser interpretada como edição do registro anterior. O código lança `AvaliacaoDuplicadaException`. Isso é uma divergência explícita entre regra escrita e implementação.
- Evento finalizado e inscrição confirmada são booleans passados ao serviço; não há consulta real ao status do evento nem ao status da inscrição.
- **RN03 - organizador somente leitura:** não há modelo de permissão/ator que impeça organizador de editar/excluir avaliação. A operação de editar/remove é aberta por ID.
- Consulta de avaliações e média geral não aparecem como comportamento de domínio.

**Criticidade:** média. O núcleo de avaliação funciona, mas há divergência semântica importante na duplicidade.

### 7. Gerenciar Notificações

**Regras bem representadas**

- **RN02 - Status do evento:** `NotificacaoServico.enviar` bloqueia quando `eventoAtivo` é falso.
- **RN03 - Persistência em cancelados:** os cenários mantêm notificação enviada antes do cancelamento.
- Edição marca a notificação como editada.

**Lacunas**

- **RN01 - público-alvo restrito:** não há destinatários, inscrição, participante, favorito ou cancelado modelados na notificação. O teste afirma "todos recebem", mas não valida uma lista real de inscritos.
- Evento ativo é um boolean, não status de evento consultado.
- Remoção não diferencia revogação de visualização, exclusão lógica ou histórico de auditoria.

**Criticidade:** média. A entidade notificação está correta como mensagem, mas a regra de entrega ainda é quase toda aplicação/infra.

### 8. Gerenciar Favoritos

**Regras bem representadas**

- **RN01 - unicidade:** `FavoritoServico.adicionar` rejeita favorito duplicado.
- **RN02 - status permitido:** só aceita `PUBLICADO` ou `ATIVO`.
- Remoção existe.

**Lacunas**

- O toggle de interface não está representado; duplicidade vira exceção, não alternância.
- A unicidade é de serviço/repositório; não há restrição persistente/índice composto no projeto.
- **RN03 - atualização reativa de status:** não há snapshot/status no favorito nem reação quando evento é cancelado/encerrado.
- Listar favoritos existe no repositório, mas a regra de histórico visual/desativação de compra não está modelada.

**Criticidade:** baixa-média. A regra principal está implementada; faltam comportamento reativo e persistência real.

### 9. Gerenciar Carteira Virtual

**Regras bem representadas**

- **RN01 - limite diário:** `CarteiraVirtual.adicionarSaldo` bloqueia acima de 5000.
- **RN02 - limite de saque:** `removerSaldo` bloqueia acima de 500.
- **RN06 - saldo negativo:** `removerSaldo` e `debitar` bloqueiam saldo insuficiente.
- PIX/cartão aparecem indiretamente em carrinho, mas não em depósito.

**Lacunas**

- **RN03 - saldo bloqueado para retirada:** não há separação entre saldo retirável e saldo de consumo.
- **RN04 - atomicidade com compra de ingresso:** não há transação que envolva carteira, reserva e inscrição.
- **RN05 - taxas por método de depósito:** não há método de depósito por cartão/PIX na carteira.
- Não há extrato, trilha contábil, auditoria ou histórico financeiro.

**Criticidade:** alta se a carteira for parte do fluxo financeiro real; média se for só prova de conceito.

### 10. Gerenciar Amigos e Comunidades

**Regras bem representadas**

- **RN01 - consentimento mútuo:** amizade nasce pendente e pode ser aceita/recusada/desfeita.
- **RN02 - idade mínima:** envio de solicitação valida idade mínima do solicitante.
- **RN03 - vínculo para grupo:** há serviço de comunidades que depende de amizade ativa.
- Compartilhamento de evento existe como `compartilharEvento(UUID)`.

**Lacunas**

- A idade mínima deveria validar ambos os usuários envolvidos; o serviço de amizade valida apenas o solicitante.
- **RN04 - ausência de chat:** está ausente por omissão, mas não há uma restrição explícita ou modelo que garanta isso.
- **RN05 - validação dinâmica de disponibilidade:** há cenários, mas a disponibilidade real do domínio de evento é simulada/externa.
- **RN06 - exclusão em cascata:** desfazer amizade não dispara remoção automática em comunidades.

**Criticidade:** média. Bom desenho básico de amizade; comunidades ainda carecem de integração com eventos e rotinas.

### 11. Gerenciar Parceiros

**Regras bem representadas**

- **RN01 - pré-requisito de fidelidade:** `ParceiroServico` exige pelo menos 5 presenças confirmadas via `PresencaConsulta`.
- Parte da **RN02 - RBAC:** `AtividadeParceiro` e limite de atividades restringem escopo.

**Lacunas**

- RBAC está muito abstrato. Não há autorização real impedindo criação de eventos, exclusão de grupos, manipulação financeira etc.
- **RN03 - comissão sistêmica:** não há fluxo de checkout integrado que credite carteira do parceiro e estorne em cancelamento.
- Links/códigos de parceiro, conversões e vínculo com cupom não aparecem como modelo forte.

**Criticidade:** média-alta. O pré-requisito é bom, mas monetização e autorização ainda são conceituais.

### 12. Gerenciar Cupons

**Regras bem representadas**

- **RN01 - uso único por CPF:** `Cupom.utilizar` guarda CPFs utilizados.
- Parte da **RN02 - limite de uso:** bloqueia quando quantidade máxima é atingida.
- **RN03 - escopo global/específico:** `eventoId == null` representa cupom global.
- **RN05 - trava de edição:** `atualizarDesconto` bloqueia alteração se já houve uso.

**Lacunas**

- **RN02 - validade temporal:** não há data de início/fim do cupom.
- **RN03 - escopo no checkout:** o cupom sabe se é global/específico, mas o carrinho não valida se o evento do item pertence ao escopo.
- **RN04 - abate zero:** está parcialmente no carrinho, que usa `max(BigDecimal.ZERO)`, mas não está no próprio cupom nem valida cenários de múltiplos itens/escopo.
- Não há inativação explícita; remover é físico via repositório.

**Criticidade:** média. Boa entidade de cupom, mas falta a ponte real com checkout.

### 13. Gerenciar Carrinho

**Regras bem representadas**

- **RN01 - taxa por método:** cartão aplica 5%, PIX não aplica taxa.
- **RN02 - máximo de 2 eventos:** `Carrinho.adicionarItem` conta eventos distintos.
- **RN03 - um cupom por transação:** `aplicarCupom` bloqueia segundo cupom.
- **RN04 parcial - liberar após compra:** `limpar` esvazia o carrinho.
- **RN04 do cupom - abate zero:** total com desconto não fica negativo.

**Lacunas**

- Cupom válido/expirado é simulado; o carrinho aceita qualquer código e desconto informado.
- **RN04 - TTL/reserva temporária:** não há tempo de expiração, lock de vaga ou retorno automático ao lote.
- **RN05 - virada de lote:** `ItemCarrinho` permite atualizar preço, mas não detecta mudança de lote nem bloqueia checkout com preço antigo.
- Não há validação de limite por CPF ao editar quantidade.
- Não há integração com pagamento/inscrição.

**Criticidade:** alta para checkout real. O cálculo local está bom, mas a reserva e consistência de estoque/preço ainda não existem.

### 14. Gerenciar Pontos e Fidelidade

**Regras bem representadas**

- **RN02 - proibição de compra direta:** `comprarPontos` sempre lança exceção.
- **RN03 - saldo suficiente:** `debitar` bloqueia pontos insuficientes.
- **RN04 parcial - expiração:** há método `expirarPontos`.
- O crédito por presença aparece em BDD e em `creditarPorPresenca`.

**Lacunas**

- **RN01 - validação dupla:** a entidade credita por presença, mas não valida por si mesma evento encerrado + check-in realizado; isso fica simulado nos steps.
- **RN04 - rotina diária:** expiração é manual, sem scheduler, data de aquisição ou lote de pontos com validade própria.
- **RN05 - atomicidade:** não há transação entre débito de pontos e geração de voucher/recompensa.
- Não há extrato de pontos.

**Criticidade:** média. Como entidade de saldo é coerente; como motor de fidelidade ainda está simplificado.

### 15. Gerenciar Recompensas com Pontos

**Regras bem representadas**

- **RN02 - congelamento de preço:** `alterarCusto` bloqueia alteração antes de 30 dias.
- **RN03 - estoque:** `resgatar` decrementa estoque restante e inativa ao esgotar.
- **RN04 parcial - exclusão lógica:** `inativar` existe.

**Lacunas**

- **RN01 - concorrência/locking:** não há lock, versão ou transação; o BDD simula priorização.
- **RN04 - histórico:** inativar não diferencia recompensa com resgate de recompensa sem resgate, nem mantém relação com histórico do usuário.
- **RN05 - atomicidade de saldo:** recompensa resgata estoque sem garantir débito de pontos atômico.

**Criticidade:** média-alta para consistência financeira de pontos.

### 16. Gerenciar Sugestões do Sistema

**Regras bem representadas**

- Entidade `Sugestao` tem ciclo simples: pendente, aprovada, rejeitada e expirada.
- Há BDD cobrindo aprovação, rejeição, edição e remoção.

**Lacunas**

- **RN01 - match por interesses:** não há preferências, tags, histórico ou favoritos como entrada do algoritmo.
- **RN02 - recalibragem por feedback negativo:** rejeitar muda status, mas não recalcula fila nem aplica peso negativo.
- **RN03 - teto semanal:** não há contador por semana ou política de máximo 4.
- **RN04 - filtros excludentes:** não há validação contra inscrição ativa, fila de espera, encerrado ou lotação máxima.
- **RN05 - automação de limpeza:** há `expirar`, mas não há ciclo semanal nem automação.

**Criticidade:** alta se recomendação for diferencial do produto; hoje a funcionalidade é CRUD/estado básico, não motor de recomendação.

## Pontos fortes

- A separação por bounded contexts está coerente e facilita evoluir regras sem misturar responsabilidades.
- Value objects importantes existem (`Cpf`, `Email`, `Senha`, `DataNascimento`, `NomeCompleto`), o que ajuda a manter validações fora dos controllers.
- As regras locais mais simples estão bem expressas em entidades/serviços: idade, CPF, duplicidade, limites, status, estoque e saldos.
- O BDD cobre praticamente todas as funcionalidades em nível de narrativa, o que é excelente para rastreabilidade acadêmica.

## Pontos críticos

- Muitos cenários BDD passam porque os steps simulam pré-condições com booleans ou repositórios em memória. Isso é aceitável para teste de domínio, mas não comprova regras sistêmicas como concorrência, integração entre módulos, pagamentos, carteira e eventos de domínio.
- O projeto ainda não tem uma camada forte de aplicação orquestrando casos de uso interdomínio: cancelamento de evento deveria falar com inscrição/carteira/notificação; checkout deveria falar com carrinho/cupom/evento/inscrição/carteira.
- Regras de infraestrutura estão descritas no documento, mas ainda não materializadas: locks, transações, jobs, auditoria, soft delete, hash, e índices únicos reais.
- Algumas divergências semânticas devem ser decididas: avaliação duplicada rejeita, mas o documento pede atualização; nome duplicado de evento ignora status, mas o documento fala em evento ativo; limite por CPF usa participanteId.

## Recomendações priorizadas

1. **Criar serviços de aplicação para fluxos interdomínio**
   - `CancelarEventoUseCase`: cancelar evento, invalidar inscrições, calcular estornos, creditar carteira e manter notificações.
   - `FinalizarCheckoutUseCase`: validar carrinho, cupom, lote, limite por CPF, pagamento, inscrição e baixa de vagas em uma transação.
   - `ResgatarRecompensaUseCase`: validar saldo, debitar pontos e baixar estoque atomicamente.

2. **Trocar booleans de pré-condição por consultas de domínio**
   - `eventoAtivo`, `eventoFinalizado`, `inscricaoConfirmada`, `possuiVagas` e `possuiInscricao` deveriam vir de portas/consultas explícitas, não de parâmetros soltos.

3. **Modelar concorrência onde a regra exige**
   - Última vaga, recompensa e resgate de pontos precisam de versão, lock ou contrato transacional.

4. **Adicionar persistência/constraints para unicidade real**
   - CPF/e-mail, favorito `(participanteId, eventoId)`, cupom por código, uso de cupom por CPF e inscrição por participante/evento devem ter restrições no banco, não só no serviço.

5. **Resolver lacunas de segurança e compliance**
   - Senha com hash, soft delete/anonimização, auditoria de organizador e histórico financeiro precisam sair da documentação e entrar no modelo/infra.

6. **Revisar divergências entre documento e código**
   - Avaliação duplicada: rejeitar ou editar?
   - Nome de evento duplicado: bloquear qualquer status ou só ativo?
   - Limite por CPF: usar `Cpf` explicitamente ou garantir 1:1 com participante?
   - Notificação removida: apagar fisicamente ou ocultar mantendo histórico?

## Conclusão

O projeto está consistente como protótipo de domínio orientado a BDD: as principais histórias existem, os testes passam e há regras centrais implementadas. A análise crítica mostra, entretanto, que a cobertura é majoritariamente de regras locais e cenários narrativos. Para ficar fiel ao `Funcionalidades.md`, o próximo salto não é criar mais features superficiais, mas fortalecer orquestração, persistência, transações e integração entre contextos. Em outras palavras: o vocabulário de negócio já está no lugar; agora falta dar peso operacional às regras mais sensíveis.
