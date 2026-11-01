# Mapeamento de mudanças: Projeto 4º Bimestre (PetShop com JDBC + JOptionPane)

> Este documento substitui o `MAPEAMENTO_PROJETO_PETSHOP.md` (3º bimestre). O anterior proibia `try/catch`, `JOptionPane` e banco de dados, e agora os três são **obrigatórios**. Ele deve ser apagado ou marcado como histórico para não confundir o grupo.

---

## 0. Fontes usadas e limites

**Enunciado:** `4o BIM POO II - Projeto.pdf`.

**Aulas (únicas fontes técnicas permitidas):**

| Aula | O que ela fornece ao projeto |
|---|---|
| Classes Essenciais (Math, Integer, String, JOptionPane) | `Integer.parseInt`; métodos de `String` (`length`, `equals`, `equalsIgnoreCase`, `trim`, `codePointAt`, `charAt`, `substring`, `indexOf`, `toUpperCase`...); proibição de comparar `String` com `==`; `showMessageDialog` com 2 e 4 parâmetros; tipos `PLAIN/ERROR/INFORMATION/WARNING/QUESTION_MESSAGE`; `showConfirmDialog` e constantes `YES_OPTION`, `NO_OPTION`, `CANCEL_OPTION`, `CLOSED_OPTION`; `showOptionDialog` com 8 parâmetros (retorna o índice do botão, ou -1 se a janela for fechada); `showInputDialog` (retorna `null` no Cancelar/fechar, `""` se confirmar vazio; a versão com lista de opções retorna `Object` e exige coerção para `String`); teste de `null` e de vazio antes de converter |
| Aula 8: Tratamento de Exceções | `try`, `catch` (um por tipo ou um genérico `Exception`), `finally`; `Exception` (obrigatória tratar/propagar) × `RuntimeException`; laço `do...while` com variável `ok` para repetir a entrada quando `parseInt` falha; relançar exceção dentro do `catch`; `throw new Exception("texto")`; cláusula `throws`; **criar uma exceção própria** herdando de `Exception`, com construtor sem argumentos, construtor com texto chamando `super(texto)`, e podendo ter atributos e métodos; `IllegalArgumentException` |
| Aula 9: Acesso a Arquivos | **try-with-resources**, que fecha recursos "como arquivos ou conexões" automaticamente; laço for-each. (Leitura/escrita de arquivos **não** é exigida pelo projeto.) |
| Aula 10: Banco de Dados | JDBC; pacotes `java.sql`; `DriverManager`, `Connection`, `Statement`, `ResultSet`, `SQLException`; URL `jdbc:mysql://localhost:3306/<banco>`; usuário/senha; `Class.forName("com.mysql.cj.jdbc.Driver")` tratando `ClassNotFoundException`; `executeQuery` para SELECT; `while (resultado.next())`; `getInt("coluna")` / `getString("coluna")`; fechar `ResultSet`/`Statement`; capturar `SQLException` |

### Recursos que NÃO devem ser usados (não aparecem nas aulas)
`ArrayList`/coleções, `instanceof`, `enum`, regex, lambdas/streams, interfaces criadas pelo grupo, pacotes (`package`), `DataSource`/pool de conexões (citado só como conceito), frameworks/ORM, `JFrame` ou outros componentes Swing além de `JOptionPane`, `Scanner`/`System.out` como interface com o usuário.

### Lacunas: itens exigidos pelo enunciado, mas sem detalhamento no texto dos slides
1. **`PreparedStatement`**: o enunciado exige ("Métodos CRUD ... com PreparedStatement"), mas o texto da Aula 10 só mostra `Statement` + `executeQuery`. O uso é obrigatório pelo enunciado. Basta o mínimo: SQL com `?` no lugar dos valores, preencher cada `?` por posição com o método do tipo correspondente (inteiro ou texto), `executeQuery` para SELECT e o método de atualização para INSERT/UPDATE/DELETE.
2. **Comandos SQL** (criar tabela, INSERT, UPDATE, DELETE): a aula só mostra `SELECT * FROM alunos`. A tabela abaixo é a mais simples possível.
3. **Instalação do driver MySQL (Connector/J)** no IntelliJ: a aula só diz que "é necessário garantir que o JDBC conheça o SGBD".
4. Os PDFs têm **exemplos de código em imagem** que não puderam ser lidos (só o texto foi extraído). Conferir nos slides a forma exata dos exemplos de `catch` do `parseInt` e do `showInputDialog` com lista.

---

## 1. Diagnóstico: requisito do enunciado × estado atual × ação

