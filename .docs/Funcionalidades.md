### **1\. Gerenciar Participante (Domínio: Pessoa)**

O módulo de gerenciamento de participante é responsável por todo o ciclo de vida do perfil do usuário final (aquele que consome os eventos) na plataforma.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Cadastrar Conta (Sign Up):** O sistema permite que um novo usuário crie um perfil fornecendo dados obrigatórios de identificação, como Nome completo, CPF, Data de Nascimento, E-mail e Senha.  
* **Visualizar Perfil:** O participante pode acessar uma página "Minha Conta" contendo o resumo dos seus dados pessoais e seu status na plataforma.  
* **Editar Dados da Conta:** O usuário pode atualizar informações voláteis e de contato através do painel do perfil. Isso inclui alterar nome social, telefone, endereço, foto de perfil ou atualizar a senha.  
* **Recuperar Senha:** O sistema fornece um fluxo de "Esqueci minha senha", enviando um link temporário para o e-mail cadastrado para a redefinição do acesso.  
* **Remover Conta (Encerramento):** O usuário pode solicitar a exclusão de sua conta e a revogação de seu acesso à plataforma de forma autônoma.

---

#### **Regras de Negócio (RN)**

* **RN01 \- Unicidade de E-mail e CPF:** O sistema deve bloquear o cadastro caso o e-mail informado já esteja registrado na base de dados. A mesma regra de unicidade absoluta se aplica ao CPF. O e-mail será utilizado como credencial principal de login.  
* **RN02 \- Validação Matemática de CPF:** O sistema não deve aceitar apenas o formato de máscara numérica (XXX.XXX.XXX-XX); ele deve obrigatoriamente validar o CPF utilizando o algoritmo oficial para garantir que o documento é real e válido.  
* **RN03 \- Idade Mínima:** Durante o cadastro, o sistema deve calcular a idade com base na data de nascimento fornecida. O participante deve obrigatoriamente possuir a idade mínima estabelecida pelo sistema para concluir a criação da conta. *(Nota: Considerando a regra do seu item 10, essa idade mínima deve ser de pelo menos 16 anos).*  
* **RN04 \- Imutabilidade da Data de Nascimento:** Uma vez que o cadastro é finalizado, o campo "Data de Nascimento" é bloqueado e o usuário não pode alterá-lo no menu de edição de conta. Isso previne fraudes em que o usuário altera a idade para burlar restrições etárias de determinados eventos ou para acessar os grupos da plataforma. Em caso de erro de digitação, a alteração só poderá ser feita mediante contato com o suporte.  
* **RN05 \- Retenção de Dados e Exclusão (Soft Delete / LGPD):** Quando o usuário decide remover sua conta, o sistema deve checar o histórico de inscrições. Se houver histórico financeiro (compras de ingressos passadas), a conta sofre uma exclusão lógica (*soft delete*) e os dados são anonimizados para cumprir a LGPD, mantendo os registros financeiros inalterados por exigência fiscal. O login, no entanto, é permanentemente desativado.  
* **RN06 \- Padrão de Segurança de Senha:** A senha cadastrada ou alterada deve atender a critérios mínimos de força (ex: mínimo de 8 caracteres, contendo letras e números) e nunca deve ser salva em texto plano, utilizando *hash* criptográfico (como *bcrypt*) no banco de dados.

### **2\. Gerenciar Organizador (Domínio: Pessoa)**

Este módulo gerencia o perfil do usuário responsável pela criação e gestão de eventos. O Organizador possui privilégios administrativos sobre seus eventos e acesso a funcionalidades financeiras (como recebimento de valores).

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Cadastrar Perfil de Organizador (Sign Up):** Permite a criação de uma conta específica para quem deseja promover eventos. Além dos dados básicos (Nome, CPF, E-mail, Senha), exige a aceitação dos termos de uso do produtor.  
* **Completar Perfil Profissional:** O organizador pode adicionar informações que serão visíveis em seus eventos, como Minibiografia, Foto/Logotipo e Links para Redes Sociais.  
* **Editar Dados de Contato:** O usuário pode atualizar telefone, endereço comercial e e-mail de suporte para os participantes.  
* **Configurar Dados Bancários:** Funcionalidade para cadastrar a conta bancária ou chave PIX onde o organizador receberá os repasses das vendas dos ingressos.  
* **Remover Conta (Encerramento):** O organizador pode solicitar a exclusão de sua conta, desde que não possua eventos ativos com ingressos vendidos.

---

#### **Regras de Negócio (RN)**

