import javax.swing.JOptionPane;
import java.sql.SQLException;

public class Main {
    private static final String TITULO = "Clínica Veterinária";
    private static final int CANCELADO = -1;

    public static void main(String[] args) {
        try {
            AgendaManager.montarTextoAgenda();
        } catch (SQLException e) {
            mostrarErro("Não foi possível conectar ao banco de dados.\n" + e.getMessage());
            return;
        }

        String[] botoes = {"Agendar Consulta", "Ver Agenda Diária", "Cancelar Agendamento",
                "Alterar Agendamento", "Sair"};
        int opcao;

        do {
            opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", TITULO,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, botoes, botoes[0]);

            try {
                if (opcao == 0) {
                    executarAgendamento();
                } else if (opcao == 1) {
                    JOptionPane.showMessageDialog(null, AgendaManager.montarTextoAgenda(),
                            "Agenda Diária", JOptionPane.INFORMATION_MESSAGE);
                } else if (opcao == 2) {
                    executarCancelamento();
                } else if (opcao == 3) {
                    executarAlteracao();
                }
            } catch (AgendaException e) {
                if (e.isAviso()) {
                    mostrarAviso(e.getMessage());
                } else {
                    mostrarErro(e.getMessage());
                }
            } catch (SQLException e) {
                mostrarErro("Erro ao acessar o banco de dados:\n" + e.getMessage());
            } catch (Exception e) {
                mostrarErro("Erro inesperado: " + e.toString());
            }
        } while (opcao != 4 && opcao != JOptionPane.CLOSED_OPTION);

        mostrarInfo("Sistema encerrado.");
    }

    private static void executarAgendamento() throws AgendaException, SQLException {
        AgendaManager.verificarAgendaNaoLotada();

        int indice = lerHorarioLivre();
        if (indice == CANCELADO) {
            operacaoCancelada();
            return;
        }

        Animal paciente = lerPaciente();
        if (paciente == null) {
            operacaoCancelada();
            return;
        }

        String sintomas = lerTexto("Informe os sintomas ou a queixa principal:", 255);
        if (sintomas == null) {
            operacaoCancelada();
            return;
        }

        Consulta consulta = new Consulta(paciente, indice, sintomas);
        if (confirmar("Confirmar agendamento?\n\nHorário: " + indice + " - "
                + AgendaManager.getHorarioTexto(indice) + "\n" + consulta.getDetalhes())) {
            AgendaManager.agendar(consulta);
            mostrarInfo("Consulta agendada com sucesso.");
        } else {
            operacaoCancelada();
        }
    }

    private static void executarCancelamento() throws AgendaException, SQLException {
        int indice = lerInteiro(AgendaManager.montarTextoAgenda()
                + "\nInforme o horário que deseja cancelar (0 a 9):", 0, 9);
        if (indice == CANCELADO) {
            operacaoCancelada();
            return;
        }

        Consulta consulta = AgendaManager.buscarConsultaOcupada(indice);
        if (confirmar("Confirmar cancelamento desta consulta?\n\n" + consulta.getDetalhes())) {
            AgendaManager.cancelar(indice);
            mostrarInfo("Consulta cancelada. Horário liberado.");
        } else {
            operacaoCancelada();
        }
    }

    private static void executarAlteracao() throws AgendaException, SQLException {
        int indice = lerInteiro(AgendaManager.montarTextoAgenda()
                + "\nInforme o horário que deseja alterar (0 a 9):", 0, 9);
        if (indice == CANCELADO) {
            operacaoCancelada();
            return;
        }

        Consulta atual = AgendaManager.buscarConsultaOcupada(indice);
        mostrarInfo("Dados atuais:\n\n" + atual.getDetalhes() + "\n\nInforme a seguir os novos dados.");

        Animal paciente = lerPaciente();
        if (paciente == null) {
            operacaoCancelada();
            return;
        }

        String sintomas = lerTexto("Informe os novos sintomas ou queixa principal:", 255);
        if (sintomas == null) {
            operacaoCancelada();
            return;
        }

        Consulta nova = new Consulta(paciente, indice, sintomas);
        if (confirmar("Confirmar alteração?\n\n" + nova.getDetalhes())) {
            AgendaManager.alterar(nova);
            mostrarInfo("Consulta alterada com sucesso.");
        } else {
            operacaoCancelada();
        }
    }