| # | Requisito do PDF | Estado atual do código | Ação |
|---|---|---|---|
| 1 | `Animal` abstrata com `nome`, `idade`, `nomeDono`, `telefoneDono` e `public abstract String emitirSom()` | **Atende** (`src/Animal.java`) | Manter. Trocar `exibirDadosComuns()` (usa `System.out`) por um método que **retorna `String`** |
| 2 | `Cachorro` com `porte`, retorna `"Au Au!"` | **Atende** | Manter; só ajustar métodos de exibição se houver |
| 3 | `Gato` com `temperamento`, retorna `"Miau!"` | **Atende** | Manter |
| 4 | `Consulta` com `Animal paciente`, **`horarioIndex`** e `sintomas` | Atributo chama-se `horario` e vai de **1 a 10** | Renomear para `horarioIndex`, faixa **0 a 9**; trocar `exibirDados()` (imprime no console e depende do `AgendaManager`) por métodos que retornam `String` |
| 5 | Substituir estrutura em memória por JDBC | `AgendaManager` guarda tudo num vetor estático `Consulta[10]`; dados somem ao fechar | Remover o vetor como armazenamento; persistir no MySQL |
| 6 | `ConexaoBD` com `DriverManager` / driver MySQL | **Não existe** | Criar |
| 7 | `AgendaDAO` com CRUD (`salvar`, `buscarAgenda`, `cancelar`) via `PreparedStatement` | **Não existe** | Criar (+ `atualizar`, por causa do requisito "alterar") |
| 8 | Arquitetura em camadas com DAO | Tudo misturado: `AgendaManager` faz regra + armazenamento + impressão; `Main` faz interface + validação | Separar em Interface → Regras → DAO → Conexão (seção 2) |
| 9 | Menu com `showOptionDialog`: Agendar, Ver Agenda, Cancelar, Sair | Menu de console com `Scanner` (opções 1-4 e 0) | Reescrever com `showOptionDialog` |
| 10 | Entradas com `showInputDialog` + `Integer.parseInt` na idade | Entradas com `Scanner.nextLine()`; validação manual de dígitos antes do `parseInt` | Reescrever com `showInputDialog`; trocar a validação manual por `try/catch` da aula 8 |
| 11 | Listagem dos 10 horários com `showMessageDialog`; `[Disponível]` ou nome, espécie, tutor e som | `listar()` imprime no console | Montar um único texto com 10 linhas e exibir com `showMessageDialog` |
| 12 | `ERROR_MESSAGE`/`WARNING_MESSAGE` para sobreposição, agenda lotada e cancelar horário vazio | Mensagens via `System.out.println` | Exceção própria + `showMessageDialog` com o ícone certo (seção 6) |
| 13 | Capturar `SQLException` | Não há banco nem `try/catch` | Propagar com `throws` no DAO e capturar na interface |
| 14 | "Alterar dados de agendamentos existentes" (texto do problema) | Existe como "Editar" no console | Manter como opção de menu, usando UPDATE (ver ponto a confirmar 12.1) |
| 15 | Driver MySQL disponível ao projeto | `ProjetoPOO3bim.iml` sem bibliotecas | Adicionar o `.jar` do Connector/J como dependência do módulo |
| 16 | Banco e tabela criados | Não existem | Criar banco + tabela; versionar script `.sql` no repositório |

---

## 2. Nova arquitetura em camadas

```text
 CAMADA DE INTERFACE        Main  (somente JOptionPane: menu, entradas, mensagens)
        │  chama / captura AgendaException e SQLException
        ▼
 CAMADA DE REGRAS           AgendaManager  (sem vetor de armazenamento; valida índice,
        │                   lotação, sobreposição, horário vazio; lança AgendaException)
        ▼
 CAMADA DE PERSISTÊNCIA     AgendaDAO  (SQL com PreparedStatement; monta objetos a partir do ResultSet)
        │                   ConexaoBD  (URL, usuário, senha, Class.forName, DriverManager)
        ▼
                            MySQL: banco "petshop", tabela "consultas"

 CAMADA DE MODELO           Animal (abstrata) ◄── Cachorro / Gato
 (usada por todas)          Consulta ──possui──► Animal paciente
                            AgendaException (nova exceção própria)
```

Regras de dependência:
- **Somente `Main`** importa e usa `JOptionPane`.
- **Somente `ConexaoBD` e `AgendaDAO`** escrevem SQL e usam `Connection`/`PreparedStatement`/`ResultSet`.
- **Nenhuma classe** usa `System.out` ou `Scanner`.
- As classes de modelo não conhecem banco nem interface.

Arquivos finais em `src/`: `Animal`, `Cachorro`, `Gato`, `Consulta`, `AgendaException` (novo), `ConexaoBD` (novo), `AgendaDAO` (novo), `AgendaManager` (reformulado), `Main` (reescrito). Continuam no pacote padrão (sem `package`), como hoje.