* **RN01 \- Unicidade e Identificação:** Assim como o participante, o e-mail e o CPF devem ser únicos na base. Se um CPF já estiver cadastrado como "Participante", o sistema deve permitir a migração ou upgrade para "Organizador", em vez de criar uma conta duplicada.  
* **RN02 \- Maioridade Obrigatória:** Diferente do participante (16 anos), o organizador deve ter obrigatoriamente **18 anos completos** no momento do cadastro, devido à natureza jurídica e financeira da venda de ingressos.  
* **RN03 \- Validação de Documento:** O CPF deve ser validado pelo algoritmo matemático oficial. Caso o sistema evolua para permitir contas PJ, deve validar o CNPJ via API da Receita Federal.  
* **RN04 \- Trava de Exclusão por Eventos Ativos:** O sistema deve **bloquear a remoção da conta** caso o organizador possua eventos com status "Publicado", "Em Andamento" ou com "Repasses Pendentes". A conta só pode ser encerrada se todos os eventos forem finalizados e os pagamentos liquidados.  
* **RN05 \- Imutabilidade de Dados Sensíveis:** A data de nascimento e o CPF não podem ser editados após a criação da conta para evitar fraudes de identidade.  
* **RN06 \- Verificação de Conta Bancária:** O sistema deve validar se o titular da conta bancária cadastrada possui o mesmo CPF/CNPJ do organizador, evitando desvios de recursos.  
* **RN07 \- Registro de Logs Administrativos:** Toda alteração em dados sensíveis do organizador (como dados bancários ou senha) deve gerar um log de auditoria com IP, data e hora da alteração.

### **3\. Gerenciar Evento (Domínio: Evento)**

Este módulo gerencia o ciclo de vida completo de um evento, desde a sua concepção, definição de regras de lotação e precificação, até a sua publicação ou possível cancelamento.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Criar Evento:** Permite ao organizador cadastrar as informações primárias do evento, incluindo Nome, Descrição, Local físico, Data e Hora exata de início e término.  
* **Configurar Capacidade e Lote Inicial:** Permite definir o limite máximo de vagas do evento e configurar os parâmetros do lote inaugural de ingressos (definindo preço e a carga de ingressos disponibilizada).  
* **Editar Informações do Evento:** O organizador pode atualizar os dados do evento (como alterar a descrição, ajustar horários), desde que as regras de segurança do sistema permitam.  
* **Cancelar/Excluir Evento:** Funcionalidade que permite ao organizador abortar o evento, retirando-o da vitrine e inativando-o no banco de dados.

#### **Regras de Negócio (RN)**

* **RN01 \- Colisão de Espaço Físico e Temporal:** O sistema deve cruzar as informações de local, data e horário (início e fim) durante o cadastro ou edição. É estritamente proibido criar ou sobrepor dois eventos no mesmo local físico no mesmo período de tempo.  
* **RN02 \- Unicidade de Nomenclatura:** Não é permitida a criação de um evento com o mesmo nome de um evento já existente e "Ativo" no sistema, evitando ambiguidades e fraudes visuais para os participantes.  
* **RN03 \- Criação Sequencial de Lotes:** O evento não pode ser criado com múltiplos lotes simultâneos. A regra de negócio exige que apenas **um (1)** lote seja criado e ativado por vez. A transição e criação de um "Lote 2" só deve ser liberada após a expiração de tempo ou esgotamento de vagas do "Lote 1".  
* **RN04 \- Cancelamento em Cascata e Estorno:** A exclusão ou alteração de status para "Cancelado" de um evento que já possui ingressos vendidos é uma operação de alto impacto. Ela deve disparar imediatamente um evento de domínio que invalide todas as inscrições ativas e inicie o estorno integral para a carteira virtual dos participantes lesados.  
    
* **RN05 \- Respeito à Lotação Máxima:** O número de ingressos configurados no lote criado nunca pode ser superior à capacidade máxima de vagas definida para o espaço físico do evento.


4. ### **Gerenciar Inscrição (Domínio: Inscrição)** 

   O módulo de gerenciamento de inscrição é responsável por processar a entrada do participante em um evento, controlando a aquisição de ingressos, a ocupação de vagas e a política de cancelamento.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Realizar Inscrição:** O participante pode selecionar um evento com lote ativo e realizar a sua inscrição, garantindo sua vaga após a confirmação do pagamento ou validação gratuita.  
* **Cancelar Inscrição:** O participante pode acessar seu painel de eventos futuros e cancelar sua inscrição de forma autônoma antes da realização do evento.

#### **Regras de Negócio (RN)**

* **RN01 \- Validação de Idade Mínima:** No momento do checkout, o sistema deve verificar a data de nascimento do perfil do participante e cruzá-la com a classificação indicativa do evento. A inscrição é bloqueada se não atingir a idade mínima.  
* **RN02 \- Conflito de Agenda (Mesmo Horário e Dia):** O sistema deve analisar a carteira de ingressos do usuário e impedir que ele confirme a inscrição caso já possua presença confirmada em outro evento que ocorra no mesmo horário e dia.  
* **RN03 \- Limite de Ingressos por CPF:** O sistema deve validar a quantidade de compras atreladas ao documento do usuário para respeitar o máximo de ingressos por cpf configurado pelo organizador do evento, prevenindo monopolização de vagas.  
* **RN04 \- Gestão de Concorrência (Última Vaga):** O sistema deve tratar a concorrência no banco de dados quando restar apenas a última vaga. Se múltiplos usuários tentarem comprá-la ao mesmo tempo, a aplicação deve aplicar um bloqueio de transação (*lock* otimista ou pessimista), garantindo a inscrição apenas para o primeiro que concluir a requisição e retornando "Lote Esgotado" para os demais.  
* **RN05 \- Política de Cancelamento (Devolução Dinâmica):** Quando o usuário aciona o cancelamento, o cálculo de estorno financeiro deve seguir uma regra de devolução dinâmica de acordo com o dia. O percentual de reembolso deve decair proporcionalmente à proximidade da data do evento (exemplo: 100% de estorno até 7 dias antes, 50% até 3 dias antes e 0% nas últimas 48 horas).

