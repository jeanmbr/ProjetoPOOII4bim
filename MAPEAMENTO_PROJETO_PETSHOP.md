# Mapeamento completo - Sistema de Agendamento da Clínica Veterinária

## 1. Interpretação do escopo

O sistema representa a agenda de **um único dia**, funciona no terminal e mantém os dados somente durante a execução. A agenda possui exatamente 10 posições. Cada posição corresponde a um horário numerado de 1 a 10 para o usuário e ao índice de 0 a 9 no vetor: 9:00, 9:30, 10:00, 10:30, 11:00, 11:30, 13:30, 14:00, 14:30 e 15:00.

O sistema precisa permitir:

1. agendar uma consulta em um horário livre;
2. escolher entre paciente `Cachorro` e `Gato`;
3. guardar dados comuns e específicos do paciente;
4. guardar a queixa principal ou os sintomas;
5. listar os 10 horários, diferenciando livres e ocupados;
6. mostrar, em cada horário ocupado, dados da consulta, nome, tipo e som do animal;
7. editar os dados ligados a um horário ocupado;
8. cancelar uma consulta, atribuindo `null` à posição;
9. impedir sobreposição, acesso fora do vetor e operações sobre posições vazias;
10. avisar quando a agenda estiver vazia ou cheia.

Não fazem parte do enunciado: banco de dados, arquivos, várias datas, cadastro separado de tutores, cadastro de veterinários, login, cobrança, estoque, prontuário ou interface gráfica.

## 2. Limite técnico adotado

Serão usados apenas os recursos presentes nos materiais:

- classes, objetos, atributos, métodos e mensagens entre objetos;
- `public`, `private` e, apenas se necessário, `protected`;
- encapsulamento, construtores, `this`, getters, setters e sobrecarga;
- membros e métodos `static`;
- herança com `extends`, chamada de construtor com `super` e sobrescrita com `@Override`;
- classe e método abstratos exigidos pelo próprio enunciado;
- referência de tipo `Animal` apontando para `Cachorro` ou `Gato`;
- vetor `Consulta[]` de tamanho fixo e valor `null` para uma posição livre;
- `Scanner`, `System.in`, `System.out.print`, `println` e `printf` para terminal;
- `if/else`, tipos primitivos, `String` e conversão com `Integer.parseInt`;
- métodos de `String` ensinados: `length`, `equals`, `equalsIgnoreCase`, `toUpperCase`, `toLowerCase`, `charAt`, `codePointAt`, `indexOf`, `lastIndexOf`, `substring`, `replace`, `startsWith`, `endsWith` e `trim`.

Não serão usados `ArrayList`, `enum`, regex, `try/catch`, streams, lambdas, arquivos, banco de dados, datas da biblioteca Java, interfaces, records ou bibliotecas externas.

### Lacunas inevitáveis dos PDFs

Os slides não mostram de forma explícita um laço nem a leitura de uma linha textual com `Scanner`. Porém, o enunciado exige percorrer um vetor de 10 posições, manter um menu ativo e ler nomes, telefone e sintomas no terminal. Assim, o projeto precisa do uso básico de `for` ou `while` e de `Scanner.nextLine()`. Esses são os dois únicos recursos básicos implícitos que não aparecem detalhados nas páginas fornecidas.

`JOptionPane` foi ensinado, mas não deve ser usado porque o sistema solicitado precisa rodar no terminal.

## 3. Arquitetura

```text
Main
  |
  | chama métodos estáticos
  v
AgendaManager ---- contém ----> Consulta[10]
                                  |
                                  | possui um
                                  v
                               Animal (abstrata)
                                  |
                     +------------+------------+
                     |                         |
                  Cachorro                   Gato
```

Responsabilidades:

| Classe | Responsabilidade exclusiva |
|---|---|
| `Main` | menu, leitura, repetição da entrada inválida, confirmação e criação dos objetos |
| `Animal` | estado e comportamento comuns a qualquer paciente |
| `Cachorro` | dado específico do cachorro e som `Au Au!` |
| `Gato` | dado específico do gato e som `Miau!` |
| `Consulta` | associação entre um horário, um paciente e a queixa principal |
| `AgendaManager` | proteger e manipular o único vetor de 10 consultas |

