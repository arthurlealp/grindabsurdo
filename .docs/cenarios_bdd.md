# Cenários BDD — Sistema de Eventos

---

## 1. Gerenciar Participante

```gherkin
Funcionalidade: Gerenciar Participante

  Cenário: Criar conta com dados válidos
    Dado que um usuário não possui conta no sistema
    Quando ele preenche nome, CPF válido, e-mail, data de nascimento e demais dados obrigatórios
    E possui a idade mínima exigida
    Então a conta é criada com sucesso
    E o participante recebe uma confirmação de cadastro

  Cenário: Criar conta com CPF inválido
    Dado que um usuário não possui conta no sistema
    Quando ele preenche os dados com um CPF inválido
    Então o sistema rejeita o cadastro
    E exibe a mensagem "CPF inválido"

  Cenário: Criar conta com e-mail já cadastrado
    Dado que um usuário não possui conta no sistema
    Quando ele preenche os dados com um e-mail já utilizado por outra conta
    Então o sistema rejeita o cadastro
    E exibe a mensagem "E-mail já cadastrado"

  Cenário: Criar conta abaixo da idade mínima
    Dado que um usuário não possui conta no sistema
    Quando ele preenche os dados com uma data de nascimento que não atinge a idade mínima
    Então o sistema rejeita o cadastro
    E exibe a mensagem "Idade mínima não atingida"

  Cenário: Editar dados da conta com sucesso
    Dado que o participante está autenticado no sistema
    Quando ele altera campos permitidos como nome ou e-mail
    Então os dados são atualizados com sucesso
    E o sistema exibe a mensagem "Dados atualizados com sucesso"

  Cenário: Tentar alterar data de nascimento
    Dado que o participante está autenticado no sistema
    Quando ele tenta alterar sua data de nascimento
    Então o sistema bloqueia a alteração
    E exibe a mensagem "Data de nascimento não pode ser alterada"

  Cenário: Remover conta com sucesso
    Dado que o participante está autenticado no sistema
    Quando ele solicita a remoção da sua conta
    Então a conta é removida do sistema
    E o participante não consegue mais fazer login
```

---

## 2. Gerenciar Organizador

```gherkin
Funcionalidade: Gerenciar Organizador

  Cenário: Criar conta de organizador com dados válidos e idade adequada
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele preenche os dados com CPF válido, e-mail único e data de nascimento comprovando 18 anos ou mais
    Então a conta de organizador é criada com sucesso

  Cenário: Criar conta de organizador com menos de 18 anos
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele preenche os dados com uma data de nascimento indicando menos de 18 anos
    Então o sistema rejeita o cadastro
    E exibe a mensagem "Organizador deve ter pelo menos 18 anos"

  Cenário: Criar conta de organizador com CPF inválido
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele preenche os dados com um CPF inválido
    Então o sistema rejeita o cadastro
    E exibe a mensagem "CPF inválido"

  Cenário: Criar conta de organizador com e-mail já cadastrado
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele informa um e-mail que já existe no sistema
    Então o sistema rejeita o cadastro
    E exibe a mensagem "E-mail já cadastrado"

  Cenário: Editar dados da conta do organizador
    Dado que o organizador está autenticado no sistema
    Quando ele altera campos permitidos como nome ou telefone
    Então os dados são atualizados com sucesso

  Cenário: Tentar alterar data de nascimento do organizador
    Dado que o organizador está autenticado no sistema
    Quando ele tenta alterar sua data de nascimento
    Então o sistema bloqueia a alteração
    E exibe a mensagem "Data de nascimento não pode ser alterada"

  Cenário: Remover conta de organizador
    Dado que o organizador está autenticado no sistema
    Quando ele solicita a remoção da sua conta
    Então a conta é removida do sistema
    E o organizador não consegue mais fazer login
```

---

## 3. Gerenciar Evento