---

## 3. Banco de dados (MySQL)

### 3.1 Banco
- Nome sugerido: `petshop`.
- URL no padrão da aula: protocolo `jdbc:mysql://`, servidor `localhost`, porta `3306`, nome do banco.
- Usuário e senha do MySQL local de quem for apresentar (ex.: `root`, como na aula).

### 3.2 Tabela `consultas` (uma linha = um horário ocupado)

| Coluna | Tipo sugerido | Obrigatória | Origem no Java | Observação |
|---|---|---|---|---|
| `horario_index` | inteiro | sim, **chave primária** | `Consulta.getHorarioIndex()` | 0 a 9. Sendo chave primária, o próprio banco recusa dois agendamentos no mesmo horário (segunda barreira contra sobreposição; o erro chega como `SQLException`) |
| `especie` | texto curto | sim | `paciente.getTipo()` | `"Cachorro"` ou `"Gato"`; é por ela que o DAO sabe qual subclasse recriar |
| `nome_animal` | texto | sim | `getNome()` | |
| `idade` | inteiro | sim | `getIdade()` | |
| `nome_dono` | texto | sim | `getNomeDono()` | |
| `telefone_dono` | texto (até 11) | sim | `getTelefoneDono()` | Manter como texto para preservar zero à esquerda |
| `caracteristica` | texto | sim | porte (cachorro) ou temperamento (gato) | Uma coluna só, preenchida de forma polimórfica (ver 4.1) |
| `sintomas` | texto | sim | `Consulta.getSintomas()` | |

**Horário livre = não existe linha** com aquele `horario_index`. Cancelar = **apagar a linha** (equivale ao antigo `agenda[i] = null`).

### 3.3 Script versionado
Criar na raiz um arquivo `banco.sql` com a criação do banco e da tabela, para que qualquer integrante (ou a professora) recrie o ambiente antes da apresentação.

### 3.4 Driver
- Baixar o MySQL Connector/J (`.jar`), colocar numa pasta `lib/` do projeto.
- No IntelliJ: *Project Structure → Modules → Dependencies → adicionar o .jar*. Isso altera o `ProjetoPOO3bim.iml` automaticamente.
- Sem esse passo, `Class.forName("com.mysql.cj.jdbc.Driver")` lança `ClassNotFoundException`.

---

## 4. Mudanças por arquivo

### 4.1 `Animal.java` (ajustes pequenos)
Manter:
- `abstract`, os 4 atributos `private`, construtor, getters/setters com validação, `telefoneValido` (usa `codePointAt`, conteúdo da aula de String), `dadosComunsValidos`, `dadosValidos`;
- `public abstract String emitirSom()` (exatamente como o PDF exige);
- `public abstract String getTipo()`: usado na listagem ("espécie") e gravado na coluna `especie`.

Mudar:
- **Remover `exibirDadosComuns()`** (imprime com `System.out`). Substituir por um método que **retorna** uma `String` com nome, idade, tutor e telefone, para ser exibida pelo `JOptionPane`.
- **Adicionar `public abstract String getCaracteristica()`**: retorna só o valor puro do atributo específico (porte ou temperamento). Assim o DAO grava a coluna `caracteristica` chamando o método pela referência `Animal`, **sem `instanceof` e sem coerção**, o que é mais um uso de polimorfismo de sobrescrita.
- `getInformacaoEspecifica()` pode continuar (retorna "Porte: X" / "Temperamento: Y") para exibição.

### 4.2 `Cachorro.java`
- Manter `porte`, `setPorte` com `equalsIgnoreCase`, `emitirSom()` = `"Au Au!"`, `getTipo()` = `"Cachorro"`, `dadosValidos()`, regra de idade 0-29 (extra do grupo, pode ficar).
- Adicionar `@Override getCaracteristica()` retornando o porte.

### 4.3 `Gato.java`
- Manter `temperamento`, `emitirSom()` = `"Miau!"`, `getTipo()` = `"Gato"`, `dadosValidos()`, regra 0-38.
- Adicionar `@Override getCaracteristica()` retornando o temperamento.

### 4.4 `Consulta.java`
- Renomear atributo `horario` → **`horarioIndex`** (nome dado no PDF), com getter/setter correspondentes.
- Faixa válida passa a ser **0 a 9** (no setter e em `dadosValidos()`).
- **Remover `exibirDados()`** (usa `System.out` e chama `AgendaManager.getHorarioTexto`, o que cria dependência do modelo com a camada de regras).
- Criar dois métodos que **retornam `String`**:
  - **resumo de uma linha** para a agenda: nome do animal, espécie (`paciente.getTipo()`), tutor e som (`paciente.emitirSom()`);
  - **detalhado** para confirmar alteração/cancelamento: dados comuns, característica, sintomas e som.
