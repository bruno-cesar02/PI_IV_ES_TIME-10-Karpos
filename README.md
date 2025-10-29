# 🌾 Karpós - Sistema de Gestão Rural

**Disciplina:** Projeto Integrador IV  
**Orientadora:** Profa. Dra. Renata Antônia Tadeu Arantes

## 👥 Equipe

| Nome | RA |
|------|-----|
| Bruno César Gonçalves Lima Mota | 24795502 |
| Felipe Lima Ragonha | 24023900 |
| Henrique Soares | 23013359 |
| Juliano Perusso | 24023434 |
| Nicolas Carvalho Nogueira | 24801664 |
| Otávio Augusto Antunes Marquez | 24025832 |

---

## 1. Definição do MVP

### Funcionalidades Obrigatórias (MVP)

#### 1.1 Módulo de Autenticação, Usuário e Propriedade

- **RF-001:** O sistema deve permitir que um novo usuário (Cliente) se cadastre, fornecendo:
  - Nome Completo
  - E-mail
  - Senha
  - Telefone
  - CPF/CNPJ
  - Nome da Empresa/Propriedade
  - Endereço
  - Tamanho da Cultura (em Hectares)
  - O que cultiva

- **RF-002:** O sistema deve validar se o E-mail e o CPF/CNPJ informados no cadastro já não existem no banco de dados.

- **RF-003:** O sistema deve permitir que um usuário cadastrado realize login através de E-mail e Senha.

- **RF-004:** O sistema deve fornecer uma funcionalidade de "Esqueci minha senha" que permita ao usuário redefinir sua senha através do seu e-mail cadastrado.

- **RF-005:** O sistema deve permitir que um usuário autenticado visualize e edite seus dados cadastrais (Nome, Telefone, Endereço, Senha).

- **RF-006:** O sistema deve permitir que o usuário autenticado visualize e edite as informações da sua propriedade (Nome da Propriedade, Hectares, Cultura).

- **RF-007:** O sistema deve permitir que um usuário autenticado realize logout (saia do sistema).

#### 1.2 Módulo Caderno de Campo

- **RF-008:** O sistema deve permitir que o usuário registre uma nova atividade de campo (ex: Aplicação de Insumo, Plantio, Colheita), associando-a à sua propriedade.

- **RF-009:** O sistema deve permitir que o usuário visualize um histórico cronológico de todas as atividades de campo registradas.

- **RF-010:** O sistema deve permitir que o usuário edite uma atividade de campo já registrada.

- **RF-011:** O sistema deve permitir que o usuário exclua uma atividade de campo já registrada.

- **RF-012:** O sistema deve permitir que o usuário filtre o histórico de atividades por data ou por tipo de atividade.

#### 1.3 Módulo Financeiro Simplificado

- **RF-013:** O sistema deve permitir que o usuário registre um novo custo (Descrição, Valor, Data), associando-o à sua propriedade.

- **RF-014:** O sistema deve permitir que o usuário (opcionalmente) associe um registro de custo a uma atividade do Caderno de Campo.

- **RF-015:** O sistema deve exibir um Dashboard Financeiro Simplificado que calcule e mostre o "Custo Total" (soma de todos os custos) e o "Custo por Hectare" (Custo Total dividido pelos Hectares cadastrados).

- **RF-016:** O sistema deve permitir que o usuário edite um registro de custo já existente.

- **RF-017:** O sistema deve permitir que o usuário exclua um registro de custo já existente.

### Funcionalidades Desejáveis (Futuras)

Funcionalidades que podem ser desenvolvidas futuramente:
- **RF-020 a RF-022:** Módulo de Análise e Mapas
- **RF-023 a RF-026:** Módulo de Monetização
- **RF-027:** Módulo de Colaboradores
- **RF-028 a RF-030:** Painel Administrativo
- **RF-031:** Relatórios Avançados

---

## 2. Decisões Técnicas

### 2.1 Tipo de Solução

**Decisão:** Aplicação Web Responsiva (Progressive Web App)

