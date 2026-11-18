ApiCativo

O ApiCativo é um sistema pra ajudar no dia a dia de uma fazenda de abelhas. A ideia é ter num lugar só as colmeias, os técnicos que cuidam delas, onde cada uma fica e tudo o que já foi feito em cada caixa: revisões, colheitas e outras manutenções.

É um projeto que estou desenvolvendo no Curso Técnico em Informática da Univates.

O que dá pra fazer

Pra entrar no sistema é preciso fazer login. A partir daí dá pra cadastrar, editar, listar e excluir técnicos, setores (as localizações dos apiários), colmeias e os tipos de manejo. No histórico fica registrado qual manejo foi feito em qual colmeia, por qual técnico e em que data. Também tem um relatório com um resumo das colmeias e do último manejo de cada uma.

Com o que foi feito

Java 21 com Swing para as telas, usando o FlatLaf para deixar a interface com um visual mais moderno (tema escuro). O banco é PostgreSQL, acessado via JDBC. O Maven cuida das bibliotecas e da geração do executável.

O código está organizado assim:

modelo/ tem as classes que representam as tabelas do banco
dao/ tem todo o SQL. As telas nunca falam direto com o banco, sempre passam por aqui
controladores/ tem as regras e validações, como a do login
as telas (Tela*.java) ficam no pacote principal, junto com o Apicativo.java, que é por onde o sistema começa
Como rodar

Você vai precisar do Java 21 (ou mais novo), do PostgreSQL e do NetBeans (ou só o Maven, se preferir o terminal).

Primeiro crie o banco no PostgreSQL:

sql
CREATE DATABASE apicativo;

Depois abra o pgAdmin, entre no banco apicativo e rode o script ApiCativo.sql pela Query Tool. Ele cria as tabelas, já coloca alguns dados pra teste e cria o usuário padrão.

Dá uma olhada no Conexao.java pra ver se o usuário e a senha do banco batem com os do seu PostgreSQL. Por padrão estão postgres / postgres na porta 5432.

Aí é só abrir o projeto no NetBeans e apertar F6. Pelo terminal também funciona:

mvn compile exec:java

Pra entrar, use login admin e senha admin.

Se for testar tudo do zero, vai mais fácil nessa ordem: cadastra um setor, um técnico, um manejo, uma colmeia e por último um registro no histórico.

Gerando o executável

No NetBeans, clique com o botão direito no projeto e vá em Clean and Build. Pelo terminal:

mvn clean package

Vai aparecer na pasta target/ o arquivo apicativo-1.0-SNAPSHOT.jar e uma pasta lib/ com as bibliotecas que o sistema usa. Pra abrir:

java -jar target/apicativo-1.0-SNAPSHOT.jar

Se for levar pra outro computador, copie o .jar e a pasta lib/ juntos. Sem a lib/ ele não abre. O outro computador também precisa ter Java e acesso ao banco.

Como o repositório está organizado

A branch dev é onde o desenvolvimento acontece, e é a padrão. A prod guarda a versão estável e a sandbox é pra testes e ideias maiores que ainda não sei se vão pra frente.

Cada coisa nova é feita numa branch separada (tipo feat/login) e depois entra na dev por Pull Request.

Nas mensagens de commit eu sigo o padrão Conventional Commits: feat pra funcionalidade nova, fix pra correção, docs pra documentação, refactor quando muda a estrutura sem mudar o funcionamento, test pra testes e chore pra configuração e manutenção. Por exemplo:

feat: adiciona tela de login e geração do jar executável