### 

### **5\. Gerenciar Grupos de Evento (Domínio: Evento)**

O módulo de gerenciamento de grupos de evento é responsável por criar um ambiente de engajamento e comunicação (fórum/chat) entre os participantes confirmados de um evento específico, sendo este espaço moderado e administrado pelo organizador.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Criar Grupo:** O organizador pode criar um grupo de comunicação vinculado a um evento ativo de sua autoria.  
* **Configurar e Editar Grupo:** O organizador pode editar detalhes do grupo e estabelecer um conjunto de regras de convivência e moderação que ficarão visíveis para os membros.  
* **Interagir (Participantes):** Os usuários que possuírem acesso ao grupo podem realizar postagens e interagir com os demais participantes.  
* **Deletar Grupo:** O organizador tem a autonomia para deletar o grupo a qualquer momento de forma manual, revogando o acesso de todos os membros e apagando o histórico.

#### **Regras de Negócio (RN)**

* **RN01 \- Restrição Etária (Maioridade):** O sistema deve cruzar a data de nascimento do usuário com os requisitos do grupo. Menores de idade não podem participar ou ter acesso de leitura/escrita no grupo, mesmo que tenham autorização legal para comparecer ao evento físico.  
* **RN02 \- Exclusividade para Inscritos:** O acesso ao grupo funciona por meio de validação de *ticket*. Apenas participantes que concluíram e validaram sua inscrição no evento têm permissão para entrar no grupo. Caso um participante cancele sua inscrição, o sistema deve expulsá-lo automaticamente do grupo.  
* **RN03 \- Ciclo de Vida Vinculado (Autodestruição):** O grupo é um espaço temporário. Uma rotina do sistema (como um *cron job* ou evento de gatilho de banco de dados) deve garantir que, imediatamente após o evento encerrar (data e hora de fim atingidas), o grupo suma e fique inacessível para todos os usuários.  
* **RN04 \- Privilégio de Criação e Moderação:** O grupo só pode ser criado, editado ou excluído pela conta do organizador que é o dono do evento. Participantes não podem criar subgrupos oficiais dentro da plataforma para o mesmo evento.

### **6\. Gerenciar Avaliação (Domínio: Evento)**

Este módulo gerencia o sistema de feedback pós-evento. Ele é fundamental para medir a satisfação do público, construir a reputação dos organizadores e alimentar o algoritmo de relevância da plataforma através do engajamento orgânico dos participantes.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Realizar Avaliação (Submeter):** Permite ao participante atribuir uma pontuação quantitativa (ex: de 1 a 5 estrelas) e registrar um comentário qualitativo descrevendo sua experiência no evento.  
* **Editar Avaliação:** O usuário pode revisar seu feedback, alterando a nota ou reescrevendo o comentário caso deseje atualizar sua opinião.  
* **Excluir Avaliação (Remoção):** Garante ao participante o controle sobre seus dados (autonomia), permitindo que ele apague permanentemente o seu feedback da página do evento.  
* **Consultar Avaliações (Leitura):** Permite que outros visitantes da plataforma leiam os comentários e vejam a média geral de notas para apoiar a decisão de compra em eventos futuros do mesmo organizador. 


#### **Regras de Negócio (RN)**

* **RN01 \- Unicidade de Feedback:** O sistema deve garantir que um participante registre apenas **uma (1)** avaliação por evento. Caso o usuário tente avaliar novamente, o sistema deve interpretar a ação como uma "Edição" (Update) do registro anterior, impedindo o acúmulo de notas repetidas para manipular a média.  
* **RN02 \- Trava de Ciclo de Vida do Evento:** O formulário de avaliação só deve ser desbloqueado e exibido para o usuário caso o status do evento no banco de dados esteja como "Finalizado" (ou "Encerrado") e a inscrição do Participante esteja como “Inscrito”. É bloqueada a avaliação de eventos futuros ou que ainda estejam em andamento.  
* **RN03 \- Imutabilidade Externa (Garantia de Transparência):** O organizador dono do evento tem acesso apenas de leitura (Read) às avaliações de seus eventos. Por questões de lisura, o sistema bloqueia qualquer privilégio de edição ou exclusão de notas e comentários por parte do organizador.

### **7\. Gerenciar Notificações (Domínio: Evento)**

O módulo de gerenciamento de notificações é responsável por permitir a comunicação oficial e unilateral do organizador com os participantes, garantindo que avisos importantes cheguem ao público correto.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Enviar Notificação:** O organizador pode redigir e disparar avisos e atualizações diretamente para a caixa de entrada (ou *push*) de quem estiver inscrito no evento.  
* **Editar Notificação:** O organizador pode editar notificações já enviadas para corrigir erros ou atualizar informações. Como a informação mudou, a aplicação trata isso de forma reativa: logo, ela é reenviada, fica com o símbolo de nova no sistema para garantir que o participante releia a versão corrigida.  
* **Remover Notificação:** O organizador pode remover notificações que foram enviadas, revogando a visualização da mensagem no painel dos participantes.

