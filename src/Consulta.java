public class Consulta {
    private Animal paciente;
    private int horarioIndex;
    private String sintomas;

    public Consulta(Animal paciente, int horarioIndex, String sintomas) {
        this.paciente = null;
        this.horarioIndex = -1;
        this.sintomas = "";

        setPaciente(paciente);
        setHorarioIndex(horarioIndex);
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

    public int getHorarioIndex() {
        return horarioIndex;
    }

    public void setHorarioIndex(int horarioIndex) {
        if (horarioIndex >= 0 && horarioIndex <= 9) {
            this.horarioIndex = horarioIndex;
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
                && horarioIndex >= 0
                && horarioIndex <= 9
                && sintomas != null
                && sintomas.trim().length() > 0;
    }

    public String getResumo() {
        if (!dadosValidos()) {
            return "[Dados inválidos]";
        }
        return paciente.getNome()
                + " | " + paciente.getTipo()
                + " | Tutor: " + paciente.getNomeDono()
                + " | Som: " + paciente.emitirSom();
    }

    public String getDetalhes() {
        if (!dadosValidos()) {
            return "Não é possível exibir uma consulta com dados inválidos.";
        }
        return "Tipo de paciente: " + paciente.getTipo() + "\n"
                + paciente.getDadosComuns() + "\n"
                + paciente.getInformacaoEspecifica() + "\n"
                + "Sintomas: " + sintomas + "\n"
                + "Som emitido: " + paciente.emitirSom();
    }
}