    private static int lerHorarioLivre() throws SQLException {
        while (true) {
            int indice = lerInteiro("Horários disponíveis:\n" + AgendaManager.montarTextoHorariosLivres()
                    + "\nEscolha um horário (0 a 9):", 0, 9);
            if (indice == CANCELADO) {
                return CANCELADO;
            }
            try {
                AgendaManager.verificarHorarioLivre(indice);
                return indice;
            } catch (AgendaException e) {
                mostrarErro(e.getMessage());
            }
        }
    }

    private static Animal lerPaciente() {
        String[] especies = {"Cachorro", "Gato"};
        int tipo = JOptionPane.showOptionDialog(null, "Tipo do paciente:", TITULO,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, especies, especies[0]);
        if (tipo != 0 && tipo != 1) {
            return null;
        }

        String nome = lerTexto("Nome do animal:", 100);
        if (nome == null) {
            return null;
        }

        int idade;
        if (tipo == 0) {
            idade = lerInteiro("Idade do cachorro (0 a 29 anos):", 0, 29);
        } else {
            idade = lerInteiro("Idade do gato (0 a 38 anos):", 0, 38);
        }
        if (idade == CANCELADO) {
            return null;
        }

        String nomeDono = lerTexto("Nome do tutor:", 100);
        if (nomeDono == null) {
            return null;
        }

        String telefone = lerTelefone();
        if (telefone == null) {
            return null;
        }

        Animal paciente;
        if (tipo == 0) {
            String porte = escolherOpcao("Porte do cachorro:", new String[]{"Pequeno", "Médio", "Grande"});
            if (porte == null) {
                return null;
            }
            paciente = new Cachorro(nome, idade, nomeDono, telefone, porte);
        } else {
            String temperamento = escolherOpcao("Temperamento do gato:", new String[]{"Dócil", "Arredio"});
            if (temperamento == null) {
                return null;
            }
            paciente = new Gato(nome, idade, nomeDono, telefone, temperamento);
        }

        if (!paciente.dadosValidos()) {
            mostrarErro("Os dados do paciente são inválidos.");
            return null;
        }
        return paciente;
    }

    private static String lerTexto(String mensagem, int tamanhoMaximo) {
        while (true) {
            String texto = JOptionPane.showInputDialog(null, mensagem, TITULO, JOptionPane.QUESTION_MESSAGE);
            if (texto == null) {
                return null;
            }
            texto = texto.trim();
            if (texto.length() == 0) {
                mostrarErro("Este campo não pode ficar vazio.");
            } else if (texto.length() > tamanhoMaximo) {
                mostrarErro("Texto muito longo. Use no máximo " + tamanhoMaximo + " caracteres.");
            } else {
                return texto;
            }
        }
    }

    private static int lerInteiro(String mensagem, int minimo, int maximo) {
        int valor = 0;
        boolean ok = false;

        do {
            String texto = JOptionPane.showInputDialog(null, mensagem, TITULO, JOptionPane.QUESTION_MESSAGE);
            if (texto == null) {
                return CANCELADO;
            }
            texto = texto.trim();

            if (texto.length() == 0) {
                mostrarErro("O valor não pode ficar vazio.");
            } else {
                try {
                    valor = Integer.parseInt(texto);
                    if (valor >= minimo && valor <= maximo) {
                        ok = true;
                    } else {
                        mostrarErro("Valor inválido. Digite um número entre " + minimo + " e " + maximo + ".");
                    }
                } catch (NumberFormatException e) {
                    mostrarErro("Entrada inválida. Digite somente números inteiros.");
                }
            }
        } while (!ok);

        return valor;
    }

    private static String lerTelefone() {
        while (true) {
            String telefone = lerTexto("Telefone do tutor (8 a 11 dígitos, somente números):", 11);
            if (telefone == null) {
                return null;
            }
            if (Animal.telefoneValido(telefone)) {
                return telefone;
            }
            mostrarErro("Telefone inválido. Digite de 8 a 11 números.");
        }
    }

    private static String escolherOpcao(String mensagem, String[] opcoes) {
        Object escolha = JOptionPane.showInputDialog(null, mensagem, TITULO,
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        if (escolha == null) {
            return null;
        }
        return (String) escolha;
    }

    private static boolean confirmar(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(null, mensagem, TITULO, JOptionPane.YES_NO_OPTION);
        return resposta == JOptionPane.YES_OPTION;
    }

    private static void operacaoCancelada() {
        mostrarInfo("Operação cancelada. Nenhum dado foi alterado.");
    }

    private static void mostrarInfo(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, TITULO, JOptionPane.WARNING_MESSAGE);
    }

    private static void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, TITULO, JOptionPane.ERROR_MESSAGE);
    }
}
