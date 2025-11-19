
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
- [MVP](#-mvp-funcionalidades-principais)
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
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Cliente   │ ◄─────► │ Servidor     │ ◄─────► │  MongoDB    │
│    Java     │  Socket │   Java 17+   │  Driver │   Atlas     │
└─────────────┘         └──────────────┘         └─────────────┘
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
├── 📁 servidor/
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
├── 📁 comum/
│   └── [Classes compartilhadas]
├── 📁 cliente/
│   └── ClienteTeste.java
├── 📁 pages/
│   └── [Protótipos/telas]
└── 📄 README.md
```

---

## 💾 Modelos das Coleções MongoDB

### 👤 Collection: `users`

Armazena dados dos usuários e suas propriedades rurais.

```
{
  "_id": "ObjectId",
  "nome": "string",
  "email": "string",
  "senha": "string (hash BCrypt)",
  "telefone": "string",
  "cpfCnpj": "string",
  "propriedade": {
    "nome": "string",
    "endereco": "string",
    "tamanhoHectares": "double",
    "cultura": "string"
  },
  "criadoEm": "Date",
  "atualizadoEm": "Date"
}
```

**Índices:** `email` (unique), `cpfCnpj` (unique)

---

### 🌱 Collection: `atividades`

Registra atividades agrícolas realizadas na propriedade.

```
{
  "_id": "ObjectId",
  "usuarioId": "ObjectId (ref: users)",
  "tipo": "string (ex: Plantio, Colheita, Aplicação)",
  "descricao": "string",
  "data": "Date",
  "observacoes": "string",
  "criadoEm": "Date",
  "atualizadoEm": "Date"
}
```

**Índices:** `usuarioId`, `data`

---

### 💰 Collection: `custos`

Controla os custos e gastos da propriedade.

```
{
  "_id": "ObjectId",
  "usuarioId": "ObjectId (ref: users)",
  "descricao": "string",
  "valor": "number",
  "data": "Date",
  "atividadeId": "ObjectId (opcional, ref: atividades)",
  "criadoEm": "Date",
  "atualizadoEm": "Date"
}
```

**Índices:** `usuarioId`, `data`

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

### Processo(s) escolhido(s) para MVP

> 📝 **[ESPAÇO RESERVADO]**
> 
> Descrição detalhada do(s) processo(s) implementado(s):
> - Cadastro de usuário
> - Registro de atividades
> - Controle de custos

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
javac -cp . cliente/ClienteTeste.java comum/*.java
```

#### Executar

```
java -cp . cliente.ClienteTeste
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
> - Screenshots da aplicação
> - Link do Figma
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