#### **Regras de Negócio (RN)**

* **RN01 \- Público-Alvo Restrito:** O sistema deve filtrar os destinatários no momento do disparo. As notificações só podem ser enviadas para quem está inscrito no evento, excluindo automaticamente usuários que apenas favoritaram ou que já haviam cancelado a inscrição previamente.  
* **RN02 \- Validação de Status do Evento:** O botão e a rota de envio de novos avisos devem validar o ciclo de vida do evento. O sistema só pode enviar notificações para eventos ativos. Eventos em "rascunho" ou já finalizados/encerrados têm esse recurso bloqueado para novos disparos.  
* **RN03 \- Persistência em Eventos Cancelados (Read-only):** Caso um evento seja cancelado (acionando a regra de cancelamento em cascata da Funcionalidade 3), o banco de dados não deve apagar as mensagens já emitidas. As notificações de eventos cancelados ainda podem ser lidas pelo antigo inscrito, o que é vital para consultar explicações do organizador sobre o motivo do cancelamento ou instruções de estorno financeiro.

### **8\. Gerenciar Favoritos (Domínio: Evento)**

O módulo de gerenciamento de favoritos permite que o participante crie uma lista personalizada de eventos de seu interesse para acompanhamento rápido, auxiliando na decisão de compra futura e no engajamento com a plataforma.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Adicionar aos Favoritos:** O participante pode adicionar eventos na lista de favorito, salvando-os em seu perfil para acesso facilitado.  
* **Excluir dos Favoritos:** O participante pode excluir eventos na lista de favorito a qualquer momento, seja por desinteresse ou após a ocorrência do evento.  
* **Listar Favoritos:** O usuário pode acessar uma página dedicada ("Meus Favoritos") para visualizar o cardápio de eventos que ele curtiu/salvou.

#### **Regras de Negócio (RN)**

* **RN01 \- Unicidade de Favoritamento:** O sistema deve aplicar uma restrição de chave composta no banco de dados (ID\_Participante \+ ID\_Evento) para garantir que um participante não pode adicionar o mesmo evento mais de uma vez à sua lista de favoritos. Na interface de usuário (front-end), o botão deve funcionar como um alternador (*toggle*): se já estiver favoritado, o clique aciona a exclusão.  
* **RN02 \- Validação de Status de Visibilidade:** O botão de favoritar não deve estar disponível para todos os ciclos de vida do evento. Apenas eventos com o status "Publicado", "Ativo" podem ser adicionados aos favoritos. Tentativas via API de favoritar eventos em "Rascunho", "Cancelados" ou "Encerrados" devem retornar erro.  
* **RN03 \- Atualização Reativa de Status:** Se um evento que já está na lista de favoritos de um usuário mudar de status (por exemplo, for Cancelado pelo organizador ou for Encerrado por ter passado da data), ele pode permanecer na lista para visualização de histórico do participante, mas deverá receber uma sinalização visual (uma etiqueta de "Esgotado", "Cancelado" ou "Encerrado") e o link de compra/inscrição deve ser desativado. 

### **9\. Gerenciar Carteira Virtual (Domínio: Pessoa)**

Este módulo é responsável por gerenciar o ciclo de vida financeiro dos créditos do participante dentro da plataforma. A carteira funciona como um sistema de saldo pré-pago, permitindo que o usuário tenha agilidade no checkout dos eventos e segurança no recebimento de estornos ou cashbacks.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Adicionar Saldo (Cash-in):** Permite ao participante inserir créditos na sua conta através de métodos de pagamento integrados (como PIX ou Cartão de Crédito). O saldo fica disponível imediatamente após a confirmação do processador de pagamento.  
* **Remover Saldo (Withdraw/Estorno):** O usuário pode solicitar a retirada de valores que ele mesmo inseriu na plataforma, enviando o dinheiro de volta para sua conta bancária de origem ou chave PIX cadastrada.  
* **Consultar Extrato Financeiro:** Funcionalidade de leitura onde o usuário acompanha todas as movimentações: entradas (depósitos, cashbacks, estornos) e saídas (compras de ingressos, taxas e retiradas).

#### **Regras de Negócio (RN)**

