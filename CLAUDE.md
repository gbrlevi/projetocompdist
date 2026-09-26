# CLAUDE.md — school-core-service

Este arquivo descreve o escopo do `school-core-service`, parte do projeto DistriSchool
(plataforma de gestão escolar distribuída em microsserviços). Leia este arquivo antes
de gerar qualquer código para este serviço.

## Escopo deste serviço

O `school-core-service` é responsável pelos seguintes módulos funcionais:

### 1. Gestão de Alunos
- Cadastro, edição e exclusão de alunos
- Campos: nome, data de nascimento, endereço, contato
- Busca por matrícula, nome ou turma
- Exportação de listas (PDF/CSV)
- Histórico acadêmico integrado
- Upload de documentos
- Requisito não funcional: suportar >10.000 alunos

### 2. Gestão de Professores
- Cadastro e edição de professores
- Campos: nome, qualificação, contato
- Atribuição de disciplinas/turmas
- Visualização de horários
- Relatórios de desempenho
- Logs de auditoria

### 3. Gestão de Turmas e Horários
- Criação de turmas/disciplinas
- Atribuição de alunos e professores às turmas
- Calendário escolar, com suporte a múltiplos turnos e feriados
- Detecção de conflitos de horário
- Importação de dados via Excel

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

Este serviço **não** consome eventos de outros serviços nesta primeira fase.

## Stack técnica do serviço

- Java 21, Spring Boot
- Spring Web (REST API)
- Spring Data JPA
- PostgreSQL + Flyway (migrações)
- Spring for Apache Kafka (produtor de eventos)
- Spring Boot Actuator
