## ApiCativo

Projeto: ApiCativo

Tema: Gerenciamento de apicultura.

Descrição: O projeto é feito pensado no gerenciamento de colmeias e técnicos 
apicultores dentro de uma fazenda de abelhas. Nesse contexto, será criado um 
banco de dados com a possibilidade de controle, registro, atualização, exclusão
e consulta de atividades operacionais. Será desenvolvido com Java, PostgreSQL 
será usado como banco de dados, JDBC será usado para a comunicação dos dados e 
FlatLaf para a interface gráfica.

OBS Branches:
dev: Versão de desenvolvimento, definida como padrão.
prod: Versão estável, main.
sandbox: Versão de testes e planejamentos maiores ou avulsos.

Funcionalidades: Cadastro, atualização, listagem e remoção de técnicos. Cadastro
e monitoramento de apiários e suas localizações. Registro e histórico
de inspeções técnicas e outras ocorrências de manutenção. Relatório com o 
resumo das colmeias e do último manejo realizado.


## Como rodar

Requisitos: Java 21 ou superior, Maven (ou NetBeans) e PostgreSQL.

1. No PostgreSQL, crie o banco de dados:
   CREATE DATABASE apicativo;
2. Execute o script ApiCativo.sql dentro do banco apicativo (pelo pgAdmin, 
   Query Tool). Ele cria as tabelas, a view do histórico e alguns dados de teste.
3. Confira o usuário e a senha do banco na classe Conexao.java 
   (padrão: usuário postgres, senha postgres, porta 5432).
4. Abra o projeto no NetBeans (File > Open Project) e rode com F6, 
   ou pelo terminal:
   mvn compile exec:java

Ordem sugerida para testar: cadastrar setor, técnico, manejo, colmeia e por 
último um registro no histórico.


## Commits

Esse projeto seguirá a convenção Conventional Commits visando mensagens claras 
e padronizadas para fácil identificação futura.
- feat; Nova Funcionalidade.
- fix; Correção de erros.
- docs; Alteração de documentação.
- refactor; Alteração na estrutura do código ou pastas sem mudar funcionalidade.
- test; Criação ou alteração de testes.
- chore; Manutenção e configuração do projeto.
