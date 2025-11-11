package comum;

public class RespostaErro extends Comunicado {
    private static final long serialVersionUID = 1L;
    public final String erro;

    public RespostaErro(String erro) {
        this.erro = erro;
    }

    @Override
    public String toString() {
        return "Erro: " + erro;
    }
}
