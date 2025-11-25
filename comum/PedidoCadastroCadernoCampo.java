package comum;

public class PedidoCadastroCadernoCampo extends Comunicado {
    public final String data;
    public final String tipoAtividade;
    public final String texto;
    public final String usuarioEmail;

    public PedidoCadastroCadernoCampo(String data, String tipoAtividade, String texto, String usuarioEmail) {
        this.data = data;
        this.tipoAtividade = tipoAtividade;
        this.texto = texto;
        this.usuarioEmail = usuarioEmail;
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
}