```gherkin
Funcionalidade: Gerenciar Evento

  Cenário: Criar evento com dados válidos
    Dado que o organizador está autenticado
    Quando ele preenche nome, local, data, horário, número de vagas, preço e cria um lote
    E não existe outro evento no mesmo local, data e horário
    E não existe outro evento com o mesmo nome
    Então o evento é criado com sucesso

  Cenário: Criar evento com nome duplicado
    Dado que o organizador está autenticado
    Quando ele tenta criar um evento com um nome já existente no sistema
    Então o sistema rejeita a criação
    E exibe a mensagem "Já existe um evento com este nome"

  Cenário: Criar evento com conflito de local, data e horário
    Dado que o organizador está autenticado
    Quando ele tenta criar um evento em um local, data e horário já ocupados por outro evento
    Então o sistema rejeita a criação
    E exibe a mensagem "Já existe um evento neste local, data e horário"

  Cenário: Criar segundo lote enquanto há lote ativo
    Dado que o organizador está autenticado
    E o evento já possui um lote ativo
    Quando ele tenta criar um novo lote para o mesmo evento
    Então o sistema rejeita a criação do lote
    E exibe a mensagem "Só é permitido um lote ativo por vez"

  Cenário: Editar informações do evento com sucesso
    Dado que o organizador está autenticado
    E o evento existe no sistema
    Quando ele edita campos como local, horário ou número de vagas
    Então as alterações são salvas com sucesso

  Cenário: Cancelar evento com cancelamento em cascata
    Dado que o organizador está autenticado
    E o evento possui inscrições e lotes associados
    Quando ele remove o evento
    Então o evento é cancelado
    E todas as inscrições e lotes vinculados são cancelados automaticamente
```

---

## 4. Gerenciar Inscrição

```gherkin
Funcionalidade: Gerenciar Inscrição

  Cenário: Realizar inscrição com sucesso
    Dado que o participante está autenticado
    E o evento está ativo e possui vagas disponíveis
    E o participante atinge a idade mínima do evento
    E não possui outra inscrição no mesmo horário e dia
    Quando ele realiza a inscrição no evento
    Então a inscrição é confirmada com sucesso

  Cenário: Tentar se inscrever abaixo da idade mínima
    Dado que o participante está autenticado
    E o evento exige uma idade mínima maior do que a idade do participante
    Quando ele tenta realizar a inscrição
    Então o sistema rejeita a inscrição
    E exibe a mensagem "Idade mínima não atingida para este evento"

  Cenário: Tentar se inscrever em dois eventos no mesmo horário e dia
    Dado que o participante já possui inscrição confirmada em um evento no dia X às 14h
    Quando ele tenta se inscrever em outro evento no mesmo dia X às 14h
    Então o sistema rejeita a inscrição
    E exibe a mensagem "Você já possui uma inscrição neste horário"

  Cenário: Realizar inscrição na última vaga disponível
    Dado que o evento possui apenas 1 vaga disponível
    E dois participantes tentam se inscrever simultaneamente
    Quando o primeiro participante confirma a inscrição
    Então a inscrição do primeiro é confirmada
    E o sistema informa ao segundo que não há mais vagas

  Cenário: Exceder o número máximo de ingressos por CPF
    Dado que o participante já atingiu o limite máximo de ingressos permitidos por CPF para o evento
    Quando ele tenta realizar mais uma inscrição
    Então o sistema rejeita a inscrição
    E exibe a mensagem "Limite máximo de ingressos por CPF atingido"

  Cenário: Cancelar inscrição com devolução integral
    Dado que o participante está autenticado e possui inscrição confirmada
    E o cancelamento ocorre dentro do prazo para devolução integral
    Quando ele cancela a inscrição
    Então a inscrição é cancelada
    E o valor total é devolvido ao participante

  Cenário: Cancelar inscrição com devolução parcial
    Dado que o participante está autenticado e possui inscrição confirmada
    E o cancelamento ocorre em prazo com devolução parcial conforme política vigente
    Quando ele cancela a inscrição
    Então a inscrição é cancelada
    E apenas o valor proporcional é devolvido ao participante

  Cenário: Cancelar inscrição sem devolução
    Dado que o participante está autenticado e possui inscrição confirmada
    E o cancelamento ocorre fora do prazo de devolução
    Quando ele cancela a inscrição
    Então a inscrição é cancelada
    E nenhum valor é devolvido ao participante
```

---

## 5. Gerenciar Grupos de Evento