- Esses métodos chamam `getTipo()` e `emitirSom()` **pela referência `Animal`**, que em tempo de execução aponta para `Cachorro` ou `Gato`. É o "polimorfismo de tipo" que o PDF pede para a classe `Consulta`.

### 4.5 `AgendaException.java` (NOVO, conteúdo da Aula 8 "Declarando um tipo de exceção")
- Herda de `Exception` (logo o compilador obriga a tratar ou propagar).
- Construtor **sem argumentos** (recomendado pela aula) e construtor com **texto**, que chama `super(texto)`.
- Atributo extra (a aula permite atributos em exceções), por exemplo um indicador `aviso` (verdadeiro/falso) com getter. A interface usa esse indicador para escolher `WARNING_MESSAGE` (aviso) ou `ERROR_MESSAGE` (erro), sem que a camada de regras precise conhecer `JOptionPane`.
- Será lançada com `throw new AgendaException(...)` e declarada com `throws` nos métodos do `AgendaManager`.

### 4.6 `ConexaoBD.java` (NOVO, roteiro da Aula 10, passos 1 a 4)
- Imports de `java.sql` (`Connection`, `DriverManager`, `SQLException`).
- Constantes privadas: URL, usuário, senha.
- Um método `static` que **retorna uma `Connection`**:
  1. carrega o driver com `Class.forName("com.mysql.cj.jdbc.Driver")`;
  2. trata `ClassNotFoundException` em `try/catch`, como pede a aula. Dentro do `catch`, **lança uma nova `SQLException`** com texto personalizado ("Driver MySQL não encontrado"), conforme "Lançando uma exceção" da Aula 8, para que o resto do sistema só precise lidar com `SQLException`;
  3. cria e devolve a conexão com `DriverManager.getConnection(url, usuario, senha)`;
  4. declara `throws SQLException`.
- Não guardar a conexão aberta num atributo: cada operação do DAO abre e fecha a sua.

### 4.7 `AgendaDAO.java` (NOVO, persistência)
Todos os métodos:
- obtêm a conexão em `ConexaoBD`;
- usam **`PreparedStatement`** com `?` para todos os valores digitados pelo usuário (nunca concatenar texto do usuário dentro do SQL);
- abrem `Connection`, `PreparedStatement` e `ResultSet` em **try-with-resources** (Aula 9: fecha "arquivos ou conexões" automaticamente, mesmo com erro), em vez de `close()` manual;
- **declaram `throws SQLException`**, sem capturar (quem decide a mensagem é a interface).

| Método | SQL | Parâmetros / retorno | Detalhes |
|---|---|---|---|
| `salvar(Consulta)` | INSERT de todas as colunas | recebe a consulta; nada a retornar | `especie` vem de `getTipo()`, `caracteristica` de `getCaracteristica()`, ambos pela referência `Animal` |
| `buscarAgenda()` | SELECT de todas as linhas, ordenado por `horario_index` | **retorna um vetor `Consulta[10]`** | Percorrer com `while (resultado.next())`; ler colunas com `getInt`/`getString`; se `especie` for `"Cachorro"` (comparar com `equals`/`equalsIgnoreCase`, nunca `==`) criar `Cachorro`, senão `Gato`; guardar numa variável do tipo **`Animal`**; criar a `Consulta` e colocá-la na posição `horario_index` do vetor. Posições sem linha ficam `null` = livres |
| `atualizar(Consulta)` | UPDATE de todas as colunas exceto a chave, com filtro por `horario_index` | recebe a consulta já alterada | Mantém o mesmo horário |
| `cancelar(int horarioIndex)` | DELETE filtrando por `horario_index` | retorna a quantidade de linhas apagadas (`int`) | Se retornar 0, o horário já estava vazio (segunda verificação) |

Por que `buscarAgenda()` retorna um vetor de 10 posições: vetor e `null` já são dominados pelo grupo (3º bimestre), `ArrayList` não está nas aulas, e o índice do vetor coincide com o índice do horário, o que simplifica as regras de lotação e sobreposição. O vetor agora é só uma **leitura temporária** do banco, não o armazenamento.

### 4.8 `AgendaManager.java` (REFORMULAR: vira camada de regras)
Remover:
- o atributo `private static Consulta[] agenda` como armazenamento;
- todos os `System.out.println`;
- `listar()`, `listarHorarios()`, `exibirConsulta()` na forma atual (imprimem).

Manter:
- o vetor constante de textos dos horários (`"9:00"`, `"9:30"`, ...) e `getHorarioTexto(int)`;
- `indiceValido(int)`.

