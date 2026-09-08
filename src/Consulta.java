public class Consulta {
    private Animal paciente;
    private int horario;
    private String sintomas;

    public Consulta(Animal paciente, int horario, String sintomas) {
        this.paciente = null;
        this.horario = 0;
        this.sintomas = "";

        setPaciente(paciente);
        setHorario(horario);
        setSintomas(sintomas);
    }

    public Animal getPaciente() {
        return paciente;
    }

    public void setPaciente(Animal paciente) {
        if (paciente != null && paciente.dadosValidos()) {
            this.paciente = paciente;
        }
    }

    public int getHorario() {
        return horario;
    }

    public void setHorario(int horario) {
        if (horario >= 1 && horario <= 10) {
            this.horario = horario;
        }
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        if (sintomas != null && sintomas.trim().length() > 0) {
            this.sintomas = sintomas.trim();
        }
    }

    public boolean dadosValidos() {
        return paciente != null
                && paciente.dadosValidos()
                && horario >= 1
                && horario <= 10
                && sintomas != null
                && sintomas.trim().length() > 0;
    }

    public void exibirDados() {
        if (!dadosValidos()) {
            System.out.println("Erro: não é possível exibir uma consulta com dados inválidos.");
        } else {
            System.out.println("Horário: " + horario + " - " + AgendaManager.getHorarioTexto(horario - 1));
            System.out.println("Tipo de paciente: " + paciente.getTipo());
            paciente.exibirDadosComuns();
            System.out.println(paciente.getInformacaoEspecifica());
            System.out.println("Queixa principal: " + sintomas);
            System.out.println("Som emitido: " + paciente.emitirSom());
        }
    }
}