```gherkin
Funcionalidade: Gerenciar Grupos de Evento

  Cenário: Organizador cria grupo de evento com sucesso
    Dado que o organizador está autenticado
    E o evento está ativo
    Quando ele cria um grupo para o evento com nome e regras definidas
    Então o grupo é criado com sucesso e vinculado ao evento

  Cenário: Participante inscrito acessa o grupo do evento
    Dado que o participante possui inscrição confirmada no evento
    E o evento possui um grupo ativo
    Quando ele acessa o grupo
    Então o acesso é concedido e ele pode visualizar e postar conteúdos

  Cenário: Participante não inscrito tenta acessar o grupo
    Dado que o participante não possui inscrição confirmada no evento
    Quando ele tenta acessar o grupo do evento
    Então o acesso é negado
    E exibe a mensagem "Inscrição necessária para acessar este grupo"

  Cenário: Menor de idade tenta participar do grupo
    Dado que o participante possui menos de 18 anos
    E o evento possui um grupo ativo
    Quando ele tenta acessar o grupo
    Então o acesso é negado
    E exibe a mensagem "Menores de idade não podem participar do grupo"

  Cenário: Organizador edita regras do grupo
    Dado que o organizador está autenticado
    E o grupo do evento existe
    Quando ele edita as regras do grupo
    Então as novas regras são salvas e exibidas no grupo

  Cenário: Grupo é encerrado automaticamente após o evento
    Dado que o evento foi encerrado
    Quando o sistema processa o encerramento do evento
    Então o grupo vinculado é removido automaticamente

  Cenário: Organizador deleta o grupo manualmente
    Dado que o organizador está autenticado
    E o grupo do evento existe
    Quando ele exclui o grupo
    Então o grupo é removido e os participantes perdem o acesso
```

---

## 6. Gerenciar Avaliação

```gherkin
Funcionalidade: Gerenciar Avaliação

  Cenário: Avaliar evento finalizado com inscrição confirmada
    Dado que o participante possui inscrição confirmada
    E o evento foi finalizado
    Quando ele submete uma avaliação com nota e comentário
    Então a avaliação é registrada com sucesso

  Cenário: Tentar avaliar evento que ainda não foi finalizado
    Dado que o participante possui inscrição confirmada
    E o evento ainda está em andamento ou ativo
    Quando ele tenta submeter uma avaliação
    Então o sistema rejeita a avaliação
    E exibe a mensagem "Só é possível avaliar após o encerramento do evento"

  Cenário: Tentar avaliar um evento sem inscrição confirmada
    Dado que o participante não possui inscrição confirmada no evento
    E o evento foi finalizado
    Quando ele tenta submeter uma avaliação
    Então o sistema rejeita a avaliação
    E exibe a mensagem "Apenas participantes com inscrição confirmada podem avaliar"

  Cenário: Tentar avaliar o mesmo evento duas vezes
    Dado que o participante já submeteu uma avaliação para o evento
    Quando ele tenta submeter uma nova avaliação para o mesmo evento
    Então o sistema rejeita a segunda avaliação
    E exibe a mensagem "Você já avaliou este evento"

  Cenário: Editar avaliação existente
    Dado que o participante já submeteu uma avaliação
    Quando ele edita a nota ou o comentário
    Então a avaliação é atualizada com sucesso

  Cenário: Excluir avaliação existente
    Dado que o participante já submeteu uma avaliação
    Quando ele solicita a exclusão da avaliação
    Então a avaliação é removida do sistema
```

---

## 7. Gerenciar Notificações

```gherkin
Funcionalidade: Gerenciar Notificações

  Cenário: Organizador envia notificação para inscritos em evento ativo
    Dado que o organizador está autenticado
    E o evento está ativo e possui participantes inscritos
    Quando ele cria e envia uma notificação
    Então todos os inscritos recebem a notificação

  Cenário: Tentar enviar notificação para evento cancelado
    Dado que o organizador está autenticado
    E o evento foi cancelado
    Quando ele tenta criar e enviar uma notificação
    Então o sistema rejeita o envio
    E exibe a mensagem "Não é possível enviar notificações para eventos cancelados"

  Cenário: Organizador edita notificação já enviada
    Dado que o organizador está autenticado
    E uma notificação foi enviada anteriormente
    Quando ele edita o conteúdo da notificação
    Então a notificação atualizada é reenviada para os inscritos
    E é exibida com o indicador de "nova" no sistema

  Cenário: Organizador remove notificação enviada
    Dado que o organizador está autenticado
    E uma notificação foi enviada anteriormente
    Quando ele remove a notificação
    Então a notificação é removida do sistema

  Cenário: Ex-inscrito lê notificação de evento cancelado
    Dado que o participante tinha inscrição no evento antes do cancelamento
    E o evento foi cancelado após o envio de notificações
    Quando o ex-inscrito acessa suas notificações
    Então ele consegue visualizar as notificações enviadas antes do cancelamento
```

