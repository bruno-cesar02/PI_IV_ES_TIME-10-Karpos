package comum;

public class ClienteLogado extends Comunicado {
    private static final long serialVersionUID = 1L;
    public final Cliente cliente;

    public ClienteLogado(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Cliente logado: " + cliente.getNomeCompleto() + " (" + cliente.getEmail() + ")";
    }
}