Novos/reescritos (todos `static`, como hoje; declaram `throws AgendaException, SQLException` quando acessam o banco):

| Método | Regra |
|---|---|
| `verificarAgendaNaoLotada()` | Busca a agenda no DAO; se as 10 posições estiverem ocupadas, lança `AgendaException` de **aviso** ("Agenda lotada"). A interface chama **antes** de pedir os dados |
| `verificarHorarioLivre(int)` | Índice fora de 0-9: exceção de **erro**. Posição ocupada: exceção de **erro** ("Horário já ocupado", impede a sobreposição) |
| `agendar(Consulta)` | Repete as verificações acima (o banco pode ter mudado), confere `dadosValidos()`, chama `AgendaDAO.salvar` |
| `buscarConsultaOcupada(int)` | Índice inválido: erro. Posição vazia: exceção de **aviso** ("Horário já está vazio"). Senão retorna a consulta (para mostrar os dados antes de alterar/cancelar) |
| `alterar(Consulta)` | Confere que o horário está ocupado e os dados válidos; chama `AgendaDAO.atualizar` |
| `cancelar(int)` | Horário vazio: exceção de **aviso** (exigência do PDF). Senão chama `AgendaDAO.cancelar` |
| `montarTextoAgenda()` | Busca a agenda e devolve **uma `String`** com as 10 linhas: `índice - hora - [Disponível]` ou `índice - hora - ` + resumo da consulta (nome, espécie, tutor, som). Se nenhuma estiver ocupada, acrescentar "Nenhum atendimento agendado" |
| `montarTextoHorariosLivres()` | `String` só com os horários livres, para usar dentro da mensagem do `showInputDialog` do horário |

### 4.9 `Main.java` (REESCREVER a interface)
Remover:
- `import java.util.Scanner`, o atributo `scanner`, todo `System.out`;
- `exibirMenu()` de console;
- a validação manual de dígitos em `lerInteiroEntre` (era um contorno porque `try/catch` não tinha sido ensinado). `somenteDigitos` só continua necessário se for usado no telefone; o `Animal` já valida o telefone, então pode sair do `Main`.

Adicionar:
- `import javax.swing.JOptionPane` (obrigatório, segundo a aula) e `import java.sql.SQLException`.

Métodos auxiliares sugeridos (todos `private static`):

| Auxiliar | Componente | Comportamento |
|---|---|---|
| mostrar informação / aviso / erro | `showMessageDialog` com 4 parâmetros (`null`, mensagem, título, tipo) | Um auxiliar por tipo: `INFORMATION_MESSAGE`, `WARNING_MESSAGE`, `ERROR_MESSAGE` |
| ler texto obrigatório | `showInputDialog` | Se retornar `null` (Cancelar/fechar) → devolve `null` para o fluxo abortar. Se após `trim()` tiver `length() == 0` → erro "campo não pode ficar vazio" e pergunta de novo |
| ler inteiro entre mín. e máx. | `showInputDialog` + `Integer.parseInt` | Padrão da Aula 8: `do...while` com variável `ok`; `parseInt` dentro do `try`; no `catch` da exceção de conversão (`NumberFormatException`, ou `Exception` genérico como no slide) mostra `ERROR_MESSAGE` e repete; fora da faixa → erro e repete; `null` → cancelamento (usar um valor sentinela, ex. -1) |
| escolher em lista | `showInputDialog` na versão com vetor de opções | Retorna `Object`, então fazer **coerção para `String`** (exemplo da "cidade" na aula); `null` = cancelou. Usar para porte (Pequeno/Médio/Grande) e temperamento (Dócil/Arredio) |
| escolher espécie | `showOptionDialog` com botões "Cachorro" e "Gato" | Retorno 0, 1, ou -1 se fechou |
| ler paciente | combina os anteriores | Espécie → nome → idade (`parseInt`, faixa 0-29 ou 0-38) → tutor → telefone → porte/temperamento. Retorna `Animal` (que aponta para `Cachorro` ou `Gato`) ou `null` se cancelou em qualquer etapa |
| confirmar | `showConfirmDialog` com `YES_NO_OPTION` | Verdadeiro apenas se o retorno for igual a `JOptionPane.YES_OPTION` |

---

## 5. Fluxos com JOptionPane

### 5.1 Início do programa
1. Antes do menu, chamar `AgendaManager.montarTextoAgenda()` (ou qualquer leitura) dentro de `try/catch (SQLException)` para **testar a conexão**.
2. Se falhar: `ERROR_MESSAGE` "Não foi possível conectar ao banco de dados" + `e.getMessage()`, e encerrar.

