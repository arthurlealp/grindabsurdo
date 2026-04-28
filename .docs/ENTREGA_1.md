# 📦 Entrega 1 — Requisitos, Artefatos e Guia de Execução

> Foco exclusivo na **1ª Entrega** do trabalho em grupo.
> Para regras gerais de arquitetura, convenções e boas práticas, consulte `AGENT_GUIDE.md`.

---

## 🎯 O que deve ser entregue

A 1ª entrega é composta pelos seguintes artefatos, **todos versionados no GitHub**:

| # | Artefato | Ferramenta/Formato |
|---|----------|--------------------|
| 1 | Descrição do domínio com linguagem onipresente | Markdown (`.md`) |
| 2 | Mapa de histórias do usuário | Miro / Figma / imagem |
| 3 | Protótipos (baixa ou alta fidelidade) | Figma / Balsamiq / imagem |
| 4 | Modelo(s) do(s) subdomínio(s) com Context Mapper | Arquivo `.cml` |
| 5 | Cenários de teste BDD | Arquivos `.feature` (Gherkin) |
| 6 | Automação dos cenários BDD com Cucumber | Código Java (Step Definitions + Runners) |

Além disso, o código deve adotar:
- DDD em **todos os seus níveis** (preliminar, estratégico, tático, operacional)
- **Arquitetura Limpa (Clean Architecture)**

---

## 1️⃣ Descrição do Domínio — Linguagem Onipresente

### O que é
Um documento que define, em português e no contexto do negócio, **todos os termos relevantes** do sistema. Serve como dicionário compartilhado entre desenvolvedores e stakeholders. Cada termo deve ter um significado preciso dentro do seu Bounded Context.

### O que o agente deve produzir
Um arquivo `docs/dominio/linguagem-onipresente.md` com:

```markdown
# Linguagem Onipresente — [Nome do Sistema]

## Bounded Context: [Nome do Contexto]

| Termo           | Definição no contexto                                               |
|-----------------|----------------------------------------------------------------------|
| [Termo]         | [Definição precisa de como este termo é entendido neste contexto]   |
| ...             | ...                                                                  |

## Bounded Context: [Outro Contexto]
...
```

### Boas práticas
- Um mesmo termo pode ter significados diferentes em contextos distintos — isso é esperado e deve ser explicitado
- Evite jargões técnicos; use a linguagem do negócio
- Todo elemento tático (entidade, VO, serviço) deve ter um termo correspondente no glossário

---

## 2️⃣ Mapa de Histórias do Usuário

### O que é
Uma representação visual organizada das funcionalidades do sistema do ponto de vista dos usuários, agrupadas por atividades e tarefas.

### Estrutura esperada
```
Atividade Principal
├── Tarefa 1
│   ├── História de usuário 1.1
│   └── História de usuário 1.2
├── Tarefa 2
│   └── História de usuário 2.1
└── ...
```

### Formato das histórias
```
Como [tipo de usuário],
Quero [ação/funcionalidade],
Para que [benefício/objetivo].
```

### O que o agente deve produzir
- Link para ferramenta visual (Miro, FigJam) **e/ou** imagem exportada em `docs/historias/`
- Arquivo `docs/historias/historias.md` listando todas as histórias no formato textual

### Regra de escopo
O sistema deve ter entre **12 e 16 funcionalidades não triviais** (2 por integrante da equipe). Funcionalidades **não triviais** são aquelas que:
- Não são apenas leitura/listagem simples
- Possuem regras de negócio de complexidade média ou alta
- Envolvem múltiplas entidades ou validações

---

## 3️⃣ Protótipos

### O que é
Representação visual das telas do sistema, podendo ser de baixa ou alta fidelidade.

### Baixa fidelidade (mínimo aceitável)
- Wireframes em preto e branco
- Ferramentas: Balsamiq, papel digitalizado, Figma (modo wireframe)

### Alta fidelidade (recomendado)
- Telas com identidade visual, cores, componentes reais
- Ferramenta: Figma

### O que o agente deve produzir
- Arquivo `docs/prototipos/README.md` com links para as telas e descrição de cada fluxo
- Imagens exportadas salvas em `docs/prototipos/imagens/`
- Cada funcionalidade não trivial deve ter ao menos uma tela representada

---

## 4️⃣ Modelo de Subdomínios com Context Mapper (`.cml`)

### O que é
O arquivo `.cml` (Context Mapper Language) é uma representação formal dos Bounded Contexts, seus agregados e as relações entre eles.

