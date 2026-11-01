public class Gato extends Animal {
    private String temperamento;

    public Gato(String nome, int idade, String nomeDono, String telefoneDono, String temperamento) {
        super(nome, idade, nomeDono, telefoneDono);
        this.temperamento = "";
        setTemperamento(temperamento);
    }

    public String getTemperamento() {
        return temperamento;
    }

    @Override
    public void setIdade(int idade) {
        if (idade >= 0 && idade <= 38) {
            super.setIdade(idade);
        }
    }

    public void setTemperamento(String temperamento) {
        if (temperamento != null && temperamento.trim().length() > 0) {
            this.temperamento = temperamento.trim();
        }
    }

    @Override
    public String emitirSom() {
        return "Miau!";
    }

    @Override
    public String getTipo() {
        return "Gato";
    }

    @Override
    public String getCaracteristica() {
        return temperamento;
    }

    @Override
    public String getInformacaoEspecifica() {
        return "Temperamento: " + temperamento;
    }

    @Override
    public boolean dadosValidos() {
        return dadosComunsValidos()
                && getIdade() <= 38
                && temperamento != null
                && temperamento.trim().length() > 0;
    }
}