## 4. Mapeamento de cada classe

### 4.1 `Animal.java`

Declaração esperada: `public abstract class Animal`.

Atributos privados obrigatórios:

- `String nome`;
- `int idade`;
- `String nomeDono`;
- `String telefoneDono`.

Métodos:

- construtor completo para tornar os quatro dados obrigatórios;
- getters e setters dos quatro atributos;
- `public abstract String emitirSom();` exatamente como exigido;
- `public abstract String getTipo();` para a listagem informar `Cachorro` ou `Gato` sem usar `instanceof` ou reflexão;
- `public boolean dadosComunsValidos()` para uma verificação defensiva antes de a consulta entrar na agenda;
- `public void exibirDadosComuns()` para centralizar a impressão e evitar duplicação.

Regras internas:

- atributos nunca ficam públicos;
- nome do animal e nome do tutor não aceitam `null`, vazio ou somente espaços;
- idade não pode ser negativa; cachorros aceitam no máximo 29 anos e gatos no máximo 38 anos;
- telefone permanece `String`, para preservar zero inicial;
- o telefone aceita somente dígitos e deve ter comprimento coerente com o padrão adotado no projeto;
- setters não substituem um valor válido por um valor inválido.

### 4.2 `Cachorro.java`

Declaração esperada: `public class Cachorro extends Animal`.

Atributo privado específico escolhido: `String porte`.

O porte é preferível à raça porque o enunciado fornece um domínio fechado e fácil de validar: `Pequeno`, `Médio` ou `Grande`.

Métodos:

- construtor completo, chamando primeiro `super(nome, idade, nomeDono, telefoneDono)`;
- `getPorte` e `setPorte`;
- `@Override public String emitirSom()`, retornando exatamente `Au Au!`;
- `@Override public String getTipo()`, retornando `Cachorro`;
- `public boolean dadosValidos()`, combinando dados comuns válidos e porte válido;
- método de exibição específica, se a impressão for delegada ao objeto.

Validação do porte:

- a entrada do terminal deve ser uma opção numérica de 1 a 3;
- o programa converte a opção para uma das três strings padronizadas;
- se também aceitar texto, a comparação deve usar `equalsIgnoreCase`, nunca `==`.

### 4.3 `Gato.java`

Declaração esperada: `public class Gato extends Animal`.

Atributo privado específico: `String temperamento`.

Métodos:

- construtor completo com chamada a `super`;
- `getTemperamento` e `setTemperamento`;
- `@Override public String emitirSom()`, retornando exatamente `Miau!`;
- `@Override public String getTipo()`, retornando `Gato`;
- `public boolean dadosValidos()`;
- método de exibição específica, se necessário.

Validação do temperamento:

- usar um menu de opções para evitar texto inconsistente;
- no mínimo, oferecer os valores citados no enunciado: `Dócil` e `Arredio`;
- armazenar sempre a grafia padronizada escolhida pelo programa.

### 4.4 `Consulta.java`

Atributos privados:

- `Animal paciente`;
- `int horario`, com valor externo de 1 a 10;
- `String sintomas` ou `String queixaPrincipal` — escolher apenas um dos nomes e usá-lo no projeto inteiro.

Métodos:

- construtor completo;
- getters e setters;
- `public boolean dadosValidos()`;
- `public void exibirDados()`.

Regras:

- `paciente` nunca pode ser `null` em uma consulta ocupada;
- `horario` deve estar entre 1 e 10;
- sintomas/queixa não aceitam `null`, vazio ou somente espaços;
- `exibirDados()` chama `paciente.getTipo()` e `paciente.emitirSom()` por meio da referência `Animal`. Isso demonstra o polimorfismo exigido;
- a classe não procura posições livres e não altera o vetor. Isso pertence ao `AgendaManager`.

### 4.5 `AgendaManager.java`

O nome do arquivo e da classe deve começar com maiúscula. O arquivo atual `agendarManager.java` e a classe `agendarManager` precisam ser renomeados para `AgendaManager.java` e `AgendaManager`.

Atributo obrigatório:

```java
private static Consulta[] agenda = new Consulta[10];
```

Métodos públicos estáticos obrigatórios:

- `public static void agendar(int indice, Consulta consulta)`;
- `public static void editar(int indice, Animal pacienteAtualizado, String sintomasAtualizados)`;
- `public static void excluir(int indice)`;
- `public static void listar()`.

Métodos auxiliares estáticos recomendados:

- `private static boolean indiceValido(int indice)`;
- `public static boolean horarioOcupado(int indice)`;
- `public static boolean estaVazia()`;
- `public static boolean estaCheia()`;
- `public static void listarHorariosLivres()`.

Contrato de `agendar`:

1. conferir se o índice está entre 0 e 9;
2. conferir se a agenda já está cheia;
3. conferir se a posição escolhida está `null`;
4. conferir se a consulta recebida não é `null` e se `dadosValidos()` é verdadeiro;
5. somente depois atribuir `agenda[indice] = consulta`;
6. imprimir mensagem clara de sucesso ou erro.

Contrato de `editar`:

1. validar o índice;
2. rejeitar a operação se `agenda[indice] == null`;
3. validar completamente os novos dados antes de alterar a consulta;
4. manter o mesmo horário;
5. substituir o paciente e/ou a queixa somente depois de todas as entradas serem válidas;
6. se qualquer validação falhar, preservar integralmente os dados antigos.

Contrato de `excluir`:

1. validar o índice;
2. rejeitar a operação se a posição já estiver vazia;
3. após confirmação textual `S/N`, executar `agenda[indice] = null`;
4. informar que o horário voltou a ficar disponível.

Contrato de `listar`:

1. se a agenda estiver completamente vazia, imprimir `Nenhum atendimento foi agendado para o dia.`;
2. ainda assim, exibir os 10 horários como `[Disponível]`, atendendo também ao requisito de mostrar toda a agenda;
3. em posição ocupada, imprimir horário, queixa, dados do paciente, tipo e som;
4. chamar `emitirSom()` pela referência `Animal`, sem decidir o som com `if`.

### 4.6 `Main.java`

Responsabilidades:

- criar uma única instância de `Scanner`;
- mostrar o menu até o usuário escolher sair;
- ler tudo inicialmente como `String` com `nextLine()`;
- normalizar textos com `trim()`;
- validar antes de converter ou criar objetos;
- chamar o método apropriado do `AgendaManager`;
- fechar o `Scanner` somente ao terminar o programa.

Menu sugerido:

```text
1 - Agendar consulta
2 - Consultar agenda
3 - Editar agendamento
4 - Cancelar consulta
0 - Sair
```

Métodos auxiliares recomendados em `Main`:

- `private static String lerTextoObrigatorio(String mensagem)`;
- `private static int lerInteiroEntre(String mensagem, int minimo, int maximo)`;
- `private static boolean somenteDigitos(String texto)`;
- `private static String lerTelefone()`;
- `private static Animal lerPaciente()`;
- `private static Cachorro lerCachorro(...)`;
- `private static Gato lerGato(...)`;
- `private static boolean confirmar(String mensagem)`;
- `private static void executarAgendamento()`;
- `private static void executarEdicao()`;
- `private static void executarCancelamento()`.

Esses auxiliares evitam repetir validação e deixam o `main` responsável apenas pelo fluxo do menu.

## 5. Fluxos funcionais

### 5.1 Agendamento

1. Verificar `AgendaManager.estaCheia()` antes de pedir dados.
2. Mostrar os 10 horários, indicando `[Disponível]` ou `[Ocupado]` ao lado de cada um.
3. Ler um horário de 1 a 10 e só então converter para índice com `indice = horario - 1`.
4. Rejeitar horário já ocupado.
5. Perguntar tipo: `1 - Cachorro`, `2 - Gato`.
6. Ler nome, idade, nome do tutor e telefone.
7. Ler porte para cachorro ou temperamento para gato.
8. Ler sintomas/queixa principal.
9. Mostrar um resumo e pedir confirmação `S/N`.
10. Instanciar a subclasse correta.
11. Criar `Consulta` usando a referência `Animal`.
12. Chamar `AgendaManager.agendar`.

### 5.2 Consulta da agenda

1. Chamar `AgendaManager.listar()`.
2. Percorrer sempre as 10 posições.
3. Para `null`, exibir `Horário N - [Disponível]`.
4. Para posição ocupada, chamar a exibição da `Consulta`.