---

## 8. Gerenciar Favoritos

```gherkin
Funcionalidade: Gerenciar Favoritos

  Cenário: Adicionar evento publicado aos favoritos
    Dado que o participante está autenticado
    E o evento possui status "Publicado"
    Quando ele adiciona o evento à lista de favoritos
    Então o evento aparece na lista de favoritos do participante

  Cenário: Adicionar evento ativo aos favoritos
    Dado que o participante está autenticado
    E o evento possui status "Ativo"
    Quando ele adiciona o evento à lista de favoritos
    Então o evento aparece na lista de favoritos do participante

  Cenário: Tentar adicionar evento com status inválido aos favoritos
    Dado que o participante está autenticado
    E o evento possui status diferente de "Publicado" ou "Ativo" (ex.: Cancelado, Encerrado)
    Quando ele tenta adicionar o evento à lista de favoritos
    Então o sistema rejeita a ação
    E exibe a mensagem "Este evento não pode ser adicionado aos favoritos"

  Cenário: Tentar adicionar o mesmo evento duas vezes aos favoritos
    Dado que o participante já possui o evento na sua lista de favoritos
    Quando ele tenta adicionar o mesmo evento novamente
    Então o sistema rejeita a ação
    E exibe a mensagem "Este evento já está na sua lista de favoritos"

  Cenário: Remover evento dos favoritos
    Dado que o participante possui um evento na lista de favoritos
    Quando ele remove o evento da lista
    Então o evento deixa de aparecer na lista de favoritos
```

---

## 9. Gerenciar Carteira

```gherkin
Funcionalidade: Gerenciar Carteira

  Cenário: Adicionar saldo dentro do limite diário
    Dado que o participante está autenticado
    E o total inserido no dia não atingiu o limite diário
    Quando ele adiciona um valor dentro do limite permitido
    Então o saldo é creditado na carteira com sucesso

  Cenário: Tentar adicionar saldo excedendo o limite diário
    Dado que o participante está autenticado
    E o total inserido no dia já atingiu o limite diário
    Quando ele tenta adicionar mais saldo
    Então o sistema rejeita a operação
    E exibe a mensagem "Limite diário de inserção atingido"

  Cenário: Remover saldo dentro do limite permitido
    Dado que o participante está autenticado
    E o saldo disponível na carteira é suficiente
    E o valor a remover está dentro do limite de remoção
    Quando ele solicita a remoção de saldo
    Então o valor é debitado da carteira com sucesso

  Cenário: Tentar remover saldo excedendo o limite de remoção
    Dado que o participante está autenticado
    Quando ele tenta remover um valor acima do limite de remoção permitido
    Então o sistema rejeita a operação
    E exibe a mensagem "Limite de remoção de saldo atingido"

  Cenário: Tentar remover saldo maior do que o disponível
    Dado que o participante está autenticado
    E o saldo disponível na carteira é insuficiente para o valor solicitado
    Quando ele tenta remover saldo
    Então o sistema rejeita a operação
    E exibe a mensagem "Saldo insuficiente"
```

---

## 10. Gerenciar Amigos e Comunidades de Amigos

