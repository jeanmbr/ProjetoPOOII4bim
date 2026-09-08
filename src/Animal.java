public abstract class Animal {
    private String nome;
    private int idade;
    private String nomeDono;
    private String telefoneDono;

    public Animal(String nome, int idade, String nomeDono, String telefoneDono) {
        this.nome = "";
        this.idade = -1;
        this.nomeDono = "";
        this.telefoneDono = "";

        setNome(nome);
        setIdade(idade);
        setNomeDono(nomeDono);
        setTelefoneDono(telefoneDono);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome != null && nome.trim().length() > 0) {
            this.nome = nome.trim();
        }
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        if (idade >= 0 && idade <= 38) {
            this.idade = idade;
        }
    }

    public String getNomeDono() {
        return nomeDono;
    }

    public void setNomeDono(String nomeDono) {
        if (nomeDono != null && nomeDono.trim().length() > 0) {
            this.nomeDono = nomeDono.trim();
        }
    }

    public String getTelefoneDono() {
        return telefoneDono;
    }

    public void setTelefoneDono(String telefoneDono) {
        if (telefoneValido(telefoneDono)) {
            this.telefoneDono = telefoneDono.trim();
        }
    }

    private boolean telefoneValido(String telefone) {
        if (telefone == null) {
            return false;
        }

        String texto = telefone.trim();
        if (texto.length() < 8 || texto.length() > 11) {
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

    public boolean dadosComunsValidos() {
        return nome != null
                && nome.trim().length() > 0
                && idade >= 0
                && idade <= 38
                && nomeDono != null
                && nomeDono.trim().length() > 0
                && telefoneValido(telefoneDono);
    }

    public boolean dadosValidos() {
        return dadosComunsValidos();
    }

    public void exibirDadosComuns() {
        System.out.println("Paciente: " + nome);
        System.out.println("Idade: " + idade);
        System.out.println("Tutor: " + nomeDono);
        System.out.println("Telefone do tutor: " + telefoneDono);
    }

    public abstract String emitirSom();

    public abstract String getTipo();

    public abstract String getInformacaoEspecifica();
}