### 5.2 Menu principal (`showOptionDialog`)
- 8 parâmetros, na ordem da aula: `null`, mensagem, título ("Clínica Veterinária"), `DEFAULT_OPTION`, `QUESTION_MESSAGE`, ícone `null`, vetor de rótulos, botão inicial.
- Rótulos: **"Agendar Consulta", "Ver Agenda Diária", "Cancelar Agendamento", "Alterar Agendamento", "Sair"** (ver 12.1).
- O retorno é o índice do botão; **-1 (fechou no X) deve ser tratado como Sair**.
- Repetir com `do...while` até Sair.
- Cada opção executa dentro de um `try` com `catch (AgendaException)` e `catch (SQLException)` (seção 6), para que nenhum erro derrube o programa; após o erro, volta ao menu.

### 5.3 Agendar Consulta
1. `verificarAgendaNaoLotada()`. Se lotada: `WARNING_MESSAGE` e volta ao menu, **sem pedir nenhum dado**.
2. Pedir o horário (0 a 9) com `showInputDialog`, mostrando na mensagem a lista de horários livres; converter com `parseInt` tratado.
3. `verificarHorarioLivre(índice)`. Se ocupado: `ERROR_MESSAGE` "Horário já ocupado" (sobreposição) e pedir outro ou voltar.
4. Ler paciente (espécie, nome, idade com `Integer.parseInt`, tutor, telefone, porte/temperamento).
5. Ler sintomas.
6. Criar `Consulta(paciente, horarioIndex, sintomas)`.
7. Mostrar o texto detalhado e confirmar com `showConfirmDialog`.
8. Sim: `AgendaManager.agendar(...)` e `INFORMATION_MESSAGE` "Consulta agendada". Não: `INFORMATION_MESSAGE` "Operação cancelada".
9. Cancelar/fechar em qualquer `showInputDialog`: abortar e voltar ao menu.

### 5.4 Ver Agenda Diária
- `showMessageDialog` com o texto de `montarTextoAgenda()` (linhas separadas por quebra de linha), título "Agenda Diária", `INFORMATION_MESSAGE` ou `PLAIN_MESSAGE`.
- Exemplo de linha livre: `3 - 10:30 - [Disponível]`.
- Exemplo de linha ocupada: `0 - 9:00 - Rex | Cachorro | Tutor: Ana | Som: Au Au!`.

### 5.5 Alterar Agendamento
1. Mostrar agenda; pedir o índice.
2. `buscarConsultaOcupada(índice)`. Se vazio: `WARNING_MESSAGE`.
3. Mostrar os dados atuais (texto detalhado).
4. Ler novamente paciente + sintomas (mesmo horário).
5. Confirmar; Sim: `alterar(...)` (UPDATE) e mensagem de sucesso. Não ou Cancelar: nada muda no banco.

### 5.6 Cancelar Agendamento
1. Mostrar agenda; pedir o índice.
2. Se o horário estiver vazio: **`WARNING_MESSAGE`** "Este horário já está vazio" (exigência explícita do PDF).
3. Mostrar a consulta que será removida; `showConfirmDialog`.
4. Sim: `cancelar(...)` (DELETE) e "Horário liberado".

### 5.7 Sair
- Mensagem de despedida (opcional) e fim do laço.

---

## 6. Mapa de tratamento de exceções

| Situação | Onde nasce | Tipo | Como chega à interface | Onde é capturada | Diálogo |
|---|---|---|---|---|---|
| Texto não numérico na idade/horário | `Integer.parseInt` no `Main` | `NumberFormatException` (`RuntimeException`) | direto | no próprio auxiliar de leitura, com `do...while` | `ERROR_MESSAGE` e repete |
| Número fora da faixa | auxiliar de leitura | (verificação com `if`) | | | `ERROR_MESSAGE` e repete |
| Campo vazio | auxiliar de leitura | (verificação com `if`) | | | `ERROR_MESSAGE` e repete |
| Cancelar/fechar um `showInputDialog` | retorno `null` | (não é exceção) | | fluxo da opção | volta ao menu, opcional `INFORMATION_MESSAGE` |
| Agenda lotada | `AgendaManager` | `AgendaException` (aviso) | `throw` + `throws` | `catch` no laço do menu | **`WARNING_MESSAGE`** |
| Horário ocupado (sobreposição) | `AgendaManager` | `AgendaException` (erro) | `throw` + `throws` | idem | **`ERROR_MESSAGE`** |
| Cancelar/alterar horário vazio | `AgendaManager` | `AgendaException` (aviso) | `throw` + `throws` | idem | **`WARNING_MESSAGE`** |
| Índice fora de 0-9 | `AgendaManager` | `AgendaException` (erro) | `throw` + `throws` | idem | `ERROR_MESSAGE` |
| Driver ausente | `ConexaoBD` | `ClassNotFoundException`, **relançada como `SQLException`** | `throws` | idem | `ERROR_MESSAGE` |
| Banco desligado, senha errada, SQL inválido, chave duplicada | `ConexaoBD`/`AgendaDAO` | `SQLException` | `throws` em DAO e Manager | idem | `ERROR_MESSAGE` com `e.getMessage()` |

