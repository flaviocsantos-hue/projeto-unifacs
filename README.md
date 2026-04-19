# Projeto UNIFACS - Sistema de Gerenciamento de Tarefas

Sistema desktop para gerenciamento de tarefas, projetos e equipes, desenvolvido em **Java** com **Hibernate** e **MySQL**.

## 📋 Pré-requisitos

- [Docker](https://www.docker.com/products/docker-desktop/) e Docker Compose
- Java 11 ou superior
- Maven (opcional, se for compilar manualmente)

---

## 🚀 Como executar o projeto

### 1. Clone o repositório
```bash
git clone [https://github.com/flaviocsantos-hue/projeto-unifacs.git](https://github.com/flaviocsantos-hue/projeto-unifacs.git)
cd projeto-unifacs
```

### 2. Inicie o banco de dados (Docker)
Navegue até a pasta do Docker e suba o container:
```bash
cd docker
docker-compose up -d
```

**O que este comando faz:**
- Cria um container MySQL 8.0.
- Configura a senha root como `root123`.
- Cria o banco de dados `gerenciamento_tarefas`.
- Mapeia a porta 3306 e cria volumes persistentes.

### 3. Execute a aplicação
Na raiz do projeto:
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.sistema.eventos.App"
```

---

## 👥 Credenciais de Teste

O sistema cria automaticamente os seguintes perfis para teste inicial:

| Perfil | Email | Senha | Permissões |
| :--- | :--- | :--- | :--- |
| **Administrador** | admin@email.com | admin123 | Acesso total ao sistema |
| **Gerente** | gerente@email.com | gerente123 | Gerenciar projetos e equipes |
| **Usuário** | joao@email.com | joao123 | Operações básicas |
| **Usuário** | maria@email.com | maria123 | Operações básicas |

---

## 🎯 Funcionalidades

| Status | Funcionalidade | Descrição |
| :--- | :--- | :--- |
| ✅ | **CRUD Completo** | Usuários, Projetos e Tarefas. |
| ✅ | **Equipes** | Gerenciamento de grupos de trabalho. |
| ✅ | **Dashboard** | Métricas e indicadores de desempenho. |
| ✅ | **Segurança** | Controle de permissões (RBAC) e Autenticação. |
| ✅ | **Interface** | GUI Desktop construída com Java Swing. |
| 🚧 | **Relatórios** | Exportação para PDF/Excel (Em breve). |

---

## ⚠️ Checklist de Execução

- [ ] Docker está instalado e rodando.
- [ ] Porta **3306** está livre.
- [ ] Aguardou 15s após o `docker-compose up` para o banco estabilizar.
- [ ] Java 11+ instalado (`java -version`).

---

## 📁 Estrutura do Projeto

```text
projeto-unifacs/
├── docker/              # docker-compose.yaml
├── src/main/java/       # Código fonte Java
│   ├── model/           # Entidades JPA
│   ├── repository/      # Camada de Dados
│   ├── service/         # Lógica de Negócio
│   ├── view/            # Telas Swing
│   └── util/            # Helpers (HibernateUtil)
├── src/main/resources/  # hibernate.properties e logs
└── pom.xml              # Dependências Maven
```