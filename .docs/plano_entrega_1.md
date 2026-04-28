# 📋 Plano de Implementação — 1ª Entrega | Projeto Voke

## 📊 Resumo do que Já Existe

| Componente | Status |
|---|---|
| Estrutura Maven multi-módulo | ✅ Completa (8 módulos + POM pai) |
| 16 funcionalidades definidas | ✅ Documentadas em `Funcionalidades.md` |
| Cenários BDD redigidos | ✅ 16 funcionalidades com ≥3 cenários cada (`cenarios_bdd.md`) |
| Código Java | ❌ Zero — nenhum `.java` existe |
| Documentação formal (`docs/`) | ❌ Inexistente |
| Arquivo `.cml` | ❌ Inexistente |
| Arquivos `.feature` | ❌ Inexistente (estão apenas no MD) |
| Step Definitions | ❌ Inexistente |

---

## 🗂️ Localização dos Testes BDD

Os testes BDD ficam **dentro de cada módulo de domínio** que eles testam, seguindo esta estrutura:

```
dominio-[contexto]/
└── src/
    └── test/
        ├── java/br/voke/
        │   ├── bdd/
        │   │   ├── config/
        │   │   │   └── CucumberSpringConfig.java
        │   │   ├── steps/
        │   │   │   └── [Contexto]Steps.java
        │   │   └── CucumberRunnerTest.java
        │   └── dominio/[contexto]/
        │       └── TestApplication.java
        └── resources/features/
            └── [funcionalidade].feature
```

Cada módulo de domínio tem seu próprio Runner, Step Definitions e arquivos `.feature`.

---

## 🧭 Mapeamento: Funcionalidades → Bounded Contexts → Módulos

Com base no `Funcionalidades.md`, o projeto possui **4 Bounded Contexts** e **16 funcionalidades**.

> ⚠️ **Mudança em relação ao esboço anterior:** O contexto `dominio-pessoa` foi dividido em dois BCs mais coesos.
> É necessário criar o novo módulo Maven `dominio-fidelidade` (ver Fase 0).

### Bounded Context: **Pessoa** (`dominio-pessoa`)

| # | Funcionalidade | Entidade (Aggregate Root) |
|---|---|---|
| 1 | Gerenciar Participante | `Participante` |
| 2 | Gerenciar Organizador | `Organizador` |
| 10 | Gerenciar Amigos e Comunidades | `Amizade` / `ComunidadeAmigos` |
| 11 | Gerenciar Parceiros | `Parceiro` |

> Responsabilidade: identidade dos usuários e grafo social.

### Bounded Context: **Fidelidade** (`dominio-fidelidade`) ← *novo módulo*

| # | Funcionalidade | Entidade (Aggregate Root) |
|---|---|---|
| 9 | Gerenciar Carteira Virtual | `CarteiraVirtual` |
| 14 | Gerenciar Pontos e Fidelidade | `ContaPontos` |
| 15 | Gerenciar Recompensas com Pontos | `Recompensa` |
| 16 | Gerenciar Sugestões do Sistema | `Sugestao` |

> Responsabilidade: economia interna da plataforma (moeda real, pontos e recomendações).

### Bounded Context: **Evento** (`dominio-evento`)

| # | Funcionalidade | Entidade (Aggregate Root) |
|---|---|---|
| 3 | Gerenciar Evento | `Evento` |
| 5 | Gerenciar Grupos de Evento | `GrupoEvento` |
| 6 | Gerenciar Avaliação | `Avaliacao` |
| 7 | Gerenciar Notificações | `Notificacao` |
| 8 | Gerenciar Favoritos | `Favorito` |
| 12 | Gerenciar Cupons | `Cupom` |

> Responsabilidade: ciclo de vida dos eventos e tudo que orbita em torno deles.

### Bounded Context: **Inscrição** (`dominio-inscricao`)

| # | Funcionalidade | Entidade (Aggregate Root) |
|---|---|---|
| 4 | Gerenciar Inscrição | `Inscricao` |
| 13 | Gerenciar Carrinho | `Carrinho` |

> Responsabilidade: fluxo transacional de aquisição de ingressos.

---

## 🗺️ Roteiro de Execução

### Fase 0 — Criar o Módulo Maven `dominio-fidelidade`

> Executar antes de qualquer código, pois o módulo precisa existir para os demais compilarem.

