# Festa Planner - Backend (Spring Boot)

Backend Java + Spring Boot construído para ser fiel tanto aos **prints de
tela** enviados quanto ao código **real** do projeto Angular
`festaPlanner-main` (não só aos comentários dos `*-service.ts`, mas ao
conteúdo de fato implementado em `orcamento-component.ts`,
`pedido-component.ts` e `agenda-component.ts`).

## Stack

- Java 17 · Spring Boot 3.3 (Web, Data JPA, Security, Validation)
- MySQL · JWT (jjwt) · Lombok

## Estrutura de pacotes

```
com.festaplanner
 ├── config      → SecurityConfig, WebConfig, DataSeeder (admin), CatalogSeeder (catálogo)
 ├── security     → JwtService, JwtAuthenticationFilter, UserDetailsServiceImpl
 ├── controller    → Auth, Admin, Agenda, Catalogo, Orcamento, Pedido
 ├── service       → regra de negócio de cada área
 ├── repository    → JpaRepository / JpaSpecificationExecutor
 ├── model         → Admin, Agenda, CatalogoItem, TipoItem, Orcamento, OrcamentoItem, Pedido
 ├── dto           → um DTO por payload trocado com o frontend
 └── exception     → GlobalExceptionHandler + exceções customizadas
```

## Por que o catálogo mudou de forma (v2)

Na primeira versão eu modelei o catálogo como o `catalogo-service.ts`
documentava: uma lista simples de produtos (nome/descrição/preço/categoria).
Ao analisar os *prints* e principalmente o `orcamento-component.ts` **real**,
vi que o catálogo de verdade é muito mais rico — o componente Angular tem,
hardcoded, ~90 itens em 8 formatos diferentes:

| Seção (tela) | Particularidade |
|---|---|
| Buffet / Bolo | Preço fechado, pode ser obrigatório |
| **Tema** | Varia por tipo de evento: infantil (por gênero), 15 anos (por estilo: clássico/moderno/romântico), casamento/floral/corporativo (1 por evento) |
| Docinhos | Preço por unidade, quantidade mínima + incremento, linha clássica/premium |
| Salgadinhos | Igual a docinhos, categorias tradicional/sofisticado |
| Bebidas | Modo (inclusa/consignação/traz própria), taxa de rolha, itens já inclusos (água/refri/suco) |
| Decoração | Fornecimento (casa/parceiro/cliente traz), unidade de medida (metro/unidade/pacote/diária), alguns "sob orçamento" |
| Música/Animação | Preço fechado + duração em horas |

Por isso criei uma entidade genérica **`CatalogoItem`** com um discriminador
`TipoItem` (`BUFFET, BOLO, TEMA, DOCE, SALGADINHO, BEBIDA, DECORACAO,
MUSICA_ANIMACAO`) e campos que cobrem a união de todos os atributos vistos
acima (campos não aplicáveis a um tipo ficam `null`). Isso permite que a
tela admin **Catálogo / Novo Produto** (que já é genérica nos prints)
gerencie tudo em um lugar só.

O `CatalogSeeder` popula o banco, na primeira execução, com os **mesmos
itens** hoje hardcoded no Angular (todos os temas, docinhos, salgadinhos,
bebidas, itens de decoração e música/animação que consegui extrair do
código-fonte), então o backend já nasce com paridade de dados.

`Orcamento` agora tem uma lista de `OrcamentoItem` (quantidade + preço no
momento da compra), em vez de uma lista simples de IDs — para suportar
"25 un de brigadeiro" e não só "brigadeiro sim/não".

## Como importar no Eclipse

1. Eclipse com plugin **m2e** (vem por padrão no Eclipse IDE for Enterprise Java and Web Developers).
2. `File > Import... > Maven > Existing Maven Projects` → aponte para a pasta `festaplanner-backend`.
3. Se der erro de Lombok: baixe `lombok.jar` em https://projectlombok.org/download, rode `java -jar lombok.jar`, aponte para sua instalação do Eclipse e reinicie.

## Antes de rodar