**Justificativa:**
- Acesso via navegador em desktop e dispositivos móveis
- Não requer instalação em lojas de aplicativos
- Facilita atualizações e manutenção
- Atende ao público-alvo (produtores rurais) que podem acessar de qualquer lugar
- Menor complexidade inicial de desenvolvimento

---

### 2.2 Implementação do Servidor Java (Socket)

**Linguagem:** Java 17+ (LTS)

**Tecnologia:** Java Socket Programming

**Justificativa:**
- Requisito obrigatório da disciplina (servidor em Java)
- Comunicação em tempo real entre cliente e servidor
- Controle total sobre o protocolo de comunicação
- Leve e sem necessidade de frameworks pesados

**Bibliotecas Java Utilizadas:**

| Biblioteca | Finalidade |
|------------|------------|
| `java.net.ServerSocket` | Criação do servidor socket |
| `java.net.Socket` | Conexões cliente-servidor |
| `org.mongodb.driver` | Driver oficial MongoDB para Java |
| `javax.crypto` | Criptografia de senhas (BCrypt) |
| `java.util.concurrent` | Gerenciamento de threads (múltiplos clientes) |

**Arquitetura do Servidor Java:**
- **ServerSocket:** Aguarda conexões na porta 8080
- **ClientHandler:** Thread dedicada para cada cliente conectado
- **RequestParser:** Interpreta comandos recebidos (ex: "LOGIN", "REGISTER", "ADD_ATIVIDADE")
- **MongoDB Connection:** Gerencia conexão com banco de dados
- **Response Builder:** Formata respostas em JSON para

---

### 2.3 Utilização do MongoDB

**Banco de Dados:** MongoDB (NoSQL)

**Hospedagem:** MongoDB Atlas (Cloud - Free Tier)

**Justificativa:**
- Requisito obrigatório da disciplina
- Flexibilidade de schema permite evolução dos dados
- Estrutura de documentos adequada para:
  - Dados de propriedades com informações variáveis
  - Histórico de atividades agrícolas
  - Registros financeiros com relacionamentos opcionais

#### Collections (Coleções) do MongoDB

**Collection: `users`**
- **Armazena:** Dados dos usuários e suas propriedades
- **Estrutura:**
```javascript
{
  _id: ObjectId,
  nome: String,
  email: String,
  senha: String,  // Hash BCrypt
  telefone: String,
  cpfCnpj: String,
  propriedade: {
    nome: String,
    endereco: String,
    tamanhoHectares: Number,
    cultura: String
  },
  criadoEm: Date,
  atualizadoEm: Date
}
```
- **Decisão de Design:** Usuário e propriedade no mesmo documento porque cada usuário tem apenas uma propriedade no MVP

**Collection: `atividades`**
- **Armazena:** Registros do Caderno de Campo
- **Estrutura:**
```javascript
{
  _id: ObjectId,
  usuarioId: ObjectId,  // Referência ao usuário
  tipo: String,  // "Plantio", "Colheita", "Aplicação de Insumo", etc.
  descricao: String,
  data: Date,
  observacoes: String,
  criadoEm: Date,
  atualizadoEm: Date
}
```
- **Decisão de Design:** Campo `usuarioId` garante isolamento de dados (RNF-006)

**Collection: `custos`**
- **Armazena:** Registros de custos da propriedade
- **Estrutura:**
```javascript
{
  _id: ObjectId,
  usuarioId: ObjectId,  // Referência ao usuário
  descricao: String,
  valor: Number,
  data: Date,
  atividadeId: ObjectId,  // OPCIONAL - vincula a uma atividade
  criadoEm: Date,
  atualizadoEm: Date
}
```
- **Decisão de Design:** Campo `atividadeId` opcional permite vincular custos a atividades específicas (RF-014)

#### Isolamento de Dados

**Estratégia:** Todas as queries incluem filtro por `usuarioId`

**Justificativa:**
- Garante que usuários só acessem seus próprios dados (RNF-006)
- Implementação no backend valida token JWT e extrai `usuarioId`
- Queries sempre filtram: `{ usuarioId: idDoUsuarioLogado }`