| Tarefa | Artefato |
|---|---|
| Criar pasta `voke/dominio-fidelidade/` com `pom.xml` | `voke/dominio-fidelidade/pom.xml` |
| Registrar o módulo no POM pai | `voke/pom.xml` — adicionar `<module>dominio-fidelidade</module>` |

---

### Fase 1 — Documentação e Modelagem

| # | Tarefa | Artefato |
|---|---|---|
| 1.1 | Criar linguagem onipresente (glossário por BC) | `docs/dominio/linguagem-onipresente.md` |
| 1.2 | Criar mapa de histórias de usuário | `docs/historias/historias.md` |
| 1.3 | Criar arquivo Context Mapper | `voke.cml` |

---

### Fase 2 — Arquivos `.feature` (apenas texto Gherkin)

> Converter os cenários de `cenarios_bdd.md` para os 16 arquivos `.feature`.
> Nesta fase, os Step Definitions **não são escritos ainda** — a implementação Java vem nas fases seguintes.

| # | Tarefa | Destino |
|---|---|---|
| 2.1 | Criar `.feature` para `dominio-pessoa` (funcs 1, 2, 10, 11) | `dominio-pessoa/src/test/resources/features/` |
| 2.2 | Criar `.feature` para `dominio-fidelidade` (funcs 9, 14, 15, 16) | `dominio-fidelidade/src/test/resources/features/` |
| 2.3 | Criar `.feature` para `dominio-evento` (funcs 3, 5, 6, 7, 8, 12) | `dominio-evento/src/test/resources/features/` |
| 2.4 | Criar `.feature` para `dominio-inscricao` (funcs 4, 13) | `dominio-inscricao/src/test/resources/features/` |

---

### Fase 3 — Código de Domínio (DDD Tático)

> **Regra:** 1 entidade de domínio por funcionalidade. Nenhum `@Entity`, nenhum `import` Spring/JPA.

#### 3.1 — `dominio-compartilhado`

| Artefato | Tipo |
|---|---|
| `EntidadeBase` | Classe base com `equals`/`hashCode` por Id |
| `Email` | Value Object |
| `Cpf` | Value Object (com validação do algoritmo oficial) |
| `NomeCompleto` | Value Object |
| `Senha` | Value Object (valida força, nunca expõe valor em texto) |
| `DataNascimento` | Value Object (valida idade mínima) |

#### 3.2 — `dominio-pessoa`

| Entidade / VO | Funcionalidade |
|---|---|
| `Participante` + `ParticipanteId` | Func 1 |
| `Organizador` + `OrganizadorId` | Func 2 |
| `Amizade` + `AmizadeId` | Func 10 |
| `ComunidadeAmigos` + `ComunidadeAmigosId` | Func 10 |
| `Parceiro` + `ParceiroId` | Func 11 |
| Interfaces: `ParticipanteRepositorio`, `OrganizadorRepositorio`, `AmizadeRepositorio`, `ComunidadeAmigosRepositorio`, `ParceiroRepositorio` | — |
| Exceções de domínio (ex: `CpfDuplicadoException`, `IdadeInsuficienteException`) | — |

#### 3.3 — `dominio-fidelidade`

| Entidade / VO | Funcionalidade |
|---|---|
| `CarteiraVirtual` + `CarteiraVirtualId` | Func 9 |
| `ContaPontos` + `ContaPontosId` | Func 14 |
| `Recompensa` + `RecompensaId` | Func 15 |
| `Sugestao` + `SugestaoId` | Func 16 |
| Interfaces: `CarteiraVirtualRepositorio`, `ContaPontosRepositorio`, `RecompensaRepositorio`, `SugestaoRepositorio` | — |
| Exceções de domínio (ex: `SaldoInsuficienteException`, `PontosInsuficientesException`) | — |

#### 3.4 — `dominio-evento`

| Entidade / VO | Funcionalidade |
|---|---|
| `Evento` + `EventoId` | Func 3 |
| `GrupoEvento` + `GrupoEventoId` | Func 5 |
| `Avaliacao` + `AvaliacaoId` | Func 6 |
| `Notificacao` + `NotificacaoId` | Func 7 |
| `Favorito` + `FavoritoId` | Func 8 |
| `Cupom` + `CupomId` | Func 12 |
| Interfaces: `EventoRepositorio`, `GrupoEventoRepositorio`, `AvaliacaoRepositorio`, `NotificacaoRepositorio`, `FavoritoRepositorio`, `CupomRepositorio` | — |
| Exceções de domínio (ex: `ColisaoDeEspacoException`, `EventoCanceladoException`) | — |