### Onde fica
Na raiz do repositório: `[nome-projeto].cml`

### Estrutura mínima esperada

```cml
ContextMap [NomeSistema]Map {
    type = SYSTEM_LANDSCAPE
    state = TO_BE

    contains [ContextoA], [ContextoB], [ContextoC]

    [ContextoA] -> [ContextoB] : Customer-Supplier

    [ContextoB] <-> [ContextoC] : Shared-Kernel
}

BoundedContext [ContextoA] implements [SubdominioA] {
    type = FEATURE
    domainVisionStatement = "Responsável por..."

    Aggregate [Agregado] {
        Entity [Entidade] {
            aggregateRoot
            - [TipoId] id
            String campo1
        }
        ValueObject [TipoId] {
            Long id
        }
    }
}
```

### Tipos de relação entre contextos (usar corretamente)
- `Customer-Supplier` — um contexto depende do outro
- `Shared-Kernel` — compartilham parte do modelo
- `Conformist` — um se conforma com o modelo do outro
- `Anti-Corruption-Layer (ACL)` — isola o contexto de modelos externos

### O que o agente deve garantir
- Todos os Bounded Contexts identificados estão no `.cml`
- Cada contexto tem ao menos um Aggregate com um Aggregate Root
- As relações entre contextos são explícitas
- O arquivo compila sem erros na extensão Context Mapper do VS Code

---

## 5️⃣ Cenários de Teste BDD (Arquivos `.feature`)

### Onde ficam
```
[modulo-de-testes ou apresentacao-backend]/
└── src/
    └── test/
        └── resources/
            └── features/
                ├── [funcionalidade-1].feature
                ├── [funcionalidade-2].feature
                └── ...
```

### Regra obrigatória: mínimo de 3 cenários por funcionalidade

Cada arquivo `.feature` deve cobrir **pelo menos 3 cenários distintos**:

| Tipo de cenário | Descrição |
|----------------|-----------|
| ✅ Cenário feliz | O fluxo principal funciona corretamente |
| ❌ Violação de regra | Uma regra de negócio é violada (ex.: valor negativo, data inválida) |
| ⚠️ Caso de borda | Recurso inexistente, limite atingido, duplicidade, etc. |
| ➕ Caso adicional | Outro caminho alternativo relevante (recomendado) |

### Template de arquivo `.feature`

```gherkin
# language: pt
Funcionalidade: [Nome da Funcionalidade em português]
  Como [tipo de usuário]
  Quero [ação]
  Para que [objetivo]

  Cenário: [Nome do cenário feliz]
    Dado [contexto inicial]
    Quando [ação realizada]
    Então [resultado esperado]

  Cenário: [Nome do cenário de validação]
    Dado [contexto com dado inválido]
    Quando [ação realizada]
    Então [erro esperado é lançado/retornado]

  Cenário: [Nome do caso de borda]
    Dado [contexto de borda]
    Quando [ação realizada]
    Então [comportamento esperado no limite]

  Cenário: [Cenário adicional relevante]
    Dado ...
    Quando ...
    Então ...
```

### Exemplo real

```gherkin
# language: pt
Funcionalidade: Realizar inscrição em evento

  Cenário: Inscrição realizada com sucesso
    Dado que existe um evento com vagas disponíveis
    E existe um participante cadastrado
    Quando o participante solicita inscrição no evento
    Então a inscrição é registrada com status "CONFIRMADA"
    E o número de vagas disponíveis é reduzido em 1

  Cenário: Inscrição negada por falta de vagas
    Dado que existe um evento sem vagas disponíveis
    E existe um participante cadastrado
    Quando o participante solicita inscrição no evento
    Então a inscrição é negada
    E o participante é adicionado à lista de espera

  Cenário: Inscrição negada por conflito de horário
    Dado que o participante já está inscrito em um evento no mesmo horário
    Quando o participante solicita inscrição em outro evento no mesmo horário
    Então a inscrição é recusada com a mensagem "Conflito de horário detectado"

  Cenário: Inscrição negada para evento cancelado
    Dado que existe um evento com status "CANCELADO"
    Quando o participante tenta se inscrever neste evento
    Então a inscrição é recusada com a mensagem "Evento não está disponível para inscrições"
```

---

## 6️⃣ Automação com Cucumber (Step Definitions)

### Dependências Maven necessárias

Adicionar no `pom.xml` do módulo de testes (ou no pai):

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.x.x</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>7.x.x</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.x.x</version>
    <scope>test</scope>
