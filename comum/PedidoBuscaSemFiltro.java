package comum;

public class PedidoBuscaSemFiltro extends Comunicado{
    public final String email;
    public final String colecao;
    public PedidoBuscaSemFiltro(String email, String colecao) {
        this.email = email;
        this.colecao = colecao;
    }
    public String getEmail() {
        return email;
    }
    public String getColecao() {
        return colecao;
    }
}
