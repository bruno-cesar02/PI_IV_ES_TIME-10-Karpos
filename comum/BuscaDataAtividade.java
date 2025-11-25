package comum;

import java.util.List;

public class BuscaDataAtividade extends Comunicado{
    public final List<String> resultado;
    public BuscaDataAtividade(List<String> resultado) {
        this.resultado = resultado;
    }
    public List<String> getResultado() {
        return resultado;
    }
}
