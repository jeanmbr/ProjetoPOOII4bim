public class AgendaManager {
    private static Consulta[] agenda = new Consulta[10];
    private static String[] horarios = {
            "9:00", "9:30", "10:00", "10:30", "11:00",
            "11:30", "13:30", "14:00", "14:30", "15:00"
    };

    private static boolean indiceValido(int indice) {
        return indice >= 0 && indice < agenda.length;
    }

    public static boolean horarioOcupado(int indice) {
        return indiceValido(indice) && agenda[indice] != null;
    }

    public static String getHorarioTexto(int indice) {
        if (indiceValido(indice)) {
            return horarios[indice];
        }
        return "Horário inválido";
    }

    public static boolean estaVazia() {
        for (int i = 0; i < agenda.length; i++) {
            if (agenda[i] != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean estaCheia() {
        for (int i = 0; i < agenda.length; i++) {
            if (agenda[i] == null) {
                return false;
            }
        }
        return true;
    }

    public static void agendar(int indice, Consulta consulta) {
        if (!indiceValido(indice)) {
            System.out.println("Erro: horário inválido.");
        } else if (estaCheia()) {
            System.out.println("A agenda está cheia. Não é possível cadastrar outra consulta.");
        } else if (agenda[indice] != null) {
            System.out.println("Este horário já está ocupado.");
        } else if (consulta == null || !consulta.dadosValidos()) {
            System.out.println("Erro: os dados da consulta são inválidos.");
        } else if (consulta.getHorario() != indice + 1) {
            System.out.println("Erro: o horário da consulta não corresponde à posição escolhida.");
        } else {
            agenda[indice] = consulta;
            System.out.println("Consulta agendada com sucesso.");
        }
    }

    public static void editar(int indice, Animal pacienteAtualizado, String sintomasAtualizados) {
        if (!indiceValido(indice)) {
            System.out.println("Erro: horário inválido.");
        } else if (agenda[indice] == null) {
            System.out.println("Não existe consulta cadastrada neste horário.");
        } else if (pacienteAtualizado == null || !pacienteAtualizado.dadosValidos()) {
            System.out.println("Erro: os novos dados do paciente são inválidos.");
        } else if (sintomasAtualizados == null || sintomasAtualizados.trim().length() == 0) {
            System.out.println("Erro: a queixa principal não pode ficar vazia.");
        } else {
            agenda[indice].setPaciente(pacienteAtualizado);
            agenda[indice].setSintomas(sintomasAtualizados);
            System.out.println("Consulta atualizada com sucesso.");
        }
    }

    public static void excluir(int indice) {
        if (!indiceValido(indice)) {
            System.out.println("Erro: horário inválido.");
        } else if (agenda[indice] == null) {
            System.out.println("Não existe consulta cadastrada neste horário.");
        } else {
            agenda[indice] = null;
            System.out.println("Consulta cancelada. O horário está disponível novamente.");
        }
    }

    public static void listar() {
        System.out.println();
        System.out.println("========== AGENDA DO DIA ==========");

        if (estaVazia()) {
            System.out.println("Nenhum atendimento foi agendado para o dia.");
        }

        for (int i = 0; i < agenda.length; i++) {
            System.out.println();
            if (agenda[i] == null) {
                System.out.println((i + 1) + " - " + horarios[i] + " - [Disponível]");
            } else {
                System.out.println((i + 1) + " - " + horarios[i] + " - [Ocupado]");
                agenda[i].exibirDados();
            }
        }

        System.out.println("===================================");
    }

    public static void listarHorarios() {
        System.out.println();
        System.out.println("========== HORÁRIOS ==========");
        for (int i = 0; i < agenda.length; i++) {
            if (agenda[i] == null) {
                System.out.println((i + 1) + " - " + horarios[i] + " - [Disponível]");
            } else {
                System.out.println((i + 1) + " - " + horarios[i] + " - [Ocupado]");
            }
        }
        System.out.println("==============================");
    }

    public static void exibirConsulta(int indice) {
        if (!indiceValido(indice)) {
            System.out.println("Erro: horário inválido.");
        } else if (agenda[indice] == null) {
            System.out.println("Não existe consulta cadastrada neste horário.");
        } else {
            agenda[indice].exibirDados();
        }
    }
}
