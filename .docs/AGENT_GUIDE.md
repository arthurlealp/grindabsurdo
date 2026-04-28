# 🤖 Guia do Agente — Projeto Acadêmico (DDD + Clean Architecture)

> Este documento é destinado a agentes de IA que irão auxiliar no desenvolvimento do projeto.
> Leia com atenção **antes** de qualquer ação de codificação ou geração de artefatos.

---

## 📌 Visão Geral do Projeto

Sistema web em Java com múltiplos subdomínios, aplicando **Domain-Driven Design (DDD)** em todos os seus níveis (preliminar, estratégico, tático e operacional) e **Arquitetura Limpa (Clean Architecture)**. O projeto é estruturado como um **Maven multi-módulo**, com cada subdomínio em seu próprio módulo.

- **Linguagem:** Java 17
- **Framework principal:** Spring Boot
- **Persistência:** Spring Data JPA + Hibernate (apenas nas camadas de infraestrutura — **NUNCA no domínio**)
- **Banco de dados:** MySQL (relacional)
- **Frontend:** Angular ou Vaadin
- **Testes BDD:** Cucumber + JUnit

---

## 🏗️ Estrutura de Módulos Maven

A estrutura de referência, baseada no projeto `gestao-eventos`, é a seguinte:

```
raiz/
├── pai/                        # POM pai (gerencia dependências e módulos)
├── dominio-compartilhado/      # Tipos base compartilhados (ValueObjects, interfaces, etc.)
├── dominio-[contexto-a]/       # Módulo de domínio do contexto A (puro, sem JPA)
├── dominio-[contexto-b]/       # Módulo de domínio do contexto B (puro, sem JPA)
├── ...                         # Um módulo de domínio por Bounded Context
├── infraestrutura/             # Implementações de repositório, JPA, adaptadores externos
├── aplicacao/                  # Casos de uso / serviços de aplicação
└── apresentacao-backend/       # Controllers REST (Spring MVC / API)
```

> Cada novo subdomínio identificado deve gerar um módulo `dominio-[nome]` separado.

---

## ⚠️ Regras Absolutas (Não Negociáveis)

### 1. 🚫 Zero JPA na Camada de Domínio

Os módulos `dominio-*` **não devem ter nenhuma dependência** do Spring, JPA, Hibernate ou qualquer framework de infraestrutura.

**Errado:**
```java
// dominio-evento/src/.../Evento.java
@Entity                          // ❌ PROIBIDO no domínio
@Table(name = "eventos")         // ❌ PROIBIDO no domínio
public class Evento { ... }
```

**Correto:**
```java
// dominio-evento/src/.../Evento.java
public class Evento {            // ✅ POJO puro
    private EventoId id;
    private Nome nome;
    ...
}

// infraestrutura/src/.../EventoJpa.java
@Entity                          // ✅ Entidade JPA fica na infraestrutura
@Table(name = "eventos")
public class EventoJpa { ... }   // Mapeada separadamente, com conversores/mappers
```

As interfaces de repositório são declaradas no domínio, mas implementadas na infraestrutura:

```java
// dominio-evento (interface pura)
public interface EventoRepositorio {
    void salvar(Evento evento);
    Optional<Evento> buscarPorId(EventoId id);
}

// infraestrutura (implementação com Spring Data JPA)
@Repository
public class EventoRepositorioJpa implements EventoRepositorio {
    private final SpringEventoRepository springRepo;
    ...
}
```

---

### 2. ✅ Validações nos Construtores

Toda regra de negócio de criação e invariante de domínio **deve ser validada no construtor** da entidade ou Value Object. Não delegue validações para camadas externas.

**Errado:**
```java
// ❌ Validação no serviço de aplicação
public Evento criarEvento(String nome, int vagas) {
    if (nome == null || nome.isBlank()) throw new IllegalArgumentException("...");
    return new Evento(nome, vagas);
}
```

**Correto:**
```java
// ✅ Validação no construtor da entidade de domínio
public class Evento {
    public Evento(Nome nome, QuantidadeVagas vagas, DataRealizacao data) {
        Objects.requireNonNull(nome, "Nome é obrigatório");
        Objects.requireNonNull(vagas, "Vagas é obrigatório");
        Objects.requireNonNull(data, "Data é obrigatória");
        if (data.isBefore(LocalDate.now())) {
            throw new DataInvalidaException("Evento não pode ser no passado");
        }
        this.nome = nome;
        this.vagas = vagas;
        this.data = data;
    }
}

// ✅ Value Objects também se autovalidam
public class QuantidadeVagas {
    public QuantidadeVagas(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Vagas deve ser positivo");
        this.valor = valor;
    }
}
```

