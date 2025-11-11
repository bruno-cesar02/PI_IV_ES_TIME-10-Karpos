package comum;

public class RespostaOk extends Comunicado {
    private static final long serialVersionUID = 1L;
    public final String mensagem;

    public RespostaOk(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return mensagem;
    }
}


