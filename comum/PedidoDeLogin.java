package comum;

public class PedidoDeLogin extends Comunicado {
    private static final long serialVersionUID = 1L;

    public final String email;
    public final String senha;
    public PedidoDeLogin(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
