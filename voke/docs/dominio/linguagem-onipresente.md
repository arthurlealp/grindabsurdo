# Linguagem Onipresente — Voke

> Glossário compartilhado entre desenvolvedores e stakeholders.
> Cada termo possui um significado preciso dentro do seu Bounded Context.

---

## Bounded Context: Pessoa

Responsável pela identidade dos usuários da plataforma e pelo grafo social entre eles.

| Termo | Definição no contexto |
|---|---|
| **Participante** | Usuário final que consome eventos na plataforma. Possui perfil com dados pessoais, credenciais de acesso e histórico de inscrições. |
| **Organizador** | Usuário com privilégios administrativos para criar e gerenciar eventos. Pode receber repasses financeiros das vendas de ingressos. Deve ter 18 anos ou mais. |
| **Parceiro** | Colaborador vinculado a um Organizador com acesso restrito ao painel, focado na divulgação de eventos. Recebe comissão automática na Carteira Virtual quando um cupom seu é resgatado. Deve ter comparecido a pelo menos 5 eventos do Organizador. |
| **Amizade** | Vínculo bilateral e consensual entre dois Participantes. Só se torna ativa após aceitação explícita das duas partes. Ambos devem ter 16 anos ou mais. |
| **ComunidadeAmigos** | Agrupamento lógico de Participantes conectados por Amizade, criado e administrado por um Participante. Permite o compartilhamento de intenções de presença em eventos. |
| **Card de Intenção** | Sinalização de interesse em um Evento publicada por um Participante dentro de uma ComunidadeAmigos. Funciona como atalho para que os amigos se inscrevam. |
| **Conta** | Perfil de acesso ao sistema. Pode ser do tipo Participante ou Organizador. |
| **Encerramento de Conta** | Desativação permanente do acesso ao sistema. Quando há histórico financeiro, torna-se uma exclusão lógica (soft delete) com anonimização dos dados pessoais para conformidade com a LGPD. |
| **Soft Delete** | Exclusão lógica onde os dados são anonimizados e o acesso é bloqueado, mas os registros financeiros permanecem íntegros por exigência fiscal. |
| **Imutabilidade** | Propriedade de campos que não podem ser alterados após o cadastro (ex.: Data de Nascimento, CPF) para prevenir fraudes de identidade. |
| **CPF Válido** | Documento cujo número passa pela validação do algoritmo matemático oficial, além da verificação de formato. Não basta apenas o formato correto. |
| **Upgrade de Conta** | Migração de uma conta existente de Participante para Organizador, evitando duplicidade de cadastros com o mesmo CPF. |

---

## Bounded Context: Fidelidade

Responsável pela economia interna da plataforma: carteira virtual com moeda real, pontos de engajamento, catálogo de recompensas e recomendações personalizadas.

| Termo | Definição no contexto |
|---|---|
| **CarteiraVirtual** | Conta de créditos pré-pagos do Participante operada em moeda real (R$). Utilizada para pagamento de ingressos e recebimento de estornos ou cashbacks. |
| **Saldo** | Valor disponível na CarteiraVirtual para uso imediato. Nunca pode ser negativo. |
| **Saldo de Consumo** | Créditos recebidos via bonificações promocionais. Podem ser usados apenas para compra de ingressos; saque em espécie é proibido. |
| **Cash-in** | Operação de depósito de valor na CarteiraVirtual via PIX ou cartão de crédito. |
| **Withdraw** | Operação de saque do saldo da CarteiraVirtual de volta para a conta bancária de origem do Participante. |
| **Extrato** | Histórico cronológico de todas as movimentações da CarteiraVirtual: entradas (depósitos, estornos, cashbacks) e saídas (compras, saques, taxas). |
| **ContaPontos** | Carteira de moeda virtual interna da plataforma (pontos) que recompensa o engajamento orgânico do Participante. Operação estritamente separada da CarteiraVirtual. |
| **Ponto** | Unidade de moeda virtual de fidelidade. Concedida exclusivamente após Check-in confirmado em evento encerrado. Não pode ser comprado com dinheiro real. |
| **Expiração de Pontos** | Remoção automática de pontos cujo prazo de validade (12 meses) foi ultrapassado sem utilização. Executada por rotina CRON diária. |
| **Check-in** | Confirmação de presença física do Participante no evento. Gatilho obrigatório para creditação de pontos na ContaPontos. |
| **Recompensa** | Benefício disponível no catálogo de fidelidade criado pelo Organizador (ex.: acesso VIP, produto oficial, desconto exclusivo). Resgatável mediante saldo de pontos suficiente. |
| **Resgate** | Operação atômica de troca de pontos por uma Recompensa. O débito dos pontos só é efetivado se a geração do voucher da Recompensa for bem-sucedida. |
| **Congelamento de Preço** | Regra que impede o Organizador de alterar o custo em pontos de uma Recompensa por 30 dias após a publicação ou última edição do valor. |
| **Sugestão** | Recomendação personalizada de Evento gerada pelo algoritmo da plataforma com base nas preferências, histórico e favoritos do Participante. Máximo de 4 novas sugestões por semana. |
| **Feedback de Sugestão** | Avaliação do Participante sobre uma Sugestão recebida ("Gostei" ou "Não Gostei"). Recalibra dinamicamente as próximas recomendações. |

