
# 🌾 Karpós - Plataforma de Agricultura de Precisão

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Git](https://img.shields.io/badge/Git-E34F26?style=for-the-badge&logo=git&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)
![Status](https://img.shields.io/badge/status-Em%20Desenvolvimento-yellow)

> Solução completa para gestão de propriedades rurais, focada em acessibilidade e simplicidade para produtores com baixo conforto tecnológico.

---

## 📋 Índice

- [Equipe](#-equipe)
- [Visão Geral](#-visão-geral-do-projeto)
- [MVP](#-MVP:_Funcionalidades_Principais)
- [Arquitetura](#-arquitetura-técnica)
- [Estrutura do Repositório](#-estrutura-do-repositório)
- [Modelos de Dados](#-modelos-das-coleções-mongodb)
- [Decisões Técnicas](#-decisões-técnicas)
- [Processos Implementados](#-fluxos-e-processos-implementados)
- [Como Executar](#-como-executar)
- [Testes](#-testes-integração-e-dados-de-exemplo)
- [Diagrama](#-diagrama-de-arquitetura)
- [Protótipos](#-printsprotótipos)
- [Organização](#-cronograma-e-organização)
- [Banca Final](#-roteiro-para-banca-final)
- [Referências](#-referências-e-ferramentas)

---

## 👥 Equipe

| Nome | RA |
|:-----|:--:|
| Bruno César Gonçalves Lima Mota | 24795502 |
| Felipe Lima Ragonha | 24023900 |
| Henrique Soares | 23013359 |
| Juliano Perusso | 24023434 |
| Nicolas Carvalho Nogueira | 24801664 |
| Otávio Augusto Antunes Marquez | 24025832 |

---
### Papéis e Responsabilidades

| Membro | Papel Principal | Contribuições |
|:-------|:---------------|:--------------|
| Bruno César | Rotas e Project | Rotas get e post e front-end |
| Felipe Lima | Telas | front-end |
| Henrique Soares | Banco | Servidor java e Banco|
| Juliano Perusso | Servidor | Servidor java e Banco|
| Nicolas Carvalho | Canva, Telas | Fluxo de telas/design e front-end |
| Otávio Augusto | Controller e node| Controllers e middleware |


## 🎯 Visão Geral do Projeto

**Karpós** é uma solução voltada para produtores rurais, facilitando o controle de atividades agrícolas, cadastro de propriedades e registros financeiros. 

### Objetivo Principal

Oferecer uma interface **simples e acessível** para usuários com baixo conforto tecnológico, centralizando todos os acessos e operações via um servidor Java robusto.

---

## MVP: Funcionalidades Principais

### ✅ Funcionalidades Implementadas

- ✔️ Cadastro e autenticação de usuários e propriedades
- ✔️ Registro, consulta, edição e exclusão de atividades agrícolas
- ✔️ Controle de custos financeiros da propriedade

### 🔮 Funcionalidades Futuras

- 📊 Módulos de análise de dados
- 💰 Sistema de monetização
- 👥 Gestão de colaboradores
- 📄 Relatórios avançados e dashboards

---

## 🏗️ Arquitetura Técnica

```
┌─────────────────┐
│   Navegador     │
│  (HTML/CSS/JS)  │
└────────┬────────┘
         │ HTTP (Express.js)
         ▼
┌─────────────────────────────┐
│   Node.js (Frontend)        │
│  - EJS Templates            │
│  - Routes (Express)         │
│  - Controllers              │
└────────┬────────────────────┘
         │ Socket/REST
         ▼
┌─────────────────────────────┐
│   Servidor Java             │
│  - Validação de dados       │
│  - Regras de negócio        │
│  - Conexão MongoDB          │
└────────┬────────────────────┘
         │ Driver MongoDB
         ▼
┌─────────────────────────────┐
│   MongoDB Atlas             │
│  - users                    │
│  - atividades               │
│  - custos                   │
└─────────────────────────────┘

```

### Componentes

| Componente | Tecnologia | Descrição |
|:-----------|:-----------|:----------|
| **Backend** | Java 17+ (Sockets) | Servidor responsável por toda lógica de negócio, validação e conexão com banco |
| **Cliente** | Java (Terminal) | Aplicativo para interação via linha de comando (possibilidade de extensão futura) |
| **Banco de Dados** | MongoDB Atlas | Persistência NoSQL dos dados do sistema |

---

## 📂 Estrutura do Repositório

```
PI_IV_ES_TIME-10-Karpos/
│
├── 📁 servidor/                           # Backend Java (autenticação, cadastro, regras de negócio)
│   ├── Main.java
│   ├── ServerSemBanco.java
│   ├── TratadoraDePedidos.java
│   ├── 📁 dbConection/
│   │   ├── DBConection.java
│   │   └── DBUse.java
│   └── 📁 Drivers/
│       ├── mongodb-driver-sync-5.6.1.jar
│       ├── mongodb-driver-core-5.6.1.jar
│       └── bson-5.6.1.jar
├── 📁 cliente/                            # Cliente Java (terminal - possível extensão futura)
│   └── ClienteTeste.java
|
├── 📁 comum/                              # Classes compartilhadas entre servidor e cliente
│   └── [Entidades/DTOs]
|
└── 📁 node/                               # Frontend Web (Node.js + Express + EJS)
    ├── server.js                          # Ponto de entrada do servidor Express
    ├── routes.js                          # Definição das rotas HTTP
    ├── package.json                       # Dependências do projeto
    ├── package-lock.json
    ├── .gitignore                         # Exclui node_modules do Git
    │
    └── src/
        ├── controllers/                   # Camada de controle (lógica por rota)
        │   ├── indexController.js        # Página inicial (usuário não logado)
        │   ├── loginController.js        # Tela de login
        │   ├── registerController.js     # Tela de cadastro
        │   ├── dadosController.js        # Tela de dados do usuário/propriedade
        │   ├── dashboardController.js    # Dashboard principal
        │   ├── cadernoCampoController.js # Funções do Caderno de Campo
        │   └── custosController.js       # Funções de Custo
        │  
        │
        ├── middlewares/                  # Autenticação e fluxo
        │   ├── verificarSeLogado.js     # Bloqueia acesso sem autenticação
        │   └── redirecionarSeLogado.js  # Redireciona logado para dashboard
        │
        ├── views/                        # Templates EJS (HTML dinâmico)
        │   ├── includes/                 # Componentes reutilizáveis
        │   │   └── head.ejs             # <head> comum com CSS dinâmico
        │   ├── index.ejs                # Landing page
        │   ├── login.ejs                # Tela de login
        │   ├── register.ejs             # Tela de cadastro
        │   ├── dashboard.ejs            # Dashboard do produtor
        │   ├── caderno-campo.ejs        # Histórico de atividades
        │   ├── novo-registro.ejs        # Formulário de nova atividade
        │   ├── custos-registrados.ejs   # Histórico de custos
        │   └── novo-custo.ejs           # Formulário de novo custo
        │
        └── public/                       # Arquivos estáticos
            ├── css/
            │   ├── dashboard.css        # Layout base (sidebar, topbar, cards)
            │   └── caderno-campo.css    # Tabelas, filtros, formulários (reutilizável)
            │
            └── img/
                 └── ...        # todas as imagens usada no projeto
                     
```

---

## 💾Coleções MongoDB

### 👤 Collection: `user-data`

Armazena dados dos usuários e suas propriedades rurais.

```
{
  "_id": "ObjectId",
  "nome": "string",
  "email": "string",
  "senha": "string",
  "telefone": "string",
  "documento": "string",
  "tamanhoHectares": "double",
  "userID": "number",
  "data": "string"
}
```

---

### 🌱 Collection: `field-metrics`

Registra atividades agrícolas realizadas na propriedade.

```
{
  "_id": "ObjectId",
  "data": "string (formato: YYYY-MM-DD)",
  "tipoAtividade": "string (Plantio, Colheita, Aplicação, etc.)",
  "texto": "string (descrição detalhada)",
  "userID": "number (ref: user-data.userID)"
}

```

---



## 🔧 Decisões Técnicas

### Princípios de Design

-  **Isolamento de Dados**: Todas as queries incluem filtro por `usuarioId`
-  **CRUD Centralizado**: Todas operações passam pelo servidor Java
-  **Validação no Backend**: Dados validados antes de persistência
-  **Segurança**: Senhas armazenadas com hash BCrypt
-  **Camadas Separadas**: Entidade, serviço e comunicação bem definidos

### Bibliotecas Utilizadas

| Biblioteca | Versão | Finalidade |
|:-----------|:-------|:-----------|
| `mongodb-driver-sync` | 5.6.1 | Driver oficial MongoDB |
| `bson` | 5.6.1 | Serialização de documentos |
| `slf4j-api` | 2.0.17 | Logging |

---

## 🔄 Fluxos e Processos Implementados

#### 1. Cadastro de Usuário e Propriedade
**Objetivo:** Permitir que o produtor rural crie sua conta.

**Passos:**
1. Usuário acessa a tela de cadastro
2. Preenche dados pessoais (nome, email, senha, telefone, CPF/CNPJ,)
3. Sistema valida os dados (`ValidarCadastro.java`)
4. Senha é criptografada com BCrypt (`HashSenha.java`)
5. Dados são persistidos no MongoDB (`CadastroService.java`)
6. Usuário é redirecionado para login

**Classes Java Envolvidas:**
- `CadastroService.java` - Lógica de negócio
- `ValidarCadastro.java` - Validações (email único, CPF válido)
- `HashSenha.java` - Criptografia de senha
- `DBUse.java` - Persistência no MongoDB

---

#### 2. Login e Autenticação
**Objetivo:** Validar credenciais e dar acesso ao sistema.

**Passos:**
1. Usuário insere email e senha
2. Sistema busca usuário no banco (`LoginService.java`)
3. Verifica hash da senha com BCrypt
4. Se válido, cria sessão e redireciona para dashboard
5. Se inválido, retorna erro

**Classes Java Envolvidas:**
- `LoginService.java` - Autenticação
- `HashSenha.java` - Validação de senha

---

#### 3. Registro de Atividades (Caderno de Campo)
**Objetivo:** Permitir que o produtor registre atividades agrícolas realizadas.

**Passos:**
1. Usuário logado acessa "Caderno de Campo"
2. Clica em "Nova Atividade"
3. Preenche: tipo (Plantio/Colheita/Aplicação), descrição, data, observações
4. Sistema valida que todos os campos obrigatórios estão preenchidos
5. Atividade é salva associada ao `usuarioId` (`CadernoDeCampoService.java`)
6. Usuário pode consultar e excluir atividades

**Classes Java Envolvidas:**
- `CadernoDeCampoService.java` - inserir atividade
- `BuscaPorDataAtividadeService.java` - Filtros por data 
- `DBUse.java` - Operações no MongoDB

---

#### 4. Controle de Custos
**Objetivo:** Registrar gastos da propriedade.

**Passos:**
1. Usuário acessa "Custos Registrados"
2. Adiciona novo custo (descrição, valor, data)
3. Pode vincular a uma atividade específica (opcional)
4. Sistema persiste no MongoDB
5. Usuário visualiza histórico de custos

**Classes Java Envolvidas:**
- `CadastroCustoService.java` - Registro de custos
- `BuscaPorDataCustoService.java` - Filtros
- `DBUse.java` - Persistência

---

## 🚀 Como Executar

### Pré-requisitos

- ☕ Java 17 ou superior
- 🍃 Conta MongoDB Atlas (ou instância local)
- 🔧 Git

### 1️⃣ Clone o Repositório

```
git clone https://github.com/bruno-cesar02/PI_IV_ES_TIME-10-Karpos.git
cd PI_IV_ES_TIME-10-Karpos
```

### 2️⃣ Execute o Servidor

#### Compilar

```
javac -cp "servidor/Drivers/*" servidor/*.java servidor/dbConection/*.java comum/*.java
```

#### Executar

**Linux/Mac:**
```
java -cp ".:servidor/Drivers/*" servidor.ServerSemBanco
```

**Windows:**
```
java -cp ".;servidor/Drivers/*" servidor.ServerSemBanco
```

✅ **Saída esperada:**
```
Servidor ouvindo na porta 5050
Conexão MongoDB estabelecida com sucesso!
```

### 3️⃣ Execute o Cliente

#### Compilar

```
javac -cp . cliente/Cliente.java comum/*.java
```

#### Executar

```
java -cp . cliente.Cliente
```

---

## 🧪 Testes, Integração e Dados de Exemplo

> 📝 **[ESPAÇO RESERVADO]**
> 
> - Roteiro de testes
> - Scripts seed para dados de exemplo
> - Instruções de integração
> - Coleção Postman/Insomnia (se aplicável)

---

## 📊 Diagrama de Arquitetura

> 📝 **[ESPAÇO RESERVADO]**
> 
> Diagrama mostrando a relação:
> ```
> Cliente ⇆ Servidor Java ⇆ MongoDB Atlas
> ```

---

## 🎨 Prints/Protótipos

> 📝 **[ESPAÇO RESERVADO]**
> 
> - Link do Canva: https://www.canva.com/design/DAG6RKBvP0c/W-PDYRAD4F52ufEvTBAVFw/edit?utm_content=DAG6RKBvP0c&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton 
> - Fluxos de tela

---

##  Organização

### Gestão do Projeto

- 📋 **Kanban GitHub Projects**
  - Backlog
  - Em Progresso
  - Concluído
- 🎯 **Issues** para cada tarefa/módulo
- 🏷️ **Labels** para categorização

---

> 
> ### Estrutura da Apresentação
> 
> 1. **Problema** - Desafios dos produtores rurais
> 2. **Solução** - Como o Karpós resolve
> 3. **Diferencial** - O que nos destaca
> 4. **Arquitetura** - Visão técnica
> 5. **Demonstração** - Processo implementado
> 6. **Aprendizados** - Lições do projeto
> 7. **Próximos Passos** - Evolução futura

---

## 📚 Referências e Ferramentas

### Tecnologias

- [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) - Banco de dados NoSQL
- [Java SE 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) - Linguagem de programação
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) / [Eclipse](https://www.eclipse.org/) - IDEs

### Documentação

- [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)
- [Java Socket Programming](https://docs.oracle.com/javase/tutorial/networking/sockets/)

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos como parte da disciplina de Projeto Integrador 4.

---

<div align="center">

**Desenvolvido com 💚 pela Equipe Karpós**

PUC Campinas - Engenharia de Software

</div>
