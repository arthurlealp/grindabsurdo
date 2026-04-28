# Plano de implementação de todas as regras de negócio

Objetivo: organizar a entrega completa das regras descritas em `.docs/Funcionalidades.md`, mantendo o padrão arquitetural do repositório de referência do professor: DDD, Clean Architecture, módulos `dominio-*` puros, BDD com Cucumber/JUnit, casos de uso em `aplicacao` e infraestrutura separada.

Este plano parte da análise crítica registrada em `.docs/AnaliseCriticaRegrasNegocio.md`.

## Princípios de implementação

1. **Preservar o padrão do repositório**
   - Não importar Spring, JPA, Hibernate ou frameworks nos módulos `dominio-*`.
   - Manter entidades, value objects, exceções e interfaces de repositório dentro dos domínios.
   - Colocar orquestrações interdomínio no módulo `aplicacao`.
   - Colocar persistência, locks, transações, jobs e integrações técnicas no módulo `infraestrutura`.
   - Manter BDD em `dominio-*/src/test/resources/features` e steps em `dominio-*/src/test/java/br/voke/bdd/steps`.
   - Preferir `Contexto*` + `Shared*Steps` com PicoContainer quando houver steps compartilhados.

2. **Fluxo obrigatório por regra**
   - Ler a regra em `Funcionalidades.md`.
   - Criar ou ajustar cenário BDD que expresse a regra.
   - Rodar o teste do módulo e confirmar falha esperada quando a regra ainda não existe.
   - Implementar no menor nível correto:
     - Value object ou entidade, se for invariante local.
     - Serviço de domínio, se depender de repositório/consulta do próprio contexto.
     - Caso de uso em `aplicacao`, se cruzar bounded contexts.
     - Infraestrutura, se exigir persistência, transação, lock, job, hash ou auditoria.
   - Rodar testes do módulo.
   - Rodar testes do reactor completo antes de fechar a onda.

3. **Comandos de validação**

   Rodar tudo:

   ```powershell
   .\mvnw.cmd -f voke\pom.xml test
   ```

   Rodar módulo específico com dependências:

   ```powershell
   .\mvnw.cmd -f voke\pom.xml -pl dominio-pessoa -am test
   .\mvnw.cmd -f voke\pom.xml -pl dominio-evento -am test
   .\mvnw.cmd -f voke\pom.xml -pl dominio-inscricao -am test
   .\mvnw.cmd -f voke\pom.xml -pl dominio-fidelidade -am test
   ```

   Rodar camadas integradas após avançar em aplicação/infra:

   ```powershell
   .\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura,apresentacao-backend -am test
   ```

## Ordem recomendada

A ordem abaixo reduz retrabalho. Primeiro fechamos regras locais de domínio; depois as regras que exigem integração entre contextos; por fim infraestrutura técnica.

| Onda | Foco | Por que vem nessa ordem |
|---|---|---|
| 0 | Baseline e padronização dos testes | Garantir que qualquer mudança parte de build verde. |
| 0.5 | Decisões semânticas obrigatórias | Resolver divergências entre documento e código antes de implementar regras ambíguas. |
| 1 | Pessoa e identidade | CPF, senha, soft delete, perfis e permissões impactam vários fluxos. |
| 2 | Evento, inscrição e carrinho | Núcleo transacional da plataforma: evento, estoque, checkout e cancelamento. |
| 3 | Cupom, carteira, pontos e recompensas | Regras financeiras e de fidelidade dependem do checkout. |
| 4 | Social: grupos, amigos, comunidades e parceiros | Dependem de pessoa, evento, inscrição e carteira. |
| 5 | Avaliações, notificações, favoritos e sugestões | Regras reativas, de leitura/histórico e reputação. |
| 6 | Consolidação de infraestrutura: transações, locks, jobs, auditoria e hash | Materializa garantias que ainda não tiverem sido fechadas junto das ondas funcionais. |
| 7 | Revisão final de rastreabilidade | Provar que cada RN tem cenário, implementação e validação. |

## Onda 0 - Baseline e padronização

### Tarefas

- Confirmar build completo verde antes de iniciar:

  ```powershell
  .\mvnw.cmd -f voke\pom.xml test
  ```

- Registrar resultado no relatório da entrega.
- Garantir que `dominio-evento` e `dominio-inscricao` continuam com `cucumber-picocontainer`, como `dominio-pessoa` e `dominio-fidelidade`.
- Evitar warnings novos de steps duplicados.
- Não trocar o padrão de `@SelectClasspathResource("features")` agora, porque esse é o padrão já usado no projeto.