* **RN01 \- Limite de Inserção Diária:** Para prevenir fraudes e lavagem de dinheiro, o sistema impõe um teto máximo para depósitos em um período de 24 horas. Tentativas de recarga que ultrapassem esse valor devem ser bloqueadas e sinalizadas para auditoria.  
* **RN02 \- Limite de Retirada (Saque):** O usuário possui um limite de valor e de frequência para a remoção de saldo (ex: uma retirada por dia ou valor máximo de R$ 500,00). Isso garante a liquidez do sistema e reduz custos operacionais de transferências bancárias.  
* **RN03 \- Saldo Bloqueado para Retirada:** Valores recebidos via  bonificações promocionais podem ser marcados como "Saldo de Consumo", podendo ser usados apenas para compra de ingressos, sendo proibida a sua retirada em espécie.  
* **RN04 \- Orquestração de Atomicidade:** Toda operação de débito para compra de ingresso deve ser atômica. O saldo só é efetivamente descontado se a reserva da vaga e a geração do ingresso forem confirmadas. Em caso de falha sistêmica, o sistema garante o rollback imediato do valor para a carteira.  
* **RN05 \- Taxas de Conveniência por Método:** O sistema deve aplicar regras distintas de custo: depósitos via Cartão de Crédito podem sofrer a incidência de taxas administrativas de operadoras, enquanto depósitos via PIX são isentos de taxas de inserção.  
* **RN06 \- Trava de Saldo Negativo:** Sob nenhuma circunstância o saldo da carteira virtual pode ficar negativo. O sistema deve realizar uma verificação de "Pre-auth" (pré-autorização) antes de qualquer tentativa de compra de ingresso.

### **10\. Gerenciar Amigos e Comunidades (Domínio: Pessoa)**

Este módulo introduz uma camada social à plataforma, focada na retenção de usuários e no planejamento coletivo de eventos. Ele permite que os participantes formem redes de contatos para compartilhar intenções de participação, funcionando como um catalisador orgânico de vendas, sem a complexidade de um aplicativo de mensagens.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Solicitar e Gerenciar Amizades (Conexão):** Permite buscar outros usuários cadastrados na plataforma, enviar convites de conexão, além de aprovar, recusar ou desfazer amizades ativas.  
* **Gerenciar Grupos/Comunidades (CRUD de Grupo):** Permite ao participante criar agrupamentos lógicos de amigos (ex: "Turma do Trabalho"), adicionar membros, remover participantes do grupo e editar o nome ou propósito da comunidade.  
* **Compartilhar Intenção de Presença (Read/Share):** O usuário pode sinalizar em um grupo que confirmou presença ou tem interesse em um evento específico. O sistema gera um "Card" do evento no grupo, servindo como um atalho direto para que os amigos se inscrevam.

#### **Regras de Negócio (RN)**

* **RN01 \- Consentimento Mútuo:** A formação do vínculo de amizade exige aprovação das duas partes. O status da relação só muda para "Ativo" e permite inclusão em grupos quando o usuário A envia a requisição e o usuário B a aceita explicitamente.  
* **RN02 \- Classificação Indicativa e Proteção (Idade Mínima):** Em conformidade com as regras de proteção e privacidade de interações sociais na plataforma, a aba de "Comunidades e Amigos" é estritamente bloqueada para usuários. O sistema deve validar que ambos os usuários envolvidos na conexão tenham 16 anos ou mais.  
* **RN03 \- Pré-requisito de Vínculo para Grupos:** A estrutura do banco de dados deve impedir a inclusão de estranhos em comunidades. Um participante só pode ser adicionado a um grupo se já possuir uma amizade confirmada (status "Ativo") com o criador/administrador daquele grupo.  
* **RN04 \- Modelo de Interação Restrita (Ausência de Chat):** O compartilhamento dentro da comunidade limita-se à emissão de "Cards de Intenção de Compra/Presença". Não há troca de mensagens de texto livre (chat). Essa regra elimina a necessidade de moderação de conteúdo, denúncias de assédio e infrações severas da LGPD relacionadas a conversas privadas.  
* **RN05 \- Validação Dinâmica de Disponibilidade no Card:** Quando um participante clica no botão "Também quero ir" no card compartilhado pelo amigo no grupo, o sistema deve fazer uma validação em tempo real no Domínio de Eventos. Se o evento estiver esgotado no momento do clique, o sistema deve impedir a compra e alterar o status do card no grupo para "Esgotado" de forma síncrona.  
* **RN06 \- Exclusão em Cascata do Grupo:** Se a relação de amizade for rompida (remoção de amigo) por qualquer uma das partes, o sistema deve disparar uma rotina automática que remove aquele participante de todos os grupos em que ambos estavam conectados através dessa amizade.

### **11\. Gerenciar Parceiros (Domínio: Pessoa)**

O módulo de gerenciamento de parceiros (também conhecidos como *promoters* ou afiliados) permite que um Organizador principal crie contas de acesso restrito para colaboradores focados na divulgação de eventos, gerenciando suas permissões operacionais e comissionamentos.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Cadastrar Parceiro:** O organizador pode cadastrar e atribuir atividades parceiros para divulgação, criando um subperfil vinculado ao seu painel principal.  
* **Editar Parceiro:** O organizador pode editar os parceiros e suas atividades, ajustando o escopo do que esse afiliado pode ou não realizar na plataforma, além de atualizar informações de contato.  
* **Excluir Parceiro:** O organizador pode excluir parceiros, revogando permanentemente o acesso desse usuário ao painel de parceiros e inativando seus links de divulgação.

#### **Regras de Negócio (RN)**

