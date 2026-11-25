ApiCativo

O ApiCativo é um sistema pra ajudar no dia a dia de uma fazenda de abelhas. A ideia é ter num lugar só as colmeias, os técnicos que cuidam delas, onde cada uma fica e tudo o que já foi feito em cada caixa: revisões, colheitas e outras manutenções.

É um projeto que estou desenvolvendo no Curso Técnico em Informática da Univates.

O que dá pra fazer

Pra entrar no sistema é preciso fazer login. A partir daí dá pra cadastrar, editar, listar e excluir técnicos, setores (as localizações dos apiários), colmeias e os tipos de manejo. No histórico fica registrado qual manejo foi feito em qual colmeia, por qual técnico e em que data. Também tem um relatório com um resumo das colmeias e do último manejo de cada uma.

Com o que foi feito

Java 17 com Swing para as telas, usando o FlatLaf para deixar a interface com um visual mais moderno (tema escuro). O banco é PostgreSQL, acessado via JDBC. O Maven cuida das bibliotecas, dos testes (JUnit 5 e JaCoCo) e da geração do executável.

O código está organizado assim:

modelo/ tem as classes que representam as tabelas do banco
dao/ tem todo o SQL. As telas nunca falam direto com o banco, sempre passam por aqui
controladores/ tem as regras e validações, como a do login
as telas (Tela*.java) ficam no pacote principal, junto com o Apicativo.java, que é por onde o sistema começa
Conexao, ConfiguracaoBanco e InstaladorBanco cuidam de achar o PostgreSQL e deixar o banco pronto
src/test/java tem os testes, no mesmo pacote de cada classe testada
Como rodar

Você vai precisar do Java 17 (ou mais novo), do PostgreSQL e do NetBeans (ou só o Maven, se preferir o terminal).

Não precisa mais criar o banco nem rodar script antes. Na primeira vez que abre, o sistema cria o banco apicativo, as tabelas e o usuário padrão sozinho (o script fica em src/main/resources/banco/ApiCativo.sql e vai junto dentro do .jar). Se quiser rodar o script na mão pelo pgAdmin também dá, e agora pode rodar de novo sem dar erro: ele só cria o que ainda não existe.

Por padrão o sistema tenta conectar com postgres / postgres na porta 5432. Se o PostgreSQL do computador tiver outra senha, aparece uma janela pedindo os dados. Eles ficam salvos no arquivo apicativo.properties, na pasta do usuário (C:\Users\seu-usuario no Windows), e na próxima vez já abre direto.

Aí é só abrir o projeto no NetBeans e apertar F6. Pelo terminal também funciona:

mvn compile exec:java

Pra entrar, use login admin e senha admin.

Se for testar tudo do zero, vai mais fácil nessa ordem: cadastra um setor, um técnico, um manejo, uma colmeia e por último um registro no histórico.

Gerando o executável

No NetBeans, clique com o botão direito no projeto e vá em Clean and Build. Pelo terminal:

mvn clean package

Vai aparecer na pasta target/ o arquivo apicativo.jar, já com todas as bibliotecas dentro (PostgreSQL, FlatLaf...). Pra abrir:

java -jar target/apicativo.jar

Pra levar pra outro computador é só copiar esse arquivo. O outro computador precisa ter Java 17 ou mais novo e o PostgreSQL instalado (ou acesso a um PostgreSQL na rede). No Windows, com o Java instalado, dá pra abrir com dois cliques.

O .jar também sai pronto na CI: no GitHub, aba Actions, abre a última execução verde e baixa o apicativo-jar em Artifacts.

Testes

Os testes ficam em src/test/java e usam JUnit 5:

mvn test

Tem dois tipos:

unitários, que não usam banco: login (ControlaUsuario), hash da senha, datas (ConversorData), combos (ItemCombo), configuração do banco e leitura do script
integração, que testam os DAOs no PostgreSQL de verdade, num banco separado chamado apicativo_teste. O banco de verdade nunca entra nos testes. Cada teste roda numa transação e dá rollback no final, então o banco de teste volta do jeito que estava

O apicativo_teste é criado sozinho. Se o PostgreSQL estiver desligado, os testes de integração aparecem como pulados (skipped) em vez de quebrar o build. Se a senha do seu PostgreSQL não for postgres, dá pra trocar pelas variáveis de ambiente APICATIVO_TESTE_SENHA (e _HOST, _PORTA, _BANCO, _USUARIO).

Cobertura

mvn verify

roda os testes e gera o relatório do JaCoCo em target/site/jacoco/index.html. As classes de regra (controladores, ConversorData, ItemCombo e ConfiguracaoBanco) precisam ter pelo menos 70% das linhas cobertas, senão o build falha. As telas ficam fora da conta.

CI

O arquivo .github/workflows/ci.yml roda o mvn verify a cada push e pull request, com um PostgreSQL só pra CI. Aparece o ✔ ou o ✖ do lado de cada commit.

Como o repositório está organizado

A branch dev é onde o desenvolvimento acontece, e é a padrão. A prod guarda a versão estável e a sandbox é pra testes e ideias maiores que ainda não sei se vão pra frente.

Cada coisa nova é feita numa branch separada (tipo feat/login) e depois entra na dev por Pull Request.

Nas mensagens de commit eu sigo o padrão Conventional Commits: feat pra funcionalidade nova, fix pra correção, docs pra documentação, refactor quando muda a estrutura sem mudar o funcionamento, test pra testes e chore pra configuração e manutenção. Por exemplo:

feat: adiciona tela de login e geração do jar executável