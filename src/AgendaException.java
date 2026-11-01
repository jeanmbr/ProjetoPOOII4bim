public class AgendaException extends Exception {
    private boolean aviso;

    public AgendaException() {
        super("Erro na agenda.");
        this.aviso = false;
    }

    public AgendaException(String texto) {
        super(texto);
        this.aviso = false;
    }

    public AgendaException(String texto, boolean aviso) {
        super(texto);
        this.aviso = aviso;
    }

    public boolean isAviso() {
        return aviso;
    }
}