---

### 3. 🧩 Uma Entidade de Domínio por Funcionalidade

Cada funcionalidade não trivial do sistema deve possuir **sua própria entidade de domínio**. É proibido reaproveitar uma entidade existente para cobrir responsabilidades de outra funcionalidade.

Essa regra garante que cada contexto tenha seu modelo coeso, evitando entidades "faz-tudo" que acumulam atributos e comportamentos de múltiplas funcionalidades.

**Errado:**
```java
// ❌ A entidade Evento acumula responsabilidades de inscrição, pagamento e certificado
public class Evento {
    private List<Participante> inscritos;    // ❌ responsabilidade de Inscrição
    private boolean pagamentoConfirmado;     // ❌ responsabilidade de Pagamento
    private String codigoCertificado;        // ❌ responsabilidade de Certificado
}
```

**Correto:**
```java
// ✅ Cada funcionalidade tem sua própria entidade com responsabilidade clara
public class Evento { ... }        // responsabilidade: representar o evento

public class Inscricao { ... }     // responsabilidade: inscrição de participante

public class Pagamento { ... }     // responsabilidade: controle de pagamento

public class Certificado { ... }   // responsabilidade: emissão de certificado
```

> Se você sentir que precisa adicionar um campo a uma entidade existente para atender uma nova funcionalidade, esse é um sinal claro de que a nova funcionalidade precisa de sua **própria entidade**.

---

### 4. 📋 Mínimo de 3 Cenários BDD por Funcionalidade

Cada funcionalidade deve ter **mais de 2 cenários de teste** (mínimo 3, recomendado 4+), cobrindo:

- Cenário feliz (caminho principal)
- Cenário de validação/negócio (dados inválidos ou regra violada)
- Cenário de borda ou alternativo (ex.: recurso não encontrado, duplicidade, limite atingido)

**Estrutura de exemplo em `.feature`:**

```gherkin
Feature: [Nome da Funcionalidade]

  Scenario: [Cenário feliz]
    Given ...
    When ...
    Then ...

  Scenario: [Violação de regra de negócio]
    Given ...
    When ...
    Then ...

  Scenario: [Caso de borda]
    Given ...
    When ...
    Then ...

  Scenario: [Outro caso relevante]
    Given ...
    When ...
    Then ...
```

---

## 🧱 Níveis de DDD a Aplicar

### Nível Preliminar
- Identificação do problema e do contexto de negócio
- Definição da linguagem onipresente (*Ubiquitous Language*) para cada Bounded Context
- Glossário de termos do domínio (em português, com significado preciso no contexto)

### Nível Estratégico
- Identificação dos **Bounded Contexts**
- Mapeamento das relações entre contextos (Context Map)
- Arquivo `.cml` gerado com o **Context Mapper**
- Identificação de Domínio Principal, Subdomínios de Suporte e Genéricos

### Nível Tático
- **Entities** (com identidade própria — `Id` como Value Object)
- **Value Objects** (imutáveis, sem identidade, autovalidados)
- **Aggregates** e **Aggregate Roots**
- **Domain Services** (para lógica que não pertence a uma entidade específica)
- **Domain Events** (quando relevante)
- **Repositories** (interfaces no domínio, implementações na infraestrutura)

### Nível Operacional
- Implementação dos casos de uso na camada de aplicação
- Exposição via API REST na camada de apresentação
- Persistência via JPA/Hibernate na camada de infraestrutura
- Testes automatizados com Cucumber

---

## 🔄 Fluxo de uma Funcionalidade (Referência)

```
HTTP Request
    │
    ▼
[apresentacao-backend] Controller
    │  DTO de entrada
    ▼
[aplicacao] Caso de Uso / Serviço de Aplicação
    │  chama repositório (interface do domínio)
    │  instancia entidades do domínio
    ▼
[dominio-x] Entidade / Aggregate Root
    │  regras de negócio, validações no construtor
    ▼
[infraestrutura] Implementação do Repositório (JPA)
    │  converte domínio → JPA Entity → banco
    ▼
[MySQL] Banco de Dados Relacional
```

---