---

## Bounded Context: Evento

Responsável pelo ciclo de vida dos eventos e por tudo que orbita em torno deles: grupos de comunicação, avaliações, notificações, favoritos e cupons.

| Termo | Definição no contexto |
|---|---|
| **Evento** | Ocorrência presencial com nome único, local físico, data/hora de início e término, capacidade máxima e organizador responsável. Segue um ciclo de vida: Rascunho → Publicado → Em Andamento → Encerrado / Cancelado. |
| **Capacidade** | Número máximo de vagas do espaço físico do Evento. Nenhum Lote pode ter quantidade superior a este valor. |
| **Lote** | Divisão de ingressos de um Evento com preço e quantidade específicos. Apenas um Lote pode estar ativo por vez; o seguinte só é liberado após o esgotamento ou expiração do anterior. |
| **Colisão de Espaço** | Situação em que dois Eventos utilizam o mesmo local físico no mesmo período de tempo. O sistema proíbe a criação ou edição que cause colisão. |
| **Cancelamento em Cascata** | Ao cancelar um Evento com ingressos vendidos, todas as Inscrições ativas são invalidadas e estornos integrais são iniciados automaticamente para as Carteiras Virtuais dos Participantes. |
| **GrupoEvento** | Espaço temporário de comunicação vinculado a um Evento ativo. Acessível exclusivamente a Participantes com inscrição confirmada. Autodestroído após o encerramento do Evento. |
| **Avaliação** | Feedback pós-evento composto por nota (1–5) e comentário qualitativo. Apenas Participantes com inscrição confirmada em evento encerrado podem avaliar. Cada Participante pode enviar apenas uma Avaliação por Evento. |
| **Notificação** | Aviso oficial e unilateral disparado pelo Organizador para todos os inscritos de um Evento ativo. Pode ser editada (e reenviada) ou removida pelo Organizador. |
| **Favorito** | Marcação de interesse de um Participante em um Evento publicado ou ativo. Não implica inscrição. Cada Participante pode favoritar um Evento apenas uma vez. |
| **Cupom** | Código promocional criado pelo Organizador que concede desconto percentual ou fixo na compra de ingressos. Possui validade, limite de resgates e pode ser global (qualquer evento do Organizador) ou específico (um único Evento). |
| **Trava de Edição de Cupom** | Regra que impede a alteração do valor de desconto de um Cupom que já possui resgates registrados. Preserva a consistência do histórico contábil. |

---

## Bounded Context: Inscrição

Responsável pelo fluxo transacional de aquisição de ingressos: da reserva temporária até a confirmação e eventual cancelamento da inscrição.

| Termo | Definição no contexto |
|---|---|
| **Inscrição** | Registro formal da participação de um Participante em um Evento, confirmado após pagamento ou validação gratuita. Possui status: Pendente → Confirmada → Cancelada. |
| **Ingresso** | Comprovante da Inscrição confirmada. Vinculado ao CPF do Participante e ao Lote vigente no momento da compra. |
| **Conflito de Agenda** | Situação em que um Participante tenta confirmar inscrição em dois Eventos sobrepostos no mesmo horário e dia. O sistema bloqueia a segunda inscrição. |
| **Limite por CPF** | Quantidade máxima de ingressos que um mesmo CPF pode adquirir para um Evento específico, definida pelo Organizador. |
| **Carrinho** | Estágio de checkout onde ingressos selecionados são agrupados antes do pagamento. Possui estado temporário e expira após o TTL (Time to Live). |
| **ItemCarrinho** | Entrada individual no Carrinho referenciando um Evento e a quantidade de ingressos desejada. |
| **Reserva Temporária** | Bloqueio de vaga aplicado no banco de dados (ou cache) com TTL de 5 minutos ao adicionar um ingresso ao Carrinho. Libera automaticamente se o pagamento não for confirmado. |
| **TTL (Time to Live)** | Tempo de vida da Reserva Temporária. Expirado o TTL, a vaga retorna ao pool público do Lote. |
| **Política de Cancelamento** | Regra de devolução dinâmica que define o percentual de estorno conforme a proximidade da data do Evento: 100% até 7 dias antes, 50% até 3 dias antes, 0% nas últimas 48 horas. |
| **Virada de Lote** | Momento em que o Lote atual expira (por tempo ou esgotamento) e um novo Lote passa a vigorar. Itens presos no Carrinho devem ter o preço atualizado antes de habilitar o pagamento. |
| **Concorrência de Última Vaga** | Situação em que múltiplos Participantes tentam adquirir a última vaga simultaneamente. O sistema aplica lock (otimista ou pessimista) garantindo a inscrição apenas ao primeiro que concluir a requisição. |