### Critério de pronto

- Todos os testes existentes passam.
- Nenhuma mudança funcional feita ainda.

## Onda 0.5 - Decisões semânticas obrigatórias

Antes de iniciar as ondas funcionais, resolver e registrar as divergências entre `.docs/Funcionalidades.md` e o código atual. Essas decisões evitam retrabalho e impedem que cenários BDD sejam escritos contra interpretações diferentes da regra.

### Decisões

- Avaliação duplicada: a segunda avaliação deve editar a avaliação existente, conforme o documento, ou continuar rejeitando duplicidade?
- Nome duplicado de evento: bloquear qualquer evento com mesmo nome ou apenas evento ativo/publicado?
- Limite por CPF: modelar `Cpf` explicitamente no fluxo de inscrição/carrinho ou assumir equivalência 1:1 entre `ParticipanteId` e CPF?
- Remoção de notificações/cupons/recompensas: exclusão física, inativação ou soft delete com histórico?
- Regras de infraestrutura críticas: implementar junto da onda funcional quando forem parte da garantia da RN, ou deixar apenas a consolidação final para a Onda 6?

### Critério de pronto

- Decisões registradas no relatório da entrega ou em documento de rastreabilidade.
- Cenários BDD novos refletem a decisão adotada.
- Nenhuma regra ambígua é implementada sem decisão explícita.

## Onda 1 - Pessoa e identidade

### 1. Gerenciar Participante

Regras pendentes ou parciais:

- RN05 - Soft delete/LGPD.
- RN06 - Hash de senha.
- Ações documentadas sem modelo: recuperar senha, visualizar perfil, telefone/endereço/foto/nome social.

Plano:

- Criar estado de conta em `Participante`: ativa, removida/anonimizada.
- Adicionar operação de remoção lógica quando houver histórico financeiro.
- Criar porta de consulta, por exemplo `HistoricoFinanceiroConsulta`, para o domínio ou aplicação decidir se remove fisicamente ou anonimiza.
- Trocar remoção simples por caso de uso em `aplicacao`, mantendo `ParticipanteServico` focado no domínio.
- Criar value object ou serviço de domínio para senha hasheada:
  - `Senha` valida força.
  - `SenhaHash` representa valor persistível.
  - A geração do hash fica em porta da aplicação/infra, não no domínio.
- Adicionar BDD:
  - remover conta sem histórico.
  - remover conta com histórico financeiro deve anonimizar e desativar login.
  - senha fraca rejeitada.
  - senha válida não deve ser exposta como texto plano no objeto persistível.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-pessoa -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao -am test
```

### 2. Gerenciar Organizador

Regras pendentes ou parciais:

- RN01 - Upgrade participante -> organizador.
- RN04 - Trava de exclusão por eventos ativos/repasses pendentes.
- RN06 - Conta bancária com titularidade.
- RN07 - Logs administrativos.

Plano:

- Modelar `PerfilUsuario` ou caso de uso de upgrade em `aplicacao`, evitando duplicidade de CPF.
- Criar portas:
  - `EventoOrganizadorConsulta` para eventos ativos.
  - `RepasseConsulta` para repasses pendentes.
  - `AuditoriaAdministrativaPorta` para logs.
  - `ContaBancariaValidador` para titularidade.
- Adicionar value object `ContaBancaria` ou `DadosBancarios`.
- Bloquear remoção quando as consultas indicarem pendência.
- Adicionar BDD:
  - CPF existente como participante permite upgrade.
  - exclusão com evento ativo é bloqueada.
  - conta bancária com CPF divergente é rejeitada.
  - alteração sensível gera registro de auditoria.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-pessoa -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao -am test
```

## Onda 2 - Evento, inscrição e carrinho

### 3. Gerenciar Evento

Regras pendentes ou parciais:

- RN02 - Nome duplicado deve considerar evento ativo, não todo evento.
- RN03 - Novo lote só após expiração ou esgotamento.
- RN04 - Cancelamento em cascata e estorno.

Plano:

- Ajustar `EventoRepositorio` para diferenciar `existeAtivoPorNome`.
- Modelar estado/expiração de `Lote` com data final ou regra de esgotamento.
- Criar evento de domínio ou resultado de caso de uso para `EventoCancelado`.
- Criar ou evoluir `CancelarEventoUseCase` em `aplicacao`:
  - cancela evento.
  - busca inscrições ativas.
  - cancela inscrições.
  - calcula estorno integral.
  - credita carteira.
  - registra notificação de cancelamento quando aplicável.
