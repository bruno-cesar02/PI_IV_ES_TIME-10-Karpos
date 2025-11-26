package comum;

import java.util.List;

public class BuscaDataCusto extends Comunicado{
    public final List<String> resultado;
    public BuscaDataCusto(List<String> resultado) {
        this.resultado = resultado;
    }
    public List<String> getResultado() {
        return resultado;
    }
}