---

### 2.4 Frontend

**Framework:** React.js 18+ com TypeScript

**Estilização:** Tailwind CSS

**Bibliotecas:**
- `react-router-dom` - Navegação entre páginas
- `axios` - Requisições HTTP para API
- `react-hook-form` - Gerenciamento de formulários
- `recharts` - Gráficos do dashboard financeiro

**Justificativa:**
- React é moderno, componentizado e amplamente utilizado
- TypeScript adiciona segurança de tipos
- Tailwind facilita criação de interfaces responsivas

---

### 2.5 Autenticação

**Estratégia:** JWT (JSON Web Token)

**Fluxo:**
1. Usuário faz login com email e senha
2. Backend valida credenciais
3. Backend gera token JWT assinado (validade: 24h)
4. Frontend armazena token
5. Todas as requisições incluem token no header: `Authorization: Bearer <token>`
6. Backend valida token em cada requisição

**Justificativa:**
- Stateless (servidor não armazena sessão)
- Escalável
- Seguro quando bem implementado

---

## 3. Prototipagem Inicial

**Ferramenta:** Figma

**Link do protótipo:** [Será adicionado]

### Fluxo Principal do Usuário

#### 1. Tela de Login/Cadastro
- Formulário simples com campos:
  - Email
  - Senha
  - Link "Esqueci minha senha"
  - Botão "Cadastrar-se"

#### 2. Tela de Cadastro
- Formulário com campos do RF-001:
  - Nome Completo
  - E-mail
  - Senha
  - Telefone
  - CPF/CNPJ
  - Nome da Propriedade
  - Endereço
  - Tamanho (hectares)
  - O que cultiva
- Checkbox de aceite da Política de Privacidade (RNF-018)

#### 3. Dashboard Principal
- Cards com indicadores:
  - Custo Total (R$)
  - Custo por Hectare (R$/ha)
- Últimas 5 atividades registradas
- Botões de ação rápida:
  - "Nova Atividade"
  - "Novo Custo"
- Menu de navegação para: Caderno de Campo, Financeiro, Perfil

#### 4. Caderno de Campo (Histórico)
- Lista de atividades com:
  - Ícone do tipo de atividade
  - Data
  - Tipo (Plantio, Colheita, etc.)
  - Descrição resumida
- Filtros:
  - Por data (calendário)
  - Por tipo (dropdown)
- Botão "+" para nova atividade

#### 5. Formulário de Nova Atividade (RF-008)
- **Máximo 4 campos** (RNF-002):
  1. Tipo (dropdown com opções predefinidas)
  2. Data (seletor de data)
  3. Descrição (texto curto)
  4. Observações (opcional, texto longo)
- Botões: "Salvar" e "Cancelar"

#### 6. Controle Financeiro
- Lista de custos com:
  - Data
  - Descrição
  - Valor (R$)
  - Atividade vinculada (se houver)
- Botão "+" para novo custo
- Dashboard visual (gráfico de barras simples)

#### 7. Formulário de Novo Custo (RF-013)
- Campos:
  - Descrição
  - Valor (R$)
  - Data
  - Vincular a atividade (opcional, dropdown)
- Botões: "Salvar" e "Cancelar"

### Princípios de Design

**Baseado na Persona Carlos (baixo conforto tecnológico):**

- **Ícones grandes e claros:** Facilitar toque em dispositivos móveis
- **Tipografia grande:** Fontes com tamanho mínimo de 16px
- **Paleta de cores:**
  - Verde (#4CAF50) - remete à agricultura
  - Branco e cinza claro - fundo limpo
  - Vermelho suave - alertas
- **Navegação simples:** Máximo 2-3 níveis de profundidade
- **Feedback visual imediato:** 
  - Mensagens de sucesso (verde)
  - Mensagens de erro (vermelho)
  - Loading states durante requisições
- **Design responsivo:** 
  - Mobile-first
  - Breakpoints para tablet e desktop
- **Botões destacados:** CTA (Call-to-Action) com alto contraste