Princípios (Aula 8): a classe que conhece a regra **lança**; a interface, que conhece o usuário, **captura e informa**. O fechamento de recursos fica garantido pelo try-with-resources (Aula 9), o que dispensa `finally` manual.

---

## 7. Catálogo de mensagens

| Mensagem | Tipo |
|---|---|
| "A agenda está lotada. Não há horários disponíveis." | WARNING |
| "Este horário já está ocupado. Escolha outro." | ERROR |
| "Este horário já está vazio. Não há consulta para cancelar." | WARNING |
| "Não existe consulta neste horário para alterar." | WARNING |
| "Horário inválido. Digite um número de 0 a 9." | ERROR |
| "Idade inválida. Digite somente números." | ERROR |
| "Idade fora do limite (cachorro 0-29 / gato 0-38)." | ERROR |
| "Este campo não pode ficar vazio." | ERROR |
| "Telefone inválido. Digite de 8 a 11 números." | ERROR |
| "Erro ao acessar o banco de dados: " + detalhe | ERROR |
| "Consulta agendada com sucesso." | INFORMATION |
| "Consulta alterada com sucesso." | INFORMATION |
| "Consulta cancelada. Horário liberado." | INFORMATION |
| "Operação cancelada. Nenhum dado foi alterado." | INFORMATION |

---

## 8. Ordem de implementação recomendada

1. Instalar/ligar o MySQL, criar banco e tabela; salvar o script `banco.sql`.
2. Adicionar o Connector/J ao módulo no IntelliJ.
3. Criar `ConexaoBD` e testar uma conexão isolada.
4. Ajustar o modelo: `getCaracteristica()` em `Animal`/`Cachorro`/`Gato`; `horarioIndex` 0-9 em `Consulta`; métodos que retornam `String` no lugar dos que imprimem.
5. Criar `AgendaException`.
6. Criar `AgendaDAO`: primeiro `salvar` e `buscarAgenda` (testar gravando e lendo), depois `cancelar` e `atualizar`.
7. Reformular `AgendaManager` como camada de regras.
8. Reescrever `Main` com `JOptionPane`: auxiliares, depois menu, depois Agendar → Ver → Cancelar → Alterar.
9. Rodar a matriz de testes (seção 10).
10. Apagar/arquivar o mapeamento do 3º bimestre e revisar o checklist (seção 11).

---

## 9. Divisão sugerida para o trio

| Integrante | Entrega | Revisão cruzada |
|---|---|---|
| 1 | Banco (`banco.sql`), driver, `ConexaoBD`, `AgendaDAO` | Conferir `PreparedStatement`, try-with-resources, reconstrução Cachorro/Gato a partir do `ResultSet` |
| 2 | Ajustes em `Animal`/`Cachorro`/`Gato`/`Consulta`, `AgendaException`, `AgendaManager` | Conferir polimorfismo (`emitirSom`, `getTipo`, `getCaracteristica`) e regras de lotação/sobreposição/vazio |
| 3 | `Main` com `JOptionPane` e tratamento de entradas | Conferir tipos de diálogo, `parseInt` com `try/catch`, Cancelar em todos os passos |

Todos precisam saber explicar o fluxo completo de uma consulta: digitada no `JOptionPane` → objeto `Cachorro`/`Gato` → `Consulta` → regra → DAO → MySQL → lida de volta → som polimórfico na tela.

---

## 10. Matriz de testes de aceitação