* **RN01 \- Pré-requisito de Fidelidade:** O sistema deve validar o histórico do usuário antes de permitir seu vínculo como afiliado. Para ser cadastrado, o parceiro tem que ter ido para pelo menos 5 eventos o organizador (presença validada, não apenas a compra do ingresso).  
* **RN02 \- Controle de Acesso Baseado em Funções (RBAC):** O parceiro atua como um organizador mais fraco. O sistema deve garantir que ele tem limitação de atividades. O parceiro não pode, por exemplo, criar novos eventos, deletar grupos, alterar a capacidade do local ou manipular valores de repasse financeiro. Suas ações devem ser restritas à geração de cupons vinculados a ele, visualização de conversões e interação de marketing.  
* **RN03 \- Comissionamento Sistêmico:** O sistema deve aplicar um gatilho automático de recompensa no checkout. Se usar o cupom de um parceiro ele ganha um valor em saldo. Esse valor de comissão deve ser creditado imediatamente na carteira digital (Funcionalidade 9\) do parceiro assim que o pagamento do ingresso pelo participante for confirmado pela processadora. Caso a compra seja cancelada, o sistema deve estornar esse saldo da carteira do parceiro.

	Regras: Parceiro tem que ter ido para pelo menos 5 eventos o organizador, tem limitação de atividades, se usar o cupom de um parceiro ele ganha um valor em saldo

### **12\. Gerenciar Cupons (Domínio: Evento)**

O módulo de gerenciamento de cupons permite ao organizador criar estratégias promocionais e de marketing, oferecendo descontos para impulsionar a conversão e a venda de ingressos.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Adicionar Cupom:** O organizador pode adicionar cupons de desconto, definindo o código promocional, o tipo de desconto (percentual ou valor fixo), a validade e as restrições de uso.  
* **Editar Cupom:** O organizador pode editar o cupom de desconto para ajustar prazos, modificar limites de resgate ou corrigir a nomenclatura do código promocional.  
* **Excluir Cupom:** O organizador pode Excluir o cupom de desconto, invalidando imediatamente o código e impedindo que ele seja aplicado em novas compras.

#### **Regras de Negócio (RN)**

* **RN01 \- Unicidade por Participante:** O sistema deve garantir que o uso do cupom é único por CPF. Ao aprovar um pagamento, o banco de dados deve registrar o uso atrelado ao documento do comprador, bloqueando tentativas futuras de reutilizar o mesmo código.  
* **RN02 \- Validação de Limites e Ciclo de Vida:** Durante o processamento do carrinho, o sistema deve checar se o código inserido é um cupom válido (ou seja, se a data atual está dentro do período de vigência) e garantir que a aplicação respeite a quantidade máxima de uso definida na criação (ex: limitado aos 50 primeiros resgates).  
* **RN03 \- Escopo de Aplicação (Contexto):** A modelagem de dados deve suportar diferentes níveis de abrangência, garantindo que podem existir cupons globais ou para o evento especificamente. Um cupom global concede desconto em qualquer evento ativo daquele organizador, enquanto um cupom específico tem o seu resgate restrito apenas aos ingressos do evento para o qual foi gerado.  
* **RN04 \- Regra de Valor Mínimo e Abate Zero:** Cupons de valor fixo (ex: R$ 50,00) não podem gerar saldo negativo, "troco" em dinheiro ou saldo na carteira (Funcionalidade 9\) para o participante. Se aplicado a um carrinho de R$ 40,00, o total a pagar passa a ser R$ 0,00 e os R$ 10,00 residuais do cupom são ignorados e perdidos.  
* **RN05 \- Trava de Edição em Cupons Ativos:** O organizador não pode alterar o valor do desconto de um cupom que já possui resgates registrados no sistema. Caso precise mudar o valor, ele deve "Excluir/Inativar" o cupom atual e criar um novo, garantindo a consistência do histórico contábil e auditoria de compras passadas. 

### **13\. Gerenciar Carrinho (Domínio: Inscrição)**

O módulo de gerenciamento de carrinho é o estágio de *checkout* transacional, responsável por agrupar os ingressos desejados, calcular taxas, aplicar benefícios e garantir a reserva de vagas antes da consolidação do pagamento.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Adicionar ao Carrinho:** O participante pode selecionar ingressos de eventos disponíveis e adicionar no carrinho para iniciar o fluxo de pagamento.  
* **Editar o Carrinho:** O participante pode editar no carrinho, ajustando a quantidade de ingressos selecionados para um determinado evento (desde que não fira o limite máximo por CPF estipulado na Funcionalidade 4).  
* **Remover do Carrinho:** O participante pode remover do carrinho ingressos que não deseja mais adquirir antes de finalizar a transação.

#### **Regras de Negócio (RN)**