</dependency>
```

### Estrutura de arquivos de teste

```
src/test/java/br/[projeto]/
├── runner/
│   └── CucumberRunnerTest.java       # Runner principal
└── steps/
    ├── [Funcionalidade1]Steps.java   # Step definitions da funcionalidade 1
    ├── [Funcionalidade2]Steps.java   # Step definitions da funcionalidade 2
    └── ...
```

### Template de Runner

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.[projeto].steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-report.html")
@SpringBootTest
public class CucumberRunnerTest {}
```

### Template de Step Definitions

```java
@SpringBootTest
public class RealizarInscricaoSteps {

    @Autowired
    private RealizarInscricaoCasoDeUso casoDeUso;

    private Evento eventoDisponivel;
    private Participante participante;
    private Exception excecaoCapturada;

    @Dado("que existe um evento com vagas disponíveis")
    public void existeEventoComVagas() {
        eventoDisponivel = // setup do evento
    }

    @Quando("o participante solicita inscrição no evento")
    public void participanteSolicitaInscricao() {
        try {
            casoDeUso.executar(participante.getId(), eventoDisponivel.getId());
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a inscrição é registrada com status {string}")
    public void inscricaoRegistrada(String status) {
        assertThat(excecaoCapturada).isNull();
        // verifica status
    }

    @Então("a inscrição é recusada com a mensagem {string}")
    public void inscricaoRecusada(String mensagem) {
        assertThat(excecaoCapturada)
            .isNotNull()
            .hasMessageContaining(mensagem);
    }
}
```

---

## ✅ Checklist de Verificação da 1ª Entrega

Antes de considerar a entrega concluída, confirme cada item:

### Documentação
- [ ] `docs/dominio/linguagem-onipresente.md` criado com todos os termos por contexto
- [ ] Mapa de histórias publicado (link ou imagem em `docs/historias/`)
- [ ] Todas as histórias no formato "Como / Quero / Para que"
- [ ] Protótipos para cada funcionalidade principal em `docs/prototipos/`

### Modelagem DDD
- [ ] Arquivo `.cml` na raiz do repositório
- [ ] Todos os Bounded Contexts identificados e representados
- [ ] Relações entre contextos explicitadas no Context Map
- [ ] Cada contexto tem ao menos um Aggregate com Aggregate Root definido
- [ ] DDD aplicado nos quatro níveis (preliminar, estratégico, tático, operacional)

### Código — Domínio
- [ ] Módulos `dominio-*` **sem nenhuma dependência JPA** no `pom.xml`
- [ ] Entidades de domínio são POJOs puros
- [ ] Toda criação de entidade/VO valida invariantes **no construtor**
- [ ] Value Objects são imutáveis
- [ ] Interfaces de repositório estão no módulo de domínio

### Código — Infraestrutura
- [ ] Entidades JPA (`@Entity`) estão apenas no módulo `infraestrutura`
- [ ] Há mappers/conversores entre entidades de domínio e entidades JPA
- [ ] Implementações de repositório herdam/implementam as interfaces do domínio

### Testes BDD
- [ ] Existe ao menos um arquivo `.feature` por funcionalidade não trivial
- [ ] Cada `.feature` tem **pelo menos 3 cenários** distintos
- [ ] Os cenários cobrem: caminho feliz, violação de regra e caso de borda
- [ ] Os cenários estão escritos em português (diretiva `# language: pt`)
- [ ] Todos os cenários têm Step Definitions implementados em Java
- [ ] Runner Cucumber configurado e todos os testes passam

### GitHub
- [ ] Repositório público (ou privado com acesso ao `@profsauloaraujo`)
- [ ] Todos os artefatos versionados (docs, `.cml`, `.feature`, código)
- [ ] README.md atualizado com descrição e instruções de execução

---

## 📌 Resumo Rápido para o Agente

```
FOCO DA 1ª ENTREGA:
1. Domínio bem modelado (DDD completo, sem JPA no domínio)
2. Validações nos construtores das entidades e VOs
3. Arquivo .cml com Context Mapper
4. Cenários .feature com ≥ 3 cenários por funcionalidade
5. Step Definitions funcionando com Cucumber
6. Documentação: linguagem onipresente + histórias + protótipos

NÃO É FOCO DESTA ENTREGA (fica para a 2ª):
- Design patterns (Iterator, Decorator, Observer, etc.)
- Camada de apresentação web completa (Angular/Vaadin)
- Camada de persistência completa e funcional
```

---

*Documento de referência para a 1ª entrega. Para arquitetura e convenções gerais, consulte `AGENT_GUIDE.md`.*