- Nos domínios, manter apenas interfaces/portas necessárias.

BDD:

- criar evento com mesmo nome de cancelado deve ser permitido, se essa for a interpretação final.
- criar lote 2 quando lote 1 está ativo deve falhar.
- criar lote 2 após esgotamento/expiração deve passar.
- cancelar evento com inscrições deve invalidar inscrições e gerar estorno.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-evento,dominio-inscricao,dominio-fidelidade -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao -am test
```

### 4. Gerenciar Inscrição

Regras pendentes ou parciais:

- RN01 - Usar data de nascimento real do participante, não inteiro solto.
- RN03 - Limite por CPF explícito.
- RN04 - Lock/transação da última vaga.
- Integração real com lote/estoque.

Plano:

- Introduzir porta `ParticipanteConsulta` retornando data de nascimento/CPF.
- Introduzir porta `EventoConsulta` retornando idade mínima, período, status e lote.
- Trocar parâmetros soltos de `InscricaoServico.realizar` por comando/DTO de domínio mais explícito.
- Criar ou evoluir `FinalizarInscricaoUseCase` em `aplicacao` para coordenar:
  - participante.
  - evento/lote.
  - conflito de agenda.
  - limite por CPF.
  - reserva/baixa de vaga.
- Em infraestrutura, preparar implementação com lock otimista/pessimista para lote.

BDD:

- inscrição abaixo da idade usando data de nascimento.
- limite por CPF com dois participantes vinculados a CPFs diferentes.
- última vaga concorrente com primeira confirmação e segunda rejeição.
- inscrição reduz vaga disponível do lote.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-inscricao,dominio-evento,dominio-pessoa -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

### 13. Gerenciar Carrinho

Regras pendentes ou parciais:

- RN03 - Validação real de cupom, prazo e limite.
- RN04 - Reserva temporária de estoque com TTL.
- RN05 - Invalidação por virada de lote.
- Editar quantidade respeitando limite por CPF.

Plano:

- Adicionar `ReservaCarrinho` ou `ReservaVaga` com expiração.
- Criar porta `Relogio` para testar TTL sem depender de `LocalDateTime.now()` diretamente.
- Criar porta `CupomConsulta` para validar código, vigência, escopo e uso.
- `Carrinho` deve guardar dados suficientes do lote: `loteId`, preço cotado, momento da reserva.
- Criar ou evoluir caso de uso `AtualizarCarrinhoUseCase` para detectar virada de lote e exigir atualização de preço.
- Implementar expiração em infraestrutura via job ou serviço agendado.

BDD:

- item expira após TTL e libera vaga.
- checkout bloqueia item com lote alterado.
- editar quantidade acima do limite por CPF falha.
- cupom expirado falha por consulta real, não exceção artificial no step.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-inscricao,dominio-evento -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

## Onda 3 - Cupom, carteira, pontos e recompensas

### 12. Gerenciar Cupons

Regras pendentes ou parciais:

- RN02 - Vigência temporal.
- RN03 - Escopo validado contra itens do carrinho.
- RN04 - Abate zero em checkout integrado.
- Remoção/inativação em vez de deleção física para histórico.

Plano:

- Adicionar `periodoInicio` e `periodoFim` ao `Cupom`.
- Adicionar método `aplicavelAoEvento(eventoId, organizadorId)`.
- Criar `UsoCupom` ou registrar usos por CPF de forma persistível.
- Alterar remoção para inativação quando houver uso.
- Criar ou evoluir `FinalizarCheckoutUseCase` para integrar cupom ao checkout.

BDD:

- cupom fora da vigência é rejeitado.
- cupom específico não aplica em outro evento.
- cupom global aplica apenas em eventos do organizador dono.
- cupom usado não permite alterar desconto.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-evento,dominio-inscricao -am test
```

### 9. Gerenciar Carteira Virtual

Regras pendentes ou parciais:

- RN03 - Saldo bloqueado/consumo.
- RN04 - Atomicidade com compra.
- RN05 - Taxas por método de depósito.
- Extrato/histórico.

Plano:

