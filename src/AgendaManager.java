import java.sql.SQLException;

public class AgendaManager {
    private static final String[] HORARIOS = {
            "9:00", "9:30", "10:00", "10:30", "11:00",
            "11:30", "13:30", "14:00", "14:30", "15:00"
    };

    public static boolean indiceValido(int indice) {
        return indice >= 0 && indice < HORARIOS.length;
    }

    public static String getHorarioTexto(int indice) {
        if (indiceValido(indice)) {
            return HORARIOS[indice];
        }
        return "Horário inválido";
    }

    private static void validarIndice(int indice) throws AgendaException {
        if (!indiceValido(indice)) {
            throw new AgendaException("Horário inválido. Escolha um número de 0 a 9.");
        }
    }

    private static boolean estaCheia(Consulta[] agenda) {
        for (Consulta consulta : agenda) {
            if (consulta == null) {
                return false;
            }
        }
        return true;
    }

    private static void validarConsulta(Consulta consulta) throws AgendaException {
        if (consulta == null || !consulta.dadosValidos()) {
            throw new AgendaException("Os dados da consulta são inválidos.");
        }
    }

    public static void verificarAgendaNaoLotada() throws AgendaException, SQLException {
        if (estaCheia(AgendaDAO.buscarAgenda())) {
            throw new AgendaException("A agenda está lotada. Não há horários disponíveis.", true);
        }
    }

    public static void verificarHorarioLivre(int indice) throws AgendaException, SQLException {
        validarIndice(indice);
        if (AgendaDAO.buscarAgenda()[indice] != null) {
            throw new AgendaException("Este horário já está ocupado. Escolha outro.");
        }
    }

    public static void agendar(Consulta consulta) throws AgendaException, SQLException {
        validarConsulta(consulta);
        verificarAgendaNaoLotada();
        verificarHorarioLivre(consulta.getHorarioIndex());
        AgendaDAO.salvar(consulta);
    }

    public static Consulta buscarConsultaOcupada(int indice) throws AgendaException, SQLException {
        validarIndice(indice);
        Consulta consulta = AgendaDAO.buscarAgenda()[indice];
        if (consulta == null) {
            throw new AgendaException("Este horário está vazio. Não há consulta cadastrada nele.", true);
        }
        return consulta;
    }

    public static void alterar(Consulta consulta) throws AgendaException, SQLException {
        validarConsulta(consulta);
        buscarConsultaOcupada(consulta.getHorarioIndex());
        if (AgendaDAO.atualizar(consulta) == 0) {
            throw new AgendaException("Este horário está vazio. Nada foi alterado.", true);
        }
    }

    public static void cancelar(int indice) throws AgendaException, SQLException {
        validarIndice(indice);
        if (AgendaDAO.cancelar(indice) == 0) {
            throw new AgendaException("Este horário já está vazio. Não há consulta para cancelar.", true);
        }
    }

    public static String montarTextoAgenda() throws SQLException {
        Consulta[] agenda = AgendaDAO.buscarAgenda();
        String texto = "";
        boolean vazia = true;

        for (int i = 0; i < agenda.length; i++) {
            texto += i + " - " + HORARIOS[i] + " - ";
            if (agenda[i] == null) {
                texto += "[Disponível]\n";
            } else {
                texto += agenda[i].getResumo() + "\n";
                vazia = false;
            }
        }

        if (vazia) {
            texto += "\nNenhum atendimento foi agendado para o dia.";
        }
        return texto;
    }

    public static String montarTextoHorariosLivres() throws SQLException {
        Consulta[] agenda = AgendaDAO.buscarAgenda();
        String texto = "";
        for (int i = 0; i < agenda.length; i++) {
            if (agenda[i] == null) {
                texto += i + " - " + HORARIOS[i] + "\n";
            }
        }
        return texto;
    }
}