* **RN01 \- Precificação Dinâmica por Método de Pagamento:** O motor de *checkout* deve recalcular o subtotal em tempo real baseado na escolha do usuário. O sistema estabelece que pagamentos com cartão de crédito sofrem taxa de processamento da adquirente/gateway, valor que deve ser discriminado no resumo da compra. Por outro lado, o pagamento com pix não tem taxa repassada ao comprador.  
* **RN02 \- Trava de Multiplexação de Eventos (Anti-Hoarding):** Para evitar que um único usuário retenha o estoque de múltiplos organizadores ao mesmo tempo sem intenção de compra, o carrinho tem uma quantidade máxima para 2 eventos nele. O sistema monitora as sessões ativas e só após a compra é liberado para mais inserções.  
* **RN03 \- Validação Estrita e Isolamento de Benefícios:** A API de pagamentos deve realizar uma checagem dupla garantindo que só pode ser usado cupons válidos (dentro do prazo de vigência e com limite de resgate disponível). Para evitar sobreposição de prejuízos ao organizador, o sistema impõe que o comprador só pode usar 1 cupom por transação de carrinho, barrando o acúmulo de descontos.  
* **RN04 \- Reserva Temporária de Estoque (TTL \- *Time to Live*):** Ao colocar um ingresso no carrinho, o sistema aplica um *lock* temporário na vaga no banco de dados ou via *cache* (ex: usando Redis) com um cronômetro (ex: 5 minutos). Se o pagamento não for consolidado nesse tempo, o carrinho expira e a vaga retorna ao *pool* público do lote.  
* **RN05 \- Invalidação por Virada de Lote:** Se um item estiver "preso" no carrinho e o lote daquele evento virar no sistema (por tempo ou por limite de vagas globais), o sistema não deve honrar o preço antigo no *checkout*. O usuário deve receber um aviso informando que o lote foi alterado, forçando uma atualização do carrinho para o novo preço vigente antes de habilitar o botão de pagamento. 

### 

### **14\. Gerenciar Pontos e Fidelidade (Domínio: Pessoa)**

Este módulo é o motor financeiro secundário do sistema, focado inteiramente em retenção e fidelização (Gamificação). Diferente da carteira virtual (que opera moeda real), este módulo gerencia uma moeda de troca interna (pontos) que recompensa o engajamento genuíno do participante com a plataforma.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Acumular Pontos (Create/Crédito):** O usuário ganha a injeção automática de créditos de fidelidade em sua conta como recompensa por sua participação ativa.  
* **Consultar Saldo e Extrato (Read):** Permite ao participante visualizar seu montante total de pontos válidos, seu histórico de ganhos/gastos e alertas sobre pontos que estão próximos do vencimento.  
* **Resgatar Bonificações (Update/Débito):** O participante utiliza seu saldo de pontos para "comprar" benefícios do catálogo de recompensas criado pelos organizadores.  
* **Expirar Pontos (Delete Lógico):** O sistema remove automaticamente do saldo do participante os pontos que ultrapassaram seu tempo de vida útil sem serem utilizados.

#### **Regras de Negócio (RN)**

* **RN01 \- Gatilho de Validação Dupla (Prova de Presença):** O crédito de pontos não ocorre no ato da compra do ingresso. O sistema exige uma dupla condição obrigatória para creditar os pontos: O status do evento deve constar como "Encerrado" E a inscrição do usuário deve constar com status de "Check-in Realizado" (presença confirmada na portaria).  
* **RN02 \- Isolamento Fiduciário (Proibição de Compra Direta):** O sistema impõe uma barreira estrita entre a Carteira Virtual (Dinheiro) e a Carteira de Pontos. É terminantemente proibida a conversão de saldo real (via PIX/Cartão) para a aquisição de pontos. Os pontos são exclusivamente fruto de engajamento orgânico.  
* **RN03 \- Validação de Débito Restrito:** Para que o participante consiga resgatar uma bonificação, o banco de dados deve validar se o Saldo de Pontos Atual é maior ou igual ao Custo da Bonificação. Tentativas de resgate sem saldo suficiente devem ser sumariamente rejeitadas, bloqueando o botão de ação.  
* **RN04 \- Ciclo de Vida e Expiração (TTL de Pontos):** Os pontos possuem um Prazo de Validade (ex: 12 meses após a data de aquisição). O sistema deve rodar uma rotina em background (CRON Job) que varre o banco de dados diariamente e realiza o débito (vencimento) dos pontos expirados, atualizando o saldo do usuário para refletir a perda.  
* **RN05 \- Atomicidade do Resgate de Pontos:** Seguindo o mesmo princípio de segurança da Inscrição, o ato de trocar pontos por uma bonificação deve ser uma transação atômica. O abatimento dos pontos do usuário só é efetivado (commit) se a geração do voucher da bonificação for bem-sucedida, prevenindo qualquer perda injusta de saldo de fidelidade por falhas de sistema.

### **15\. Gerenciar Recompensas com Pontos (Domínio: Pessoa)**

Este módulo fornece ao organizador uma ferramenta de fidelização e engajamento. Ele permite a criação de um catálogo de benefícios (como acessos VIP, produtos oficiais ou descontos exclusivos) que os participantes podem "comprar" resgatando os pontos/cashback acumulados na plataforma.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Cadastrar Recompensa (Create):** O organizador cria um novo benefício, definindo título, descrição, estoque disponível e o "preço" exigido em pontos para o resgate.  
* **Consultar Catálogo (Read):** Permite listar as recompensas ativas, monitorar quantas unidades já foram resgatadas e ver o estoque restante.  
* **Editar Recompensa (Update):** O organizador pode alterar informações visuais da recompensa ou atualizar o seu valor em pontos (sujeito a regras de congelamento temporal).  
* **Remover/Inativar Recompensa (Delete):** Retira a recompensa da vitrine dos usuários, impedindo novos resgates.

