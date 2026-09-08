import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao = -1;

        System.out.println("Sistema de Agendamento da Clínica Veterinária");

        while (opcao != 0) {
            exibirMenu();
            opcao = lerInteiroEntre("Escolha uma opção: ", 0, 4);

            if (opcao == 1) {
                executarAgendamento();
            } else if (opcao == 2) {
                AgendaManager.listar();
            } else if (opcao == 3) {
                executarEdicao();
            } else if (opcao == 4) {
                executarCancelamento();
            } else {
                System.out.println("Sistema encerrado.");
            }
        }

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("============== MENU ==============");
        System.out.println("1 - Agendar consulta");
        System.out.println("2 - Consultar agenda");
        System.out.println("3 - Editar agendamento");
        System.out.println("4 - Cancelar consulta");
        System.out.println("0 - Sair");
        System.out.println("==================================");
    }

    private static void executarAgendamento() {
        AgendaManager.listarHorarios();

        if (AgendaManager.estaCheia()) {
            System.out.println("A agenda está cheia. Não é possível cadastrar outra consulta.");
            return;
        }

        int horario = lerHorarioLivre();
        Animal paciente = lerPaciente();
        String sintomas = lerTextoObrigatorio("Informe os sintomas ou a queixa principal: ");

        Consulta consulta = new Consulta(paciente, horario, sintomas);

        System.out.println();
        System.out.println("Confira os dados do novo agendamento:");
        consulta.exibirDados();

        if (confirmar("Confirmar agendamento? (S/N): ")) {
            AgendaManager.agendar(horario - 1, consulta);
        } else {
            System.out.println("Operação cancelada. Nenhum dado foi alterado.");
        }
    }

    private static void executarEdicao() {
        AgendaManager.listar();
        int horario = lerInteiroEntre("Informe o horário que deseja editar (1 a 10): ", 1, 10);
        int indice = horario - 1;

        if (!AgendaManager.horarioOcupado(indice)) {
            System.out.println("Não existe consulta cadastrada neste horário.");
            return;
        }

        System.out.println();
        System.out.println("Dados atuais:");
        AgendaManager.exibirConsulta(indice);

        System.out.println();
        System.out.println("Informe todos os novos dados da consulta:");
        Animal pacienteAtualizado = lerPaciente();
        String sintomasAtualizados = lerTextoObrigatorio("Informe os novos sintomas ou queixa principal: ");
        Consulta consultaAtualizada = new Consulta(pacienteAtualizado, horario, sintomasAtualizados);

        System.out.println();
        System.out.println("Confira os novos dados:");
        consultaAtualizada.exibirDados();

        if (confirmar("Confirmar edição? (S/N): ")) {
            AgendaManager.editar(indice, pacienteAtualizado, sintomasAtualizados);
        } else {
            System.out.println("Operação cancelada. Nenhum dado foi alterado.");
        }
    }

    private static void executarCancelamento() {
        AgendaManager.listar();
        int horario = lerInteiroEntre("Informe o horário que deseja cancelar (1 a 10): ", 1, 10);
        int indice = horario - 1;

        if (!AgendaManager.horarioOcupado(indice)) {
            System.out.println("Não existe consulta cadastrada neste horário.");
            return;
        }

        System.out.println();
        System.out.println("Consulta que será cancelada:");
        AgendaManager.exibirConsulta(indice);

        if (confirmar("Confirmar cancelamento? (S/N): ")) {
            AgendaManager.excluir(indice);
        } else {
            System.out.println("Operação cancelada. Nenhum dado foi alterado.");
        }
    }

    private static int lerHorarioLivre() {
        int horario = lerInteiroEntre("Escolha um horário disponível (1 a 10): ", 1, 10);

        while (AgendaManager.horarioOcupado(horario - 1)) {
            System.out.println("Este horário já está ocupado.");
            horario = lerInteiroEntre("Escolha outro horário (1 a 10): ", 1, 10);
        }

        return horario;
    }

    private static Animal lerPaciente() {
        int tipo = lerInteiroEntre("Tipo do paciente (1 - Cachorro, 2 - Gato): ", 1, 2);
        String nome = lerTextoObrigatorio("Nome do animal: ");
        int idade;

        if (tipo == 1) {
            idade = lerInteiroEntre("Idade do cachorro (0 a 29 anos): ", 0, 29);
        } else {
            idade = lerInteiroEntre("Idade do gato (0 a 38 anos): ", 0, 38);
        }

        String nomeDono = lerTextoObrigatorio("Nome do tutor: ");
        String telefoneDono = lerTelefone();

        if (tipo == 1) {
            String porte = lerPorte();
            return new Cachorro(nome, idade, nomeDono, telefoneDono, porte);
        }

        String temperamento = lerTemperamento();
        return new Gato(nome, idade, nomeDono, telefoneDono, temperamento);
    }

    private static String lerPorte() {
        System.out.println("Porte do cachorro:");
        System.out.println("1 - Pequeno");
        System.out.println("2 - Médio");
        System.out.println("3 - Grande");
        int opcao = lerInteiroEntre("Escolha o porte: ", 1, 3);

        if (opcao == 1) {
            return "Pequeno";
        } else if (opcao == 2) {
            return "Médio";
        }
        return "Grande";
    }

    private static String lerTemperamento() {
        System.out.println("Temperamento do gato:");
        System.out.println("1 - Dócil");
        System.out.println("2 - Arredio");
        int opcao = lerInteiroEntre("Escolha o temperamento: ", 1, 2);

        if (opcao == 1) {
            return "Dócil";
        }
        return "Arredio";
    }

    private static String lerTextoObrigatorio(String mensagem) {
        String texto = "";

        while (texto.length() == 0) {
            System.out.print(mensagem);
            texto = scanner.nextLine().trim();
            if (texto.length() == 0) {
                System.out.println("Este campo não pode ficar vazio.");
            }
        }

        return texto;
    }

    private static String lerTelefone() {
        String telefone = "";
        boolean valido = false;

        while (!valido) {
            System.out.print("Telefone do tutor (8 a 11 dígitos): ");
            telefone = scanner.nextLine().trim();

            if (telefone.length() < 8 || telefone.length() > 11) {
                System.out.println("Telefone inválido. Digite entre 8 e 11 números.");
            } else if (!somenteDigitos(telefone)) {
                System.out.println("Telefone inválido. Digite somente números.");
            } else {
                valido = true;
            }
        }

        return telefone;
    }

    private static int lerInteiroEntre(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String texto = scanner.nextLine().trim();

            if (texto.length() == 0) {
                System.out.println("Entrada inválida. O valor não pode ficar vazio.");
            } else if (texto.length() > 9 || !somenteDigitos(texto)) {
                System.out.println("Entrada inválida. Digite somente números inteiros.");
            } else {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Valor inválido. Digite um número entre " + minimo + " e " + maximo + ".");
            }
        }
    }

    private static boolean somenteDigitos(String texto) {
        if (texto == null || texto.length() == 0) {
            return false;
        }

        for (int i = 0; i < texto.length(); i++) {
            int codigo = texto.codePointAt(i);
            if (codigo < 48 || codigo > 57) {
                return false;
            }
        }

        return true;
    }

    private static boolean confirmar(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String resposta = scanner.nextLine().trim();

            if (resposta.equalsIgnoreCase("S")) {
                return true;
            } else if (resposta.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Resposta inválida. Digite S para sim ou N para não.");
        }
    }
}