## 📦 Dependências entre Módulos (Regra de Dependência)

```
apresentacao-backend  →  aplicacao
aplicacao             →  dominio-*
aplicacao             →  infraestrutura (via interfaces)
infraestrutura        →  dominio-* (implementa interfaces)
infraestrutura        →  dominio-compartilhado
dominio-*             →  dominio-compartilhado
dominio-*             NÃO depende de infraestrutura
dominio-*             NÃO depende de Spring/JPA
```

---

## 📝 Convenções de Nomenclatura

| Elemento           | Convenção                          | Exemplo                        |
|--------------------|-------------------------------------|--------------------------------|
| Módulo Maven       | `dominio-[contexto]`               | `dominio-inscricao`            |
| Pacote base        | `br.[projeto].[contexto]`          | `br.gestao.inscricao`          |
| Aggregate Root     | Substantivo singular               | `Inscricao`, `Evento`          |
| Value Object       | Substantivo descritivo             | `Email`, `NomeCompleto`        |
| Repository (iface) | `[Entidade]Repositorio`            | `InscricaoRepositorio`         |
| Repository (impl)  | `[Entidade]RepositorioJpa`         | `InscricaoRepositorioJpa`      |
| Caso de Uso        | Verbo + Substantivo + `CasoDeUso`  | `RealizarInscricaoCasoDeUso`   |
| Controller         | `[Contexto]Controller`             | `InscricaoController`          |
| Feature file       | `[nome-funcionalidade].feature`    | `realizar-inscricao.feature`   |
| Step Definitions   | `[NomeFuncionalidade]Steps`        | `RealizarInscricaoSteps`       |

---

## 🔧 Stack Técnica e Dependências

| Camada           | Tecnologia                         |
|------------------|------------------------------------|
| Domínio          | Java 17 puro (sem frameworks)      |
| Aplicação        | Spring (IoC apenas), Java 17       |
| Infraestrutura   | Spring Data JPA, Hibernate, MySQL  |
| Apresentação     | Spring MVC (REST Controllers)      |
| Frontend         | Angular ou Vaadin                  |
| Testes           | JUnit 5, Cucumber, Spring Boot Test|
| Build            | Maven (multi-módulo)               |
| Versionamento    | Git + GitHub (repositório público) |

---

## 🚀 Checklist Antes de Commitar Código

- [ ] Nenhuma anotação JPA (`@Entity`, `@Column`, etc.) em módulos `dominio-*`
- [ ] Toda criação de entidade valida suas invariantes no construtor
- [ ] Value Objects são imutáveis (`final` nos campos)
- [ ] Interface de repositório está no módulo de domínio
- [ ] Implementação JPA está no módulo de infraestrutura
- [ ] Cada funcionalidade tem pelo menos 3 cenários Gherkin
- [ ] Os cenários Gherkin estão automatizados com Cucumber + Step Definitions
- [ ] Sem lógica de negócio nos Controllers
- [ ] Sem lógica de negócio nos Repositórios JPA

---

## 📁 Repositório

O projeto deve estar disponível no GitHub em um repositório:
- **Público**, ou **privado com acesso** ao usuário `@profsauloaraujo`
- Todos os artefatos devem ser versionados: código, documentação, modelos `.cml`, arquivos `.feature`

---

## 📝 Padrão de Commit

Todos os commits devem seguir o padrão **Conventional Commits**. O formato é:

```
<tipo>(<escopo>): <descrição curta no imperativo>

[corpo opcional — explica o "por quê", não o "o quê"]

[rodapé opcional — ex: referência a issue]
```

### Tipos permitidos

| Tipo | Quando usar |
|------|-------------|
| `feat` | Nova funcionalidade ou caso de uso |
| `fix` | Correção de bug ou comportamento incorreto |
| `test` | Adição ou correção de testes (Cucumber, JUnit) |
| `docs` | Alterações em documentação (`.md`, `.cml`, comentários) |
| `refactor` | Mudança de código que não adiciona feature nem corrige bug |
| `chore` | Configurações de build, dependências, arquivos de projeto |
| `style` | Formatação, espaçamento, sem mudança de lógica |

### Escopos recomendados

O escopo deve indicar **qual módulo ou contexto** foi afetado:

| Escopo | Representa |
|--------|-----------|
| `dominio-[contexto]` | Módulo de domínio específico |
| `infraestrutura` | Camada de persistência e adaptadores |
| `aplicacao` | Casos de uso |
| `apresentacao` | Controllers e API REST |
| `bdd` | Arquivos `.feature` e Step Definitions |
| `docs` | Documentação geral |
| `cml` | Arquivo Context Mapper |
| `config` | Configurações gerais do projeto |

### Exemplos de commits válidos

```bash
# Nova funcionalidade de domínio
git commit -m "feat(dominio-inscricao): adicionar validação de conflito de horário no construtor"

# Novo cenário BDD
git commit -m "test(bdd): adicionar cenários de borda para realizar-inscricao.feature"

# Correção de regra de negócio
git commit -m "fix(dominio-evento): corrigir validação de data retroativa no construtor de Evento"

# Documentação
git commit -m "docs(cml): atualizar relações entre contextos no context map"

# Infraestrutura
git commit -m "feat(infraestrutura): implementar EventoRepositorioJpa com mapeamento ORM"

# Refatoração
git commit -m "refactor(dominio-participante): extrair validação de CPF para ValueObject Cpf"

# Configuração
git commit -m "chore(config): adicionar dependências do Cucumber no pom.xml"
```

### Regras para o agente ao gerar commits

- A **descrição** deve estar no imperativo e em português: "adicionar", "corrigir", "extrair", não "adicionado", "corrigido"
- Máximo de **72 caracteres** na primeira linha
- Nunca commitar código que quebra a compilação
- Nunca misturar múltiplas responsabilidades no mesmo commit (ex.: feat + refactor)
- Arquivos `.feature` e seus Step Definitions devem ser commitados **juntos**

---

## 📦 Estrutura de Pacotes Java (por módulo)

A plataforma do professor gera a estrutura de módulos Maven automaticamente. O agente deve respeitar a organização interna de pacotes dentro de cada módulo.

> O pacote base do projeto é `br.[nome-do-projeto]`. Substitua conforme o nome real.

### `dominio-[contexto]/`

```
br.[projeto].[contexto]/
├── [Entidade].java                  # Aggregate Root ou Entidade
├── [EntidadeId].java                # Value Object de identidade
├── [OutroValueObject].java          # Outros Value Objects do contexto
├── [Contexto]Repositorio.java       # Interface do repositório (porta de saída)
├── [Contexto]Servico.java           # Domain Service (se necessário)
└── excecao/
    └── [RegraVioladaException].java # Exceções de domínio específicas
```

**Exemplo real:**
```
br.gestao.inscricao/
├── Inscricao.java
├── InscricaoId.java
├── StatusInscricao.java             # Enum de domínio
├── InscricaoRepositorio.java
└── excecao/
    └── VagasEsgotadasException.java
```

### `dominio-compartilhado/`

```
br.[projeto].compartilhado/
├── EntidadeBase.java                # Classe base com equals/hashCode por Id
├── ValueObjectBase.java             # (opcional) base para VOs
└── [TiposCompartilhados].java       # Ex: Email, CPF, NomeCompleto
```

### `aplicacao/`

```
br.[projeto].[contexto]/
└── [NomeFuncionalidade]CasoDeUso.java   # Um arquivo por caso de uso
```

**Exemplo:**
```
br.gestao.inscricao/
├── RealizarInscricaoCasoDeUso.java
├── CancelarInscricaoCasoDeUso.java
└── dto/
    ├── RealizarInscricaoEntrada.java    # DTO de entrada (record ou classe)
    └── InscricaoSaida.java              # DTO de saída
```

### `infraestrutura/`

```
br.[projeto].[contexto]/
├── [Entidade]Jpa.java               # Entidade JPA (@Entity) — espelho da de domínio
├── [Entidade]JpaMapper.java         # Conversor domínio ↔ JPA
├── Spring[Entidade]Repository.java  # Interface Spring Data (extends JpaRepository)
└── [Entidade]RepositorioJpa.java    # Implementa a interface do domínio
```

**Exemplo:**
```
br.gestao.inscricao/
├── InscricaoJpa.java
├── InscricaoJpaMapper.java
├── SpringInscricaoRepository.java
└── InscricaoRepositorioJpa.java
```

### `apresentacao-backend/`

```
br.[projeto].[contexto]/
├── [Contexto]Controller.java        # @RestController com os endpoints
└── dto/
    ├── [Recurso]Request.java        # Corpo da requisição HTTP
    └── [Recurso]Response.java       # Corpo da resposta HTTP
```

