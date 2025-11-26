package comum;

public class PedidoCadastroCusto extends Comunicado{
    public final String data;
    public final String tipoAtividade;
    public final String texto;
    public final Double valor;
    public final String usuarioEmail;

    public PedidoCadastroCusto(String data, String tipoAtividade, String texto, String usuarioEmail ,  Double valor) {
        this.data = data;
        this.tipoAtividade = tipoAtividade;
        this.texto = texto;
        this.usuarioEmail = usuarioEmail;
        this.valor = valor;
    }
    public String getData() {
        return data;
    }
    public String getTipoAtividade() {
        return tipoAtividade;
    }
    public String getTexto() {
        return texto;
    }
    public String getUsuarioEmail() {
        return usuarioEmail;
    }
    public Double getValor() {
        return valor;
    }
}
