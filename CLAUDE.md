# CLAUDE.md — school-core-service

Este arquivo descreve o escopo do `school-core-service`, parte do projeto DistriSchool
(plataforma de gestão escolar distribuída em microsserviços). Leia este arquivo antes
de gerar qualquer código para este serviço.

## Tarefa atual

Implementar a **Gestão de Técnicos Administrativos** (seção 4 abaixo).

Os módulos 1, 2 e 3 já estão implementados. Não altere o código deles, exceto se for
estritamente necessário para integrar o novo módulo (por exemplo, registrar um novo
tipo de exceção no `ApiExceptionHandler` ou um novo tópico em `KafkaTopics`).

## Escopo deste serviço

O `school-core-service` é responsável pelos seguintes módulos funcionais:

### 1. Gestão de Alunos — implementado
- Cadastro, edição e exclusão de alunos
- Campos: nome, data de nascimento, endereço, contato
- Busca por matrícula, nome ou turma
- Exportação de listas (PDF/CSV)
- Histórico acadêmico integrado
- Upload de documentos
- Requisito não funcional: suportar >10.000 alunos

### 2. Gestão de Professores — implementado
- Cadastro e edição de professores
- Campos: nome, qualificação, contato
- Atribuição de disciplinas/turmas
- Visualização de horários
- Relatórios de desempenho
- Logs de auditoria

### 3. Gestão de Turmas e Horários — implementado
- Criação de turmas/disciplinas
- Atribuição de alunos e professores às turmas
- Calendário escolar, com suporte a múltiplos turnos e feriados
- Detecção de conflitos de horário
- Importação de dados via Excel

### 4. Gestão de Técnicos Administrativos — a implementar
Técnicos administrativos são os funcionários da escola que não dão aula
(secretaria, coordenação, biblioteca, TI, apoio etc.).

**Funcionalidades (CRUD):**
- Cadastro de técnico administrativo
- Consulta de um técnico por ID
- Listagem paginada, com filtros por nome, cargo, setor e situação (ativo/inativo)
- Edição dos dados
- Exclusão lógica: o `DELETE` marca o técnico como inativo em vez de apagar o registro,
  para preservar o histórico. Técnicos inativos não aparecem na listagem por padrão.

**Campos:**
- `nome` (obrigatório)
- `cpf` (obrigatório, único, validado)
- `cargo` (obrigatório; enum: `SECRETARIO`, `COORDENADOR`, `BIBLIOTECARIO`, `TECNICO_TI`, `AUXILIAR_ADMINISTRATIVO`, `OUTRO`)
- `setor` (obrigatório, texto livre)
- `email` (obrigatório, único, validado)
- `telefone`
- `dataAdmissao` (obrigatório, não pode ser futura)
- `ativo` (gerenciado pelo sistema, padrão `true`)

**Regras:**
- Retornar 409 ao tentar cadastrar CPF ou email já existente
- Retornar 404 para técnico inexistente
- Retornar 400 com a lista de campos inválidos em caso de erro de validação
- CPF é dado pessoal (LGPD): não deve aparecer em logs

**Endpoints (`/api/tecnicos`):**
- `POST /api/tecnicos`
- `GET /api/tecnicos` (paginado; filtros `nome`, `cargo`, `setor`, `ativo`)
- `GET /api/tecnicos/{id}`
- `PUT /api/tecnicos/{id}`
- `DELETE /api/tecnicos/{id}` (exclusão lógica)

## Padrões de implementação

Siga os mesmos padrões já usados nos módulos existentes:

- Novo pacote `tecnico/` em `com.distrischool.school_core_service`, com entidade,
  repository, specifications, service, controller e DTOs, como em `aluno/` e `professor/`
- Nova migration Flyway com o próximo número de versão disponível; **nunca** edite
  migrations já existentes
- Índices em `cpf`, `email` e `lower(nome)`
- Erros no formato `ProblemDetail`, via o `ApiExceptionHandler` existente
- Eventos Kafka publicados somente após o commit da transação, em JSON sem
  cabeçalhos de tipo Java, como os eventos já existentes
- Testes de slice web (`@WebMvcTest`) para o controller, no mesmo estilo do teste
  existente de alunos; rode `./mvnw test` ao final

## Fora do escopo deste serviço

Os módulos abaixo pertencem a **outros** serviços e não devem ser implementados aqui:

- Autenticação/autorização de usuários (login, JWT, OAuth2) → serviço de usuários
- Envio de notificações (email, push, avisos) → `notification-service`
- Registro de presenças → serviço futuro
- Notas e avaliações → serviço futuro
- Geração de relatórios analíticos/ML → serviço futuro

## Comunicação com outros serviços

Este serviço publica eventos no Kafka para os seguintes tópicos, que outros serviços
(como o `notification-service`) consomem:

- `student.created` — publicado ao cadastrar um novo aluno
- `teacher.assigned` — publicado ao atribuir um professor a uma turma/disciplina
- `schedule.updated` — publicado ao criar/alterar uma turma ou horário
- `staff.created` — **novo**: publicado ao cadastrar um técnico administrativo
  (payload: id, nome, email, cargo, setor; **sem CPF**), para que o serviço de
  usuários possa criar a conta de acesso

Este serviço **não** consome eventos de outros serviços nesta primeira fase.

## Stack técnica do serviço

- Java 21, Spring Boot
- Spring Web (REST API)
- Spring Data JPA
- PostgreSQL + Flyway (migrações)
- Spring for Apache Kafka (produtor de eventos)
- Spring Boot Actuator