```gherkin
Funcionalidade: Gerenciar Amigos e Comunidades de Amigos

  Cenário: Enviar solicitação de amizade
    Dado que o participante está autenticado e possui 16 anos ou mais
    Quando ele envia uma solicitação de amizade para outro participante
    Então a solicitação fica pendente até ser aceita ou recusada

  Cenário: Aceitar solicitação de amizade
    Dado que o participante recebeu uma solicitação de amizade
    Quando ele aceita a solicitação
    Então os dois participantes são vinculados como amigos no sistema

  Cenário: Recusar solicitação de amizade
    Dado que o participante recebeu uma solicitação de amizade
    Quando ele recusa a solicitação
    Então a solicitação é descartada e nenhum vínculo é criado

  Cenário: Menor de 16 anos tenta adicionar amigo
    Dado que o participante possui menos de 16 anos
    Quando ele tenta enviar uma solicitação de amizade
    Então o sistema rejeita a ação
    E exibe a mensagem "Funcionalidade disponível apenas para maiores de 16 anos"

  Cenário: Criar grupo de amigos após amizade confirmada
    Dado que o participante possui pelo menos um amigo confirmado
    Quando ele cria um grupo de amigos e compartilha um evento futuro
    Então o grupo é criado e os amigos visualizam o evento compartilhado

  Cenário: Tentar criar grupo sem amizade confirmada
    Dado que o participante não possui amizades confirmadas
    Quando ele tenta criar um grupo de amigos
    Então o sistema rejeita a ação
    E exibe a mensagem "Confirme uma amizade antes de criar um grupo"

  Cenário: Amigo se inscreve em evento compartilhado com vaga disponível
    Dado que um participante compartilhou um evento com seu grupo de amigos
    E o evento ainda possui vagas disponíveis
    Quando um amigo decide se inscrever no evento pelo grupo
    Então ele é direcionado para o fluxo de inscrição do evento

  Cenário: Amigo tenta se inscrever em evento sem vagas disponíveis
    Dado que um participante compartilhou um evento com seu grupo de amigos
    E o evento não possui mais vagas disponíveis
    Quando um amigo tenta se inscrever
    Então o sistema informa que não há vagas disponíveis

  Cenário: Remover amigo da lista
    Dado que o participante possui amigos confirmados
    Quando ele remove um amigo da sua lista
    Então o vínculo de amizade é desfeito para ambos os lados
```

---

## 11. Gerenciar Parceiros

```gherkin
Funcionalidade: Gerenciar Parceiros

  Cenário: Cadastrar parceiro elegível com sucesso
    Dado que o organizador está autenticado
    E o usuário a ser cadastrado como parceiro participou de pelo menos 5 eventos do organizador
    Quando o organizador cadastra o parceiro e atribui atividades de divulgação
    Então o parceiro é registrado com sucesso

  Cenário: Tentar cadastrar parceiro sem histórico mínimo de eventos
    Dado que o organizador está autenticado
    E o usuário participou de menos de 5 eventos do organizador
    Quando o organizador tenta cadastrar o usuário como parceiro
    Então o sistema rejeita o cadastro
    E exibe a mensagem "O usuário não atingiu o mínimo de 5 eventos para ser parceiro"

  Cenário: Atingir limite de atividades do parceiro
    Dado que o parceiro já possui o número máximo de atividades atribuídas
    Quando o organizador tenta adicionar mais uma atividade ao parceiro
    Então o sistema rejeita a adição
    E exibe a mensagem "Limite de atividades do parceiro atingido"

  Cenário: Participante usa cupom de parceiro e parceiro recebe saldo
    Dado que o participante utilizou um cupom vinculado a um parceiro
    Quando a compra é concluída com sucesso
    Então o parceiro recebe automaticamente um valor em saldo conforme as regras

  Cenário: Organizador edita informações do parceiro
    Dado que o organizador está autenticado
    E o parceiro está cadastrado
    Quando ele edita as informações ou atividades do parceiro
    Então as alterações são salvas com sucesso

  Cenário: Organizador exclui parceiro
    Dado que o organizador está autenticado
    E o parceiro está cadastrado
    Quando ele exclui o parceiro
    Então o parceiro é removido do sistema
```

---

## 12. Gerenciar Cupons

```gherkin
Funcionalidade: Gerenciar Cupons

  Cenário: Organizador cria cupom global com quantidade máxima
    Dado que o organizador está autenticado
    Quando ele cria um cupom global com código, desconto e quantidade máxima de usos
    Então o cupom fica disponível para uso em qualquer evento do organizador

  Cenário: Organizador cria cupom específico para um evento
    Dado que o organizador está autenticado
    E o evento existe no sistema
    Quando ele cria um cupom vinculado especificamente ao evento com código, desconto e quantidade máxima
    Então o cupom fica disponível apenas para aquele evento

  Cenário: Participante utiliza cupom válido pela primeira vez com seu CPF
    Dado que o participante está no fluxo de compra
    E o cupom é válido e não foi usado pelo CPF do participante
    E a quantidade máxima de usos do cupom não foi atingida
    Quando ele aplica o cupom
    Então o desconto é aplicado com sucesso

  Cenário: Participante tenta usar o mesmo cupom duas vezes com o mesmo CPF
    Dado que o participante já utilizou o cupom anteriormente
    Quando ele tenta aplicar o mesmo cupom em uma nova compra
    Então o sistema rejeita o uso
    E exibe a mensagem "Este cupom já foi utilizado pelo seu CPF"

  Cenário: Tentar usar cupom com quantidade máxima de usos esgotada
    Dado que o cupom atingiu a quantidade máxima de usos definida
    Quando qualquer participante tenta aplicar o cupom
    Então o sistema rejeita o uso
    E exibe a mensagem "Este cupom não está mais disponível"

  Cenário: Organizador edita cupom existente
    Dado que o organizador está autenticado
    E o cupom existe no sistema
    Quando ele edita o desconto ou a quantidade máxima de usos
    Então as alterações são salvas com sucesso

  Cenário: Organizador exclui cupom
    Dado que o organizador está autenticado
    E o cupom existe no sistema
    Quando ele exclui o cupom
    Então o cupom é removido e não pode mais ser utilizado
```