#### 3.5 — `dominio-inscricao`

| Entidade / VO | Funcionalidade |
|---|---|
| `Inscricao` + `InscricaoId` | Func 4 |
| `Carrinho` + `CarrinhoId` | Func 13 |
| `ItemCarrinho` | VO/Entidade interna do Carrinho |
| Interfaces: `InscricaoRepositorio`, `CarrinhoRepositorio` | — |
| Exceções de domínio (ex: `VagasEsgotadasException`, `ConflitoDaAgendaException`) | — |

---

### Fase 4 — Casos de Uso (`aplicacao`)

> Um `CasoDeUso` por operação (SRP). Os casos de uso orquestram o domínio e chamam repositórios via interface.

| Funcionalidade | Casos de Uso (exemplos) |
|---|---|
| Func 1 (Participante) | `CadastrarParticipanteCasoDeUso`, `EditarParticipanteCasoDeUso`, `RemoverParticipanteCasoDeUso` |
| Func 2 (Organizador) | `CadastrarOrganizadorCasoDeUso`, `EditarOrganizadorCasoDeUso` |
| Func 3 (Evento) | `CriarEventoCasoDeUso`, `EditarEventoCasoDeUso`, `CancelarEventoCasoDeUso` |
| Func 4 (Inscrição) | `RealizarInscricaoCasoDeUso`, `CancelarInscricaoCasoDeUso` |
| *(demais funcs)* | *(seguir o mesmo padrão)* |

---

### Fase 5 — Step Definitions e Runner Cucumber

> Agora que as entidades e casos de uso existem, é possível implementar os Step Definitions.

| # | Tarefa | Destino |
|---|---|---|
| 5.1 | Implementar `Steps` + `CucumberRunnerTest` + `CucumberSpringConfig` + `TestApplication` para `dominio-pessoa` | `dominio-pessoa/src/test/java/br/voke/bdd/` |
| 5.2 | Idem para `dominio-fidelidade` | `dominio-fidelidade/src/test/java/br/voke/bdd/` |
| 5.3 | Idem para `dominio-evento` | `dominio-evento/src/test/java/br/voke/bdd/` |
| 5.4 | Idem para `dominio-inscricao` | `dominio-inscricao/src/test/java/br/voke/bdd/` |
| 5.5 | Garantir que todos os cenários passam com `mvn test` | — |

---

### Fase 6 — Infraestrutura (`infraestrutura`)

> Implementar o necessário para os testes de integração passarem.
> Não precisa ser completo — apenas o suficiente para cada funcionalidade testada.

| Artefato | Padrão |
|---|---|
| `[Entidade]Jpa.java` | `@Entity` com mapeamento ORM |
| `[Entidade]JpaMapper.java` | Conversor domínio ↔ JPA |
| `Spring[Entidade]Repository.java` | `extends JpaRepository` |
| `[Entidade]RepositorioJpa.java` | Implementa interface do domínio |

---

### Fase 7 — Protótipos e Fechamento

| # | Tarefa | Artefato |
|---|---|---|
| 7.1 | Gerar wireframes low-fidelity das telas | `docs/prototipos/` |
| 7.2 | Atualizar `README.md` com descrição e instruções | `readme.md` |

---

## 📏 Regras Chave

- 🚫 **Zero JPA** nos módulos `dominio-*`
- ✅ **1 Entidade de domínio por funcionalidade** — nunca empurre responsabilidades extras em entidade existente
- ✅ **Validações nos construtores** de entidades e VOs
- ✅ **VOs imutáveis** com campos `final`
- ✅ **≥ 3 cenários BDD** por funcionalidade (todos já atendem!)
- ✅ **Conventional Commits** em português no imperativo
- ✅ **1 caso de uso por classe** (SRP)
- ✅ **Testes BDD co-localizados** dentro do módulo de domínio correspondente

---

## 🏗️ Estrutura Maven Final (após Fase 0)

```
voke/
├── pom.xml                    # POM pai
├── dominio-compartilhado/
├── dominio-pessoa/            # BC Pessoa (funcs 1, 2, 10, 11)
├── dominio-fidelidade/        # BC Fidelidade (funcs 9, 14, 15, 16) ← novo
├── dominio-evento/            # BC Evento (funcs 3, 5, 6, 7, 8, 12)
├── dominio-inscricao/         # BC Inscrição (funcs 4, 13)
├── aplicacao/
├── infraestrutura/
├── apresentacao-backend/
└── apresentacao-frontend/
```
