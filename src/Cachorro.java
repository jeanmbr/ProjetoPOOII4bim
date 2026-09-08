public class Cachorro extends Animal {
    private String porte;

    public Cachorro(String nome, int idade, String nomeDono, String telefoneDono, String porte) {
        super(nome, idade, nomeDono, telefoneDono);
        this.porte = "";
        setPorte(porte);
    }

    public String getPorte() {
        return porte;
    }

    @Override
    public void setIdade(int idade) {
        if (idade >= 0 && idade <= 29) {
            super.setIdade(idade);
        }
    }

    public void setPorte(String porte) {
        if (porte != null) {
            if (porte.trim().equalsIgnoreCase("Pequeno")) {
                this.porte = "Pequeno";
            } else if (porte.trim().equalsIgnoreCase("Médio")) {
                this.porte = "Médio";
            } else if (porte.trim().equalsIgnoreCase("Grande")) {
                this.porte = "Grande";
            }
        }
    }

    @Override
    public String emitirSom() {
        return "Au Au!";
    }

    @Override
    public String getTipo() {
        return "Cachorro";
    }

    @Override
    public String getInformacaoEspecifica() {
        return "Porte: " + porte;
    }

    @Override
    public boolean dadosValidos() {
        return dadosComunsValidos()
                && getIdade() <= 29
                && (porte.equals("Pequeno")
                || porte.equals("Médio")
                || porte.equals("Grande"));
    }
}
