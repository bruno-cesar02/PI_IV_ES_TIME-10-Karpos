package comum;

public class PedidoDeLogin extends Comunicado {
    private static final long serialVersionUID = 1L;

    public final String email;
    public final String senha;
    public final String cpfCnpj;

    public PedidoDeLogin(String email, String senha,  String cpfCnpj) {
        this.email = email;
        this.senha = senha;
        this.cpfCnpj = cpfCnpj;
    }
}