1. `CREATE DATABASE festaplanner;` (ou deixe o `createDatabaseIfNotExist=true` criar sozinho).
2. Ajuste usuário/senha do MySQL em `application.properties`.
3. Rode `FestaPlannerApplication` (ou `./mvnw spring-boot:run`).

Na primeira execução:
- `DataSeeder` cria o admin padrão: **admin@festaplanner.com / admin123**
- `CatalogSeeder` popula todo o catálogo (buffet, bolo, temas, docinhos, salgadinhos, bebidas, decoração, música)

## Endpoints principais

| Área | Método | Rota | Auth |
|---|---|---|---|
| Auth | POST | `/api/auth/login` | Pública |
| Admin | GET | `/api/admin/dashboard`, `/dashboard/faturamento`, `/dashboard/por-tipo` | JWT |
| Admin | GET/PUT | `/api/admin/perfil` | JWT |
| Catálogo | GET | `/api/catalogo`, `/api/catalogo/{id}` | Pública |
| Catálogo | GET | `/api/catalogo/tipo/{tipo}` (ex: `DOCE`, `DECORACAO`) | Pública |
| Catálogo | GET | `/api/catalogo/tipo/{tipo}/categoria/{categoria}` | Pública |
| Catálogo | GET | `/api/catalogo/temas?evento=&genero=&categoriaTema=` | Pública |
| Catálogo | POST/PUT/DELETE | `/api/catalogo`, `/api/catalogo/{id}` (multipart) | JWT |
| Agenda | GET | `/api/agendas`, `/{id}`, `/mes/{ano}/{mes}` | JWT |
| Agenda | GET | `/api/agendas/disponibilidade/{data}` | Pública |
| Agenda | POST/PUT/DELETE | `/api/agendas`, `/{id}` | JWT |
| Orçamento | POST | `/api/orcamentos` (cria + gera Pedido) | Pública |
| Orçamento | POST | `/api/orcamentos/calcular` (calcula sem persistir) | Pública |
| Orçamento | GET | `/api/orcamentos`, `/{id}` | JWT |
| Orçamento | PUT/DELETE | `/api/orcamentos/{id}/status`, `/{id}` | JWT |
| Pedidos | GET | `/api/pedidos`, `/{id}` | JWT |
| Pedidos | PUT | `/api/pedidos/{id}/status` | JWT |
| Pedidos | POST | `/api/pedidos/{id}/observacao` | JWT |

### Exemplo de payload — criar orçamento

```json
POST /api/orcamentos
{
  "nomeCliente": "Ana Carolina Silva",
  "emailCliente": "ana@email.com",
  "telefoneCliente": "(21) 99999-0001",
  "tipoEvento": "Casamento",
  "dataEvento": "2026-10-15",
  "numeroConvidados": 100,
  "modoBebidas": "inclusa",
  "itens": [
    { "catalogoItemId": 21, "quantidade": 1 },
    { "catalogoItemId": 1,  "quantidade": 1 },
    { "catalogoItemId": 45, "quantidade": 25 },
    { "catalogoItemId": 52, "quantidade": 20 }
  ],
  "observacoes": "Alguma preferência especial"
}
```
(`catalogoItemId` 21 = tema "Casamento", 1 = "Buffet Básico", 45/52 = docinhos/salgadinhos com quantidade — os IDs reais dependem da ordem gerada pelo `CatalogSeeder`; use `GET /api/catalogo/tipo/TEMA` etc. para descobrir.)

## Status usados (decisão de modelagem)

Encontrei **três vocabulários de status diferentes** nos prints/código (ver
recomendações abaixo). Escolhi seguir o que está **realmente implementado
e funcional** no componente Angular, por ser o que o sistema de fato usa
hoje:

- **Pedido**: `novo` → `em_analise` → `aprovado` / `cancelado` (igual a `pedido-component.ts`, incluindo os cards de status "Novos/Em Análise/Aprovados/Cancelados")
- **Agenda**: `confirmado` | `pendente` | `cancelado` (igual ao `eventoForm` de `agenda-component.ts`)
- **Orçamento**: `pendente` | `aprovado` | `negociacao` | `cancelado` (igual a `orcamento-service.ts`)