#### **Regras de Negócio (RN)**

* **RN01 \- Concorrência e Prioridade de Resgate (Locking):** Em cenários de concorrência, o direito do consumidor prevalece. Se um usuário inicia o processo de resgate com seus pontos no exato milissegundo em que o organizador tenta editar ou excluir a recompensa, o banco de dados deve utilizar bloqueio de registro (Lock) priorizando a transação do usuário. A edição do organizador falhará temporariamente ou será aplicada apenas ao inventário restante.  
* **RN02 \- Período de Congelamento de Preço (Time-Lock de 1 Mês):** Para proteger o participante contra inflação artificial e garantir previsibilidade na meta de acúmulo de pontos, o valor em pontos de uma recompensa sofre um bloqueio temporal. Após a publicação ou última edição do valor, o organizador é travado pelo sistema e fica proibido de alterar o "preço" daquela recompensa por um período mínimo de 30 dias.  
* **RN03 \- Controle Estrito de Estoque:** Toda recompensa deve possuir uma quantidade máxima configurada. A cada resgate confirmado, o sistema deduz uma unidade. Quando o estoque atinge zero, a recompensa tem seu status alterado automaticamente para "Esgotado", impossibilitando tentativas de resgate em abas fantasmas.  
* **RN04 \- Exclusão Lógica e Histórico (Soft Delete):** Uma recompensa que já tenha sido resgatada por pelo menos um participante não pode sofrer deleção física (Hard Delete) do banco de dados, para garantir a integridade do extrato do usuário. A exclusão solicitada pelo organizador será tratada como Inativação, removendo o item da vitrine de ofertas, mas mantendo-o visível no histórico de quem já o adquiriu.  
* **RN05 \- Validação Atômica de Saldo de Pontos:** Assim como na carteira virtual, o resgate exige uma transação atômica. A recompensa só tem sua baixa de estoque confirmada se a dedução dos pontos no perfil do participante ocorrer com sucesso, impedindo que os usuários fiquem com saldo de pontos "negativo".

### **16\. Gerenciar Sugestões do Sistema (Domínio: Pessoa)**

Este módulo atua como o motor de recomendação inteligente da plataforma. O objetivo é aumentar a retenção do usuário e impulsionar a venda de ingressos conectando o participante a eventos altamente relevantes, criando um ciclo de melhoria contínua através do feedback direto.

#### **O que a funcionalidade faz (Ações do Usuário)**

* **Configurar Interesses Base (Create):** Permite ao participante cadastrar inicialmente suas preferências e categorias favoritas (ex: Tecnologia, Música, Esportes) para alimentar o sistema.  
* **Visualizar Feed de Sugestões (Read):** O usuário acessa uma vitrine personalizada contendo os eventos que o sistema selecionou ativamente para ele.  
* **Avaliar Sugestão (Update / Feedback):** O participante interage com o evento sugerido informando explicitamente se achou a recomendação relevante ("Gostei") ou irrelevante ("Não Gostei").  
* **Remover/Descartar Sugestão (Delete):** Permite que o usuário oculte permanentemente um card de sugestão da sua tela caso não tenha interesse em visualizar aquele evento novamente.

#### **Regras de Negócio (RN)**

* **RN01 \- Motor Direcionado por Interesses (Match):** A geração de sugestões não é aleatória. O sistema deve obrigatoriamente realizar um cruzamento (match) entre as tags do evento e as preferências cadastradas pelo usuário, somadas ao seu histórico de eventos anteriores e lista de favoritos.  
* **RN02 \- Recalibragem por Feedback Negativo (Reformulação):** O feedback do usuário é um gatilho de atualização do algoritmo. Caso o participante marque que "Não Gostou" de uma sugestão, o sistema deve registrar esse peso negativo, ocultar o evento e reformular dinamicamente a fila de próximas recomendações, evitando sugerir imediatamente eventos daquela mesma subcategoria ou perfil.  
* **RN03 \- Teto de Frequência (Controle de Carga Semanal):** Para evitar fadiga visual, ansiedade de escolha e comportamento de "spam", o sistema possui um limite rígido de entrega. O motor de recomendação está travado para exibir, no máximo, **4 novas sugestões de eventos por semana** para um mesmo usuário.  
* **RN04 \- Exclusão Lógica de Redundâncias:** O algoritmo de sugestões possui filtros excludentes. Sob nenhuma circunstância o sistema pode sugerir um evento no qual o usuário já possua uma inscrição ativa, que já esteja na sua Fila de Espera, ou que já esteja com status de "Encerrado" ou "Lotação Máxima".  
* **RN05 \- Automação de Limpeza:** Se o usuário ignorar as sugestões da semana e não interagir com elas (nem aprovar, nem rejeitar, nem comprar), as sugestões devem expirar ao final do ciclo semanal, abrindo espaço para 4 recomendações novas e frescas na semana seguinte.