- Separar saldo em `saldoDisponivel` e `saldoConsumo`.
- Criar `LancamentoCarteira` para extrato.
- Criar métodos de depósito com `MetodoDeposito`.
- Criar porta transacional em aplicação para compra com carteira.
- Garantir rollback via transação de infraestrutura no use case.

BDD:

- saldo promocional não pode ser sacado.
- depósito com cartão aplica taxa.
- compra sem saldo suficiente falha sem gerar inscrição.
- falha na reserva devolve saldo.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-fidelidade,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

### 14. Gerenciar Pontos e Fidelidade

Regras pendentes ou parciais:

- RN01 - Validar evento encerrado + check-in.
- RN04 - Expiração por data de aquisição.
- RN05 - Atomicidade de resgate.
- Extrato.

Plano:

- Trocar saldo simples por lotes de pontos ou lançamentos com data de expiração.
- Criar ou evoluir `CreditarPontosPorPresencaUseCase` consultando evento e inscrição.
- Criar job de expiração em infraestrutura.
- Criar `ExtratoPontos`.

BDD:

- compra sem check-in não gera ponto.
- check-in em evento ainda ativo não gera ponto.
- pontos vencidos são removidos por rotina.
- tentativa de resgate com falha de voucher não debita pontos.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-fidelidade,dominio-evento,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

### 15. Gerenciar Recompensas com Pontos

Regras pendentes ou parciais:

- RN01 - Lock/prioridade em resgate concorrente.
- RN04 - Soft delete com histórico.
- RN05 - Débito de pontos + baixa de estoque atômicos.

Plano:

- Criar ou evoluir `ResgatarRecompensaUseCase`.
- Em domínio, manter `Recompensa.resgatar` e `ContaPontos.debitar`.
- Em aplicação, coordenar os dois com transação.
- Em infraestrutura, usar lock/versionamento na recompensa.
- Diferenciar `inativar` de `remover` conforme existência de resgate.

BDD:

- resgate simultâneo vence edição.
- resgate sem saldo não baixa estoque.
- falha na baixa de estoque não debita pontos.
- recompensa já resgatada é inativada, não removida fisicamente.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-fidelidade -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

## Onda 4 - Social, grupos e parceiros

### 5. Gerenciar Grupos de Evento

Regras pendentes ou parciais:

- RN02 - Expulsão automática após cancelamento de inscrição.
- RN03 - Rotina real de encerramento.
- RN04 - Somente organizador dono cria/edita/remove.

Plano:

- Adicionar dono do evento como consulta obrigatória no caso de uso.
- Criar ou evoluir `GerenciarGrupoEventoUseCase` em aplicação.
- Criar listener/use case para `InscricaoCancelada` removendo membro do grupo.
- Criar rotina/job para encerramento de grupos após fim do evento.

BDD:

- participante não dono não pode criar grupo.
- organizador que não é dono não pode editar/excluir grupo.
- cancelamento de inscrição remove membro.
- encerramento do evento remove grupo automaticamente.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-evento,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

### 10. Gerenciar Amigos e Comunidades

Regras pendentes ou parciais:

- RN02 - Validar idade de ambos.
- RN04 - Ausência de chat como restrição explícita.
- RN05 - Disponibilidade dinâmica do card.
- RN06 - Remoção em cascata após desfazer amizade.

Plano:

- Alterar `AmizadeServico.enviarSolicitacao` para receber/consultar ambos os participantes.
- Em comunidade, modelar apenas cards de evento, sem mensagem livre.
- Criar porta `EventoDisponibilidadeConsulta`.
- Criar ou evoluir caso de uso `DesfazerAmizadeUseCase` removendo o participante das comunidades relacionadas.

BDD:

- receptor menor de 16 também bloqueia amizade.
- comunidade não aceita mensagem livre.
- card muda para esgotado quando evento sem vagas.
- desfazer amizade remove membro de grupos compartilhados.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-pessoa,dominio-evento -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao -am test
```

### 11. Gerenciar Parceiros

Regras pendentes ou parciais:

- RN02 - RBAC real.
- RN03 - Comissão integrada ao checkout/carteira.

Plano:

- Tornar `AtividadeParceiro` mais explícita e associar permissões a ações.
- Criar `AutorizacaoParceiroServico` no domínio ou aplicação.
- Integrar cupom de parceiro ao checkout.
- Criar ou evoluir `CreditarComissaoParceiroUseCase` após pagamento confirmado.
- Criar estorno de comissão quando compra for cancelada.

BDD:

- parceiro não pode criar evento.
- parceiro não pode alterar capacidade/preço/repasses.
- compra com cupom de parceiro credita carteira.
- cancelamento da compra estorna comissão.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-pessoa,dominio-evento,dominio-fidelidade,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

## Onda 5 - Avaliações, notificações, favoritos e sugestões

### 6. Gerenciar Avaliação

Regras pendentes ou parciais:

- RN01 - Segunda avaliação deve ser tratada conforme decisão da Onda 0.5.
- RN02 - Evento finalizado e inscrição confirmada devem vir de consultas reais, não de booleans soltos.
- RN03 - Organizador dono do evento deve ter acesso somente leitura.
- Consulta de avaliações e média geral precisam respeitar o histórico do evento.

Plano:

- Ajustar `AvaliacaoServico` para aplicar a decisão sobre duplicidade: editar avaliação existente ou rejeitar explicitamente.
- Criar ou evoluir `AvaliarEventoCasoDeUso` para consultar status do evento e status da inscrição por portas explícitas.
- Criar porta `EventoAvaliacaoConsulta` para validar se o evento está finalizado/encerrado e quem é o organizador dono.
- Criar porta `InscricaoAvaliacaoConsulta` para validar inscrição confirmada ou check-in, conforme regra final adotada.
- Bloquear edição/exclusão de avaliação quando o ator for organizador.
- Adicionar consulta de média/listagem sem permitir mutação externa pelo organizador.

BDD:

- segunda avaliação do mesmo participante atualiza ou rejeita conforme decisão documentada.
- evento não finalizado bloqueia avaliação.
- participante sem inscrição confirmada não avalia.
- organizador consegue consultar avaliações, mas não editar nem excluir.
- média do evento considera apenas avaliações válidas.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-evento,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao -am test
```

### 7. Gerenciar Notificações

Regras pendentes ou parciais:

- RN01 - Público-alvo real.
- RN02 - Status completo: ativo, rascunho, finalizado, encerrado.
- RN03 - Leitura read-only após cancelamento com destinatário antigo.

Plano:

- Criar `DestinatarioNotificacao` ou lista de participantes no envio.
- Criar porta `InscritosEventoConsulta`.
- `EnviarNotificacaoUseCase` deve filtrar inscritos confirmados e excluir cancelados/favoritos.
- Notificações antigas devem permanecer consultáveis por destinatário.

BDD:

- favorito não recebe notificação.
- inscrito cancelado antes do envio não recebe.
- inscrito antigo lê notificação de evento cancelado.
- evento finalizado bloqueia novo envio.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-evento,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao -am test
```

### 8. Gerenciar Favoritos

Regras pendentes ou parciais:

- RN01 - Toggle.
- RN03 - Status reativo/histórico.
- Constraint persistente.

Plano:

- Criar método/caso de uso `alternarFavorito`.
- Adicionar snapshot de status ou consulta reativa para favoritos.
- Infraestrutura deve ter índice único `(participanteId, eventoId)`.
- Ao evento mudar de status, favorito permanece, mas compra/inscrição fica indisponível.

BDD:

- clicar favorito existente remove.
- evento cancelado permanece na lista com status cancelado.
- link de inscrição desativado para favorito encerrado/cancelado.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-evento -am test
.\mvnw.cmd -f voke\pom.xml -pl infraestrutura -am test
```

### 16. Gerenciar Sugestões do Sistema

Regras pendentes ou parciais:

- RN01 - Match por interesses.
- RN02 - Feedback negativo recalibra.
- RN03 - Máximo 4 por semana.
- RN04 - Filtros excludentes.
- RN05 - Expiração semanal automática.

Plano:

- Modelar `PreferenciaParticipante` e tags/interesses.
- Criar `GerarSugestoesUseCase`.
- Criar portas:
  - `HistoricoEventosConsulta`.
  - `FavoritosConsulta`.
  - `InscricoesConsulta`.
  - `DisponibilidadeEventoConsulta`.
  - `Relogio`.
- Sugestões devem carregar semana/ciclo.
- Job semanal expira sugestões pendentes e gera novas.

BDD:

- gerar sugestões apenas com tag compatível.
- não sugerir evento já inscrito.
- não sugerir evento encerrado/esgotado.
- no máximo 4 sugestões por semana.
- rejeitar sugestão evita subcategoria semelhante no próximo ciclo.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl dominio-fidelidade,dominio-evento,dominio-inscricao -am test
.\mvnw.cmd -f voke\pom.xml -pl aplicacao,infraestrutura -am test
```

## Onda 6 - Infraestrutura técnica

Esta onda não deve contaminar os módulos de domínio. Ela consolida as portas definidas nas ondas anteriores, mas não significa que toda infraestrutura deve esperar até o fim. Quando uma regra depender de garantia técnica para ser verdadeira, a infraestrutura mínima deve ser entregue junto da própria onda funcional.

Exemplos de infraestrutura que deve nascer junto da regra:

- Hash de senha junto de participante/identidade.
- Constraint única de favorito junto de favoritos.
- Lock/versionamento de lote junto de inscrição/carrinho.
- Transação de checkout junto de carteira, inscrição e cupom.
- Lock/versionamento de recompensa junto de resgate.
- Jobs de expiração quando a RN exigir comportamento automático, não apenas método manual.

### Persistência e constraints

Implementar em `infraestrutura`:

- Unicidade real de CPF e e-mail.
- Unicidade real de favorito por participante/evento.
- Unicidade de uso de cupom por cupom/CPF.
- Versionamento de lote e recompensa para lock otimista.
- Soft delete/anonymization para participante/recompensa/cupom quando aplicável.

### Transações

Casos transacionais prioritários:

- Finalizar checkout.
- Cancelar evento com estornos.
- Cancelar inscrição.
- Resgatar recompensa.
- Creditar comissão de parceiro.

### Jobs

Jobs necessários:

- Expirar reservas de carrinho.
- Expirar pontos.
- Encerrar grupos após fim do evento.
- Expirar sugestões semanais e gerar novas.

### Segurança/auditoria

- Hash de senha em adaptador de infraestrutura.
- Logs administrativos para organizador.
- Auditoria de alterações sensíveis.

Validação:

```powershell
.\mvnw.cmd -f voke\pom.xml -pl infraestrutura,aplicacao -am test
.\mvnw.cmd -f voke\pom.xml test
```

## Onda 7 - Rastreabilidade final

Criar tabela final de evidências:

| Funcionalidade | RN | Feature | Step | Classe de domínio | Caso de uso | Teste passou |
|---|---|---|---|---|---|---|

Critérios de aceite:

- Toda RN do `Funcionalidades.md` aparece na tabela.
- Toda RN tem ao menos um cenário BDD.
- Toda RN local tem implementação em domínio.
- Toda RN interdomínio tem caso de uso em `aplicacao`.
- Toda RN de persistência/concorrência/job/auditoria tem implementação em `infraestrutura`.
- `.\mvnw.cmd -f voke\pom.xml test` passa.

## Checklist por implementação

Use este checklist a cada regra:

- [ ] Feature Gherkin criada/ajustada.
- [ ] Step implementado sem duplicidade.
- [ ] Regra implementada no nível correto.
- [ ] Exceção de domínio criada quando necessário.
- [ ] Repositório/porta criada somente se a regra exigir consulta externa.
- [ ] Domínio continua sem Spring/JPA.
- [ ] Teste do módulo passa.
- [ ] Teste de módulos dependentes passa.
- [ ] Reactor completo passa antes de fechar a onda.
- [ ] Documento de rastreabilidade atualizado.

## Observações importantes

- Nem toda regra deve ir para entidade. Regras como cancelamento em cascata, checkout, estorno, comissão, expiração e notificações são casos de uso ou infraestrutura.
- Não resolver regra sistêmica com boolean solto no teste. Booleans são aceitáveis em steps provisórios, mas a entrega final deve preferir portas e consultas explícitas.
- Evitar refatorações cosméticas durante a implementação das regras. Cada onda deve alterar apenas os módulos necessários.
- As divergências documentais devem estar resolvidas na Onda 0.5 antes de implementar cenários e código. Casos já identificados:
  - Avaliação duplicada: rejeitar ou editar avaliação existente?
  - Nome duplicado de evento: bloquear qualquer status ou apenas ativo?
  - Limite por CPF: modelar `Cpf` explicitamente ou assumir `ParticipanteId`?
  - Remoção de notificações/cupons/recompensas: física ou lógica?

## Resultado esperado

Ao final deste plano, a entrega deixa de ser apenas um conjunto de cenários BDD verdes e passa a demonstrar aderência real às regras de negócio: domínio validando invariantes locais, aplicação orquestrando fluxos entre contextos e infraestrutura garantindo transações, locks, persistência, auditoria e jobs.