---

## 13. Gerenciar Carrinho

```gherkin
Funcionalidade: Gerenciar Carrinho

  Cenário: Adicionar evento ao carrinho com sucesso
    Dado que o participante está autenticado
    E o carrinho possui menos de 2 eventos
    Quando ele adiciona um ingresso de evento ao carrinho
    Então o ingresso é adicionado com sucesso

  Cenário: Tentar adicionar terceiro evento ao carrinho antes de finalizar compra
    Dado que o participante já possui 2 eventos diferentes no carrinho
    Quando ele tenta adicionar um terceiro evento
    Então o sistema rejeita a adição
    E exibe a mensagem "Limite de 2 eventos por carrinho atingido. Finalize a compra atual para continuar"

  Cenário: Realizar pagamento via PIX sem taxa
    Dado que o participante possui itens no carrinho
    Quando ele escolhe PIX como forma de pagamento e finaliza a compra
    Então o valor cobrado é exatamente o valor dos ingressos sem acréscimos

  Cenário: Realizar pagamento via cartão de crédito com taxa
    Dado que o participante possui itens no carrinho
    Quando ele escolhe cartão de crédito como forma de pagamento
    Então o sistema aplica a taxa correspondente ao valor total
    E exibe o valor final com a taxa antes da confirmação

  Cenário: Aplicar cupom válido no carrinho
    Dado que o participante possui itens no carrinho
    E o cupom é válido e não foi utilizado pelo CPF do participante
    Quando ele aplica o cupom no carrinho
    Então o desconto do cupom é refletido no valor total

  Cenário: Tentar aplicar dois cupons no mesmo carrinho
    Dado que o participante já aplicou um cupom no carrinho
    Quando ele tenta aplicar um segundo cupom
    Então o sistema rejeita a ação
    E exibe a mensagem "Apenas um cupom pode ser utilizado por compra"

  Cenário: Tentar aplicar cupom inválido
    Dado que o participante possui itens no carrinho
    Quando ele tenta aplicar um cupom expirado ou inexistente
    Então o sistema rejeita o cupom
    E exibe a mensagem "Cupom inválido ou expirado"

  Cenário: Remover item do carrinho
    Dado que o participante possui itens no carrinho
    Quando ele remove um item
    Então o item é removido e o valor total é recalculado

  Cenário: Carrinho liberado para mais eventos após compra concluída
    Dado que o participante finalizou a compra com 2 eventos no carrinho
    Quando a compra é confirmada
    Então o carrinho é esvaziado e o participante pode adicionar novos eventos
```

---

## 14. Gerenciar Pontos