Se vocês preferirem adotar o vocabulário mais rico visto nos *prints*
estáticos (`NOVO/PENDENTE/PRÉ-RESERVA/CONFIRMADO`), é só avisar — a
mudança no backend é pequena (trocar os valores default e os filtros).

---

## Recomendações de melhoria para o frontend

Ao ler o código Angular completo (não só os comentários dos services) para
construir este backend, notei alguns pontos que vale a pena revisar com o
time:

### 1. O fluxo de orçamento não usa o `CatalogoService` (o mais importante)
`orcamento-component.ts` tem ~90 itens (temas, docinhos, salgadinhos,
bebidas, decoração, música) **hardcoded como arrays locais no componente**,
em vez de buscar do backend via `CatalogoService`. Isso significa que hoje
as telas admin **Catálogo** e **Novo Produto** não têm nenhum efeito real
sobre o que o cliente vê ao montar um orçamento — são dois catálogos
desconectados. Recomendo migrar `orcamento-component.ts` para consumir
`GET /api/catalogo/tipo/{tipo}` (endpoints que já deixei prontos e
populados com os mesmos dados) em vez dos arrays locais. Isso também reduz
bastante o tamanho do bundle do componente.

### 2. Vocabulário de status inconsistente entre 3 lugares
- Mockup estático (prints `adm2pedido.png`/`adm3agenda.png`): `NOVO`, `PENDENTE`, `PRÉ-RESERVA`, `CONFIRMADO` (pedidos) e `Confirmado/Pré-Reserva/Disponível/Bloqueado` (agenda)
- `pedido-component.ts` (real): `novo`, `em_analise`, `aprovado`, `cancelado`
- `agenda-component.ts` (real): `confirmado`, `pendente`, `cancelado`
- `pedidos-service.ts` (comentários): igual ao componente real ✓

Recomendo escolher **um** vocabulário canônico e alinhar mockup HTML,
componente TS, CSS (`status-badge.new/.pending/.prereserva/.confirmed/.rejected`)
e backend. Hoje o mockup estático parece ser uma versão mais antiga (ou
mais nova/aspiracional) que nunca foi sincronizada com o componente real.

### 3. Arquivo `home-service.ts` contém uma cópia do `LoginService`
Ao extrair os services percebi que `core/services/home-service.ts` tem,
na íntegra, o mesmo conteúdo de `login-service.ts` (classe `LoginService`,
mesma lógica de tratamento de erro 401/403/0). Parece um copy-paste
acidental — o esperado seria `home-service.ts` conter os endpoints de
banners/depoimentos/estatísticas da home (que, aliás, o `models/home.ts`
já documenta como futuros). Vale um `git blame`/revisão nesse arquivo.

### 4. Interface `Admin` duplicada com formatos diferentes
`models/admin.ts` define `Admin { id, nome, email, ativo, criadoEm }` e
`models/login.ts` define outro `Admin { id, nome, email, perfil, ativo }`
— sem `perfil` num, sem `criadoEm` no outro. Isso pode causar bugs sutis
de tipagem dependendo de qual import é usado. Recomendo consolidar em um
único `Admin` (fiz isso no backend: um DTO só, com ambos os campos).

### 5. `AuthService` tem métodos duplicados
`isAutenticado()` e `estaAutenticado()` fazem exatamente a mesma coisa
(mesmo para `getToken`/`salvarSessao` vs armazenamento direto). Não é bug,
mas é uma boa oportunidade de limpeza — mantenha um nome só e atualize os
usos.

### 6. Volume de dados estático é um risco de manutenção
Com ~90 itens de catálogo escritos à mão dentro de um `.ts`, qualquer
alteração de preço, adição de item ou correção de descrição exige deploy
do frontend. Migrar para o backend (recomendação #1) resolve isso e ainda
dá ao admin controle real via a tela **Novo Produto**, que hoje é
efetivamente decorativa para o fluxo de orçamento.