### `src/test/` (módulo de testes BDD)

```
src/
├── test/
│   ├── java/br.[projeto]/
│   │   ├── runner/
│   │   │   └── CucumberRunnerTest.java
│   │   └── steps/
│   │       ├── [Funcionalidade1]Steps.java
│   │       └── [Funcionalidade2]Steps.java
│   └── resources/
│       └── features/
│           ├── [funcionalidade-1].feature
│           └── [funcionalidade-2].feature
```

---

## 🚨 Anti-patterns — O Que Nunca Fazer

O agente deve identificar e **recusar** implementar qualquer um dos padrões abaixo.

### ❌ Lógica de negócio fora do domínio

```java
// ERRADO — regra de negócio no Controller
@PostMapping
public ResponseEntity<?> inscrever(@RequestBody InscricaoRequest req) {
    if (evento.getVagas() <= 0) {           // ❌ regra de negócio aqui
        return ResponseEntity.badRequest().build();
    }
    ...
}

// ERRADO — regra de negócio no Repositório
public void salvar(Inscricao inscricao) {
    if (inscricao.getParticipante() == null) { // ❌ validação aqui
        throw new RuntimeException("...");
    }
    ...
}
```

### ❌ JPA no domínio

```java
// ERRADO
@Entity                        // ❌
public class Evento {
    @Id @GeneratedValue        // ❌
    private Long id;
    @Column(nullable = false)  // ❌
    private String nome;
}
```

### ❌ Construtor sem validação

```java
// ERRADO — construtor permissivo
public class QuantidadeVagas {
    public QuantidadeVagas(int valor) {
        this.valor = valor;    // ❌ aceita qualquer valor, inclusive negativo
    }
}
```

### ❌ Controller chamando repositório diretamente

```java
// ERRADO — pula a camada de aplicação e o domínio
@PostMapping
public ResponseEntity<?> criar(@RequestBody EventoRequest req) {
    EventoJpa jpa = new EventoJpa(req.getNome());
    eventoRepository.save(jpa);            // ❌ repositório JPA direto no Controller
    return ResponseEntity.ok().build();
}
```

### ❌ Value Objects mutáveis

```java
// ERRADO — VO com setter
public class Email {
    private String valor;
    public void setValor(String valor) {   // ❌ VOs devem ser imutáveis
        this.valor = valor;
    }
}

// CORRETO
public final class Email {
    private final String valor;            // ✅ final
    public Email(String valor) {
        if (!valor.contains("@")) throw new IllegalArgumentException("Email inválido");
        this.valor = valor;
    }
    public String getValor() { return valor; }
}
```

### ❌ Caso de uso com múltiplas responsabilidades

```java
// ERRADO — um caso de uso que faz tudo
public class EventoCasoDeUso {
    public void criar(...) { ... }
    public void cancelar(...) { ... }
    public void listar(...) { ... }    // ❌ responsabilidade única violada
    public void buscarPorData(...) { ... }
}

// CORRETO — um caso de uso por operação
public class CriarEventoCasoDeUso { ... }     // ✅
public class CancelarEventoCasoDeUso { ... }  // ✅
```

### ❌ Menos de 3 cenários BDD por funcionalidade

```gherkin
# ERRADO — apenas 1 cenário
Funcionalidade: Realizar inscrição

  Cenário: Inscrição com sucesso   # ❌ insuficiente
    Dado ...
    Quando ...
    Então ...
```

### ❌ Reutilizar entidade existente para nova funcionalidade

```java
// ERRADO — empurrar responsabilidades novas em entidade existente
public class Evento {
    private String codigoCertificado;     // ❌ isso é responsabilidade de Certificado
    private boolean pagamentoEfetuado;    // ❌ isso é responsabilidade de Pagamento
}

// CORRETO — criar entidade própria para cada funcionalidade
public class Certificado {
    private CertificadoId id;
    private EventoId eventoId;
    private ParticipanteId participanteId;
    private CodigoCertificado codigo;

    public Certificado(EventoId eventoId, ParticipanteId participanteId) {
        Objects.requireNonNull(eventoId);
        Objects.requireNonNull(participanteId);
        this.codigo = CodigoCertificado.gerar();  // ✅ regra encapsulada na entidade
        ...
    }
}
```

---

*Para os requisitos específicos da 1ª Entrega, consulte o arquivo `ENTREGA_1.md`.*