```gherkin
Funcionalidade: Gerenciar Pontos

  Cenário: Acumular pontos após presença confirmada em evento encerrado
    Dado que o participante compareceu ao evento
    E o evento foi encerrado
    Quando o sistema processa o encerramento
    Então os pontos referentes ao evento são creditados na conta do participante

  Cenário: Não acumular pontos sem presença confirmada
    Dado que o participante tinha inscrição no evento
    E o evento foi encerrado
    Mas a presença do participante não foi confirmada
    Quando o sistema processa o encerramento
    Então nenhum ponto é creditado ao participante

  Cenário: Resgatar bonificação com saldo de pontos suficiente
    Dado que o participante possui saldo de pontos igual ou superior ao valor da bonificação
    Quando ele seleciona e resgata a bonificação
    Então a bonificação é concedida e os pontos são debitados do saldo

  Cenário: Tentar resgatar bonificação com saldo de pontos insuficiente
    Dado que o participante possui saldo de pontos inferior ao valor da bonificação
    Quando ele tenta resgatar a bonificação
    Então o sistema rejeita o resgate
    E exibe a mensagem "Saldo de pontos insuficiente para esta bonificação"

  Cenário: Tentar comprar mais pontos com saldo real
    Dado que o participante está autenticado
    Quando ele tenta comprar pontos utilizando saldo real ou dinheiro
    Então o sistema rejeita a ação
    E exibe a mensagem "Não é permitido adquirir pontos com saldo real"

  Cenário: Pontos expiram após o prazo de validade
    Dado que o participante possui pontos com data de validade vencida
    Quando o sistema processa a expiração
    Então os pontos expirados são removidos do saldo do participante
    E o participante é notificado sobre a expiração
```

---

## 15. Gerenciar Recompensas com Pontos

```gherkin
Funcionalidade: Gerenciar Recompensas com Pontos

  Cenário: Organizador cria recompensa com sucesso
    Dado que o organizador está autenticado
    Quando ele cria uma recompensa com nome, descrição e valor em pontos
    Então a recompensa fica disponível para resgate pelos participantes

  Cenário: Organizador edita recompensa sem resgate em andamento
    Dado que o organizador está autenticado
    E nenhum participante está resgatando a recompensa no momento
    E o prazo mínimo de 1 mês desde a última alteração de valor foi respeitado
    Quando ele edita o valor ou informações da recompensa
    Então as alterações são salvas com sucesso

  Cenário: Organizador tenta editar valor da recompensa antes do prazo mínimo
    Dado que o organizador alterou o valor da recompensa há menos de 1 mês
    Quando ele tenta alterar novamente o valor da recompensa
    Então o sistema rejeita a alteração
    E exibe a mensagem "O valor da recompensa só pode ser alterado após 1 mês da última modificação"

  Cenário: Resgate de recompensa é priorizado durante edição simultânea
    Dado que um participante está resgatando uma recompensa no exato momento
    E o organizador tenta editar ou remover essa recompensa simultaneamente
    Quando o sistema processa as duas operações concorrentes
    Então o resgate do participante é concluído com os valores anteriores
    E a edição ou remoção é aplicada somente após a conclusão do resgate

  Cenário: Organizador remove recompensa sem resgate em andamento
    Dado que o organizador está autenticado
    E nenhum participante está resgatando a recompensa no momento
    Quando ele remove a recompensa
    Então a recompensa é excluída e não aparece mais para os participantes
```

---

## 16. Gerenciar Sugestões do Sistema

```gherkin
Funcionalidade: Gerenciar Sugestões do Sistema

  Cenário: Sistema envia 4 sugestões de eventos na semana baseadas nos interesses do usuário
    Dado que o participante possui interesses cadastrados no sistema
    Quando o sistema processa as sugestões semanais
    Então o participante recebe exatamente 4 sugestões de eventos alinhadas ao seu perfil

  Cenário: Participante avalia sugestão positivamente
    Dado que o participante recebeu uma sugestão de evento
    Quando ele indica que gostou da sugestão
    Então o sistema registra o feedback positivo
    E utiliza essa informação para refinar sugestões futuras

  Cenário: Participante avalia sugestão negativamente e sugestões são reformuladas
    Dado que o participante recebeu uma sugestão de evento
    Quando ele indica que não gostou da sugestão
    Então o sistema registra o feedback negativo
    E reformula as próximas sugestões evitando perfil semelhante ao rejeitado

  Cenário: Administrador adiciona nova sugestão para um usuário
    Dado que o administrador está autenticado no sistema
    Quando ele adiciona uma sugestão de evento para um usuário específico
    Então a sugestão é inserida na fila de sugestões do usuário

  Cenário: Administrador edita sugestão existente
    Dado que uma sugestão foi enviada ao usuário e ainda está pendente de avaliação
    Quando o administrador edita o conteúdo da sugestão
    Então a sugestão atualizada é exibida ao usuário

  Cenário: Administrador remove sugestão existente
    Dado que uma sugestão foi enviada ao usuário e ainda está pendente de avaliação
    Quando o administrador remove a sugestão
    Então a sugestão é excluída e não aparece mais para o usuário
```