| Nº | Cenário | Resultado esperado |
|---:|---|---|
| 1 | Iniciar com MySQL desligado | `ERROR_MESSAGE` de conexão; programa não trava |
| 2 | Iniciar sem o driver no projeto | `ERROR_MESSAGE` "driver não encontrado" |
| 3 | Ver agenda com tabela vazia | 10 linhas `[Disponível]` + aviso de nenhum atendimento |
| 4 | Agendar cachorro válido | Linha gravada; agenda mostra nome, Cachorro, tutor, `Au Au!` |
| 5 | Agendar gato válido | Agenda mostra Gato e `Miau!` |
| 6 | **Fechar e reabrir o programa** | Agendamentos continuam lá (prova da persistência) |
| 7 | Agendar em horário ocupado | `ERROR_MESSAGE`; registro antigo intacto |
| 8 | Horário com letras / vazio / 10 / -1 | `ERROR_MESSAGE` e nova pergunta |
| 9 | Idade com letras (ex. "dois") | Exceção do `parseInt` capturada; `ERROR_MESSAGE`; pergunta de novo |
| 10 | Idade acima do limite da espécie | `ERROR_MESSAGE` |
| 11 | Telefone com letras ou tamanho errado | `ERROR_MESSAGE` |
| 12 | Clicar Cancelar no meio do cadastro | Volta ao menu; nada gravado |
| 13 | Responder "Não" na confirmação | Nada gravado |
| 14 | Preencher os 10 horários e tentar o 11º | `WARNING_MESSAGE` de lotada **antes** de pedir dados |
| 15 | Cancelar horário vazio | `WARNING_MESSAGE` |
| 16 | Cancelar horário ocupado | Linha apagada; horário volta a `[Disponível]` |
| 17 | Reagendar no horário liberado | Aceito |
| 18 | Alterar horário vazio | `WARNING_MESSAGE` |
| 19 | Alterar horário ocupado (trocar Cachorro por Gato) | Agenda passa a mostrar Gato e `Miau!` no mesmo horário |
| 20 | Fechar o menu no X | Programa encerra |
| 21 | Nome com `'` ou aspas (ex. "D'Artagnan") | Grava normalmente (efeito do `PreparedStatement`) |
| 22 | Desligar o MySQL com o programa aberto e tentar listar | `ERROR_MESSAGE` de `SQLException`; menu continua |
| 23 | Cachorro e gato na mesma listagem | Sons diferentes saindo da mesma chamada pela referência `Animal` |

---

## 11. Checklist final (objetivos do PDF)

**Abstração e herança**
- [ ] `Animal` é `abstract` com os 4 atributos `private`.
- [ ] `Cachorro` e `Gato` usam `extends Animal` e chamam `super(...)`.
- [ ] `Cachorro` tem `porte`; `Gato` tem `temperamento`.

**Polimorfismo**
- [ ] `public abstract String emitirSom()` em `Animal`, com `@Override` nas subclasses retornando `"Au Au!"` e `"Miau!"`.
- [ ] `Consulta` tem atributo `Animal paciente` e `horarioIndex` (0-9) e `sintomas`.
- [ ] O DAO recria `Cachorro` ou `Gato` e guarda numa referência `Animal`.
- [ ] A listagem chama `emitirSom()` sem `if` decidindo o som.

**JDBC**
- [ ] `ConexaoBD` com `Class.forName`, `DriverManager.getConnection`, URL/usuário/senha.
- [ ] `AgendaDAO` com `salvar`, `buscarAgenda`, `cancelar` (+ `atualizar`), todos com `PreparedStatement`.
- [ ] SELECT lido com `ResultSet`, `next()`, `getInt`/`getString`.
- [ ] Recursos fechados (try-with-resources).
- [ ] Nenhum vetor estático guardando a agenda entre operações.
- [ ] `banco.sql` no repositório; driver no módulo.

**Interface**
- [ ] Menu com `showOptionDialog`.
- [ ] Entradas com `showInputDialog`; idade convertida com `Integer.parseInt`.
- [ ] Agenda com `showMessageDialog`, 10 linhas, `[Disponível]` nas livres.
- [ ] Nenhum `Scanner` nem `System.out` no projeto.

**Exceções e regras**
- [ ] `SQLException` capturada e exibida com `ERROR_MESSAGE`.
- [ ] Sobreposição impedida (`ERROR_MESSAGE`).
- [ ] Agenda lotada avisada (`WARNING_MESSAGE`).
- [ ] Cancelar horário vazio avisado (`WARNING_MESSAGE`).
- [ ] `AgendaException` própria herdando de `Exception`.
- [ ] Todos os 23 testes executados.

---

## 12. Pontos a confirmar com a professora

1. **Opção "Alterar" no menu**: o texto do problema exige que a recepcionista possa alterar agendamentos, mas a lista do menu só traz 4 opções (Agendar, Ver, Cancelar, Sair). Sugestão: manter as quatro nomeadas e incluir "Alterar Agendamento" antes de "Sair".
2. **Numeração dos horários**: o PDF fala em índices 0 a 9; o código atual mostra 1 a 10. Sugestão: exibir e pedir 0 a 9, igual ao PDF, o que elimina as conversões `+1/-1` (fonte comum de erro).
3. **`PreparedStatement`**: exigido pelo enunciado mas ausente do texto da Aula 10. Confirmar se há material complementar.
4. **Limites de idade (29/38) e telefone 8-11 dígitos**: regras extras herdadas do 3º bimestre; não atrapalham, mas não são exigidas.
5. **Uso de arquivos (Aula 9)**: o projeto não pede; não foi incluído. Da Aula 9 só se aproveita o try-with-resources e o for-each.