### 5.3 Edição

Para manter a implementação simples e segura, a edição deve reler um conjunto completo e validado de dados para o horário, em vez de aceitar alterações parciais difíceis de reverter.

1. Listar a agenda.
2. Ler e validar o horário.
3. Rejeitar imediatamente se estiver vazio.
4. Exibir os dados atuais.
5. Ler novamente tipo, dados comuns, dado específico e sintomas.
6. Confirmar a substituição.
7. Criar o paciente atualizado.
8. Chamar `AgendaManager.editar`.
9. Não alterar nada se o usuário não confirmar ou se algum dado for inválido.

### 5.4 Cancelamento

1. Listar a agenda.
2. Ler e validar o horário.
3. Rejeitar imediatamente se estiver vazio.
4. Exibir os dados que serão removidos.
5. Pedir confirmação `S/N`.
6. Chamar `AgendaManager.excluir` somente após `S`.

## 6. Validação de entrada sem recursos avançados

Como `try/catch` e regex não aparecem nos materiais, números devem ser validados antes de `Integer.parseInt`.

Estratégia para um inteiro:

1. aplicar `trim()`;
2. recusar comprimento zero;
3. limitar o comprimento conforme o campo, evitando estouro na conversão;
4. percorrer cada posição com `charAt` ou `codePointAt`;
5. aceitar apenas códigos de `0` a `9`;
6. somente então usar `Integer.parseInt`;
7. validar a faixa final.

Matriz de validação:

| Campo | Validação obrigatória/recomendada | Tratamento |
|---|---|---|
| opção principal | dígitos e faixa 0 a 4 | repetir até ser válida |
| horário | dígitos e faixa 1 a 10 | converter para índice somente após validar |
| tipo do animal | apenas 1 ou 2 | repetir a pergunta |
| nome do animal | não `null`, não vazio após `trim` | repetir |
| idade | somente dígitos; cachorro de 0 a 29 e gato de 0 a 38 | repetir até ficar dentro do limite da espécie |
| nome do tutor | não `null`, não vazio após `trim` | repetir |
| telefone | `String` somente com dígitos | preservar zero inicial; adotar comprimento de 10 ou 11 dígitos se o grupo confirmar padrão brasileiro |
| porte | opção 1, 2 ou 3 | armazenar Pequeno, Médio ou Grande |
| temperamento | opção de menu | armazenar texto padronizado |
| sintomas/queixa | não `null`, não vazio após `trim` | repetir |
| confirmação | `S` ou `N` com `equalsIgnoreCase` | repetir se diferente |

Cuidados adicionais:

- nunca comparar `String` com `==`;
- nunca acessar `charAt(0)` antes de confirmar que `length() > 0`;
- nunca converter texto antes de confirmar que ele contém somente dígitos;
- nunca calcular o índice antes de confirmar que o horário está entre 1 e 10;
- nunca pedir todos os dados de agendamento se a agenda já está cheia;
- nunca sobrescrever uma consulta ocupada;
- nunca editar ou excluir uma posição `null`;
- nunca armazenar telefone como `int`;
- nunca alterar parcialmente uma consulta se o restante dos novos dados falhar;
- manter as mensagens de erro específicas, informando o que precisa ser corrigido.

## 7. Mensagens mínimas esperadas

- `Opção inválida. Digite um número de 0 a 4.`
- `Horário inválido. Escolha um número de 1 a 10.`
- `Este horário já está ocupado.`
- `A agenda está cheia. Não é possível cadastrar outra consulta.`
- `Não existe consulta cadastrada neste horário.`
- `Nenhum atendimento foi agendado para o dia.`
- `Nome não pode ficar vazio.`
- `Idade inválida. Digite somente números.`
- `Telefone inválido. Digite somente números.`
- `Consulta agendada com sucesso.`
- `Consulta atualizada com sucesso.`
- `Consulta cancelada. O horário está disponível novamente.`
- `Operação cancelada. Nenhum dado foi alterado.`

## 8. Plano de implementação

Ordem recomendada:

1. corrigir o nome de `agendarManager` para `AgendaManager`;
2. implementar `Animal` e seus testes manuais simples;
3. implementar `Cachorro` e `Gato`, conferindo `super` e `@Override`;
4. implementar `Consulta` e testar a chamada polimórfica de `emitirSom`;
5. implementar o vetor e as regras em `AgendaManager`;
6. implementar os auxiliares de validação em `Main`;
7. implementar o menu e os quatro fluxos;
8. executar todos os cenários da matriz de testes;
9. revisar encapsulamento, nomes e ausência de sintaxe não ensinada.

Divisão possível para três integrantes:

| Integrante | Entrega principal | Revisão cruzada |
|---|---|---|
| 1 | `Animal`, `Cachorro`, `Gato` | validar herança, construtores, `super` e sobrescrita |
| 2 | `Consulta`, `AgendaManager` | validar associação, vetor, `null`, cheio/vazio e sobreposição |
| 3 | `Main` e validações de terminal | validar menu, entradas inválidas, mensagens e integração |

Todos devem testar o sistema integrado e entender todas as classes para a apresentação.

## 9. Matriz de testes de aceitação

| Nº | Cenário | Resultado esperado |
|---:|---|---|
| 1 | iniciar e listar | aviso de agenda vazia e 10 posições disponíveis |
| 2 | agendar cachorro válido | posição ocupada, porte e `Au Au!` exibidos |
| 3 | agendar gato válido | posição ocupada, temperamento e `Miau!` exibidos |
| 4 | agendar sobre posição ocupada | recusa sem alterar a consulta anterior |
| 5 | informar horário 0 | recusa sem acessar o vetor |
| 6 | informar horário 11 | recusa sem acessar o vetor |
| 7 | informar letras no horário | recusa antes de `parseInt` |
| 8 | informar campo obrigatório vazio | nova solicitação do campo |
| 9 | informar idade negativa ou com letras | recusa |
| 10 | informar telefone com letras | recusa |
| 11 | preencher as 10 posições | agenda listada como cheia |
| 12 | tentar o 11º agendamento | recusa antes de solicitar os demais dados |
| 13 | editar posição vazia | mensagem de erro e nenhum dado alterado |
| 14 | editar posição ocupada | novos dados exibidos, mesmo horário preservado |
| 15 | desistir da edição | dados antigos integralmente preservados |
| 16 | cancelar posição vazia | mensagem de erro |
| 17 | cancelar posição ocupada | posição recebe `null` e volta a aparecer disponível |
| 18 | desistir do cancelamento | consulta permanece |
| 19 | reagendar após cancelamento | novo cadastro aceito na posição liberada |
| 20 | listar cachorro e gato juntos | sons diferentes chamados pela mesma referência `Animal` |
| 21 | usar `s`, `S`, `n` e `N` | confirmação funciona com `equalsIgnoreCase` |
| 22 | inserir espaços antes/depois | `trim` normaliza sem aceitar texto só com espaços |

## 10. Checklist final

- [ ] classe `Animal` é abstrata;
- [ ] todos os atributos das entidades estão privados;
- [ ] subclasses usam `extends Animal`;
- [ ] construtores das subclasses chamam `super`;
- [ ] ambos os sons usam `@Override`;
- [ ] `Consulta` possui um atributo do tipo `Animal`;
- [ ] `AgendaManager` possui exatamente um vetor estático de 10 posições;
- [ ] os quatro métodos obrigatórios são estáticos;
- [ ] o programa exibe os 10 horários;
- [ ] agenda vazia e agenda cheia são detectadas;
- [ ] não há sobreposição;
- [ ] edição e exclusão rejeitam posição vazia;
- [ ] exclusão atribui `null`;
- [ ] toda conversão numérica ocorre depois da validação textual;
- [ ] toda comparação de texto usa `equals` ou `equalsIgnoreCase`;
- [ ] a interface usa apenas terminal;
- [ ] não há recursos fora do limite definido;
- [ ] todos os 22 testes foram executados.

## 11. Estado atual do projeto

O workspace já contém os seis arquivos necessários, mas ainda estão praticamente vazios. `Main.java` contém apenas o exemplo padrão do IntelliJ, e `Animal`, `Cachorro`, `Gato` e `Consulta` não têm membros. A classe controladora existe com grafia incompatível com o enunciado e com a convenção ensinada; essa é a primeira correção estrutural a fazer.
