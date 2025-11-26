package comum;

import java.util.List;

public class BuscaSemFiltro extends Comunicado{
    public final List<String> resultado;
    public BuscaSemFiltro(List<String> resultado) {
        this.resultado = resultado;
    }
    public List<String> getResultado() {
        return resultado;
    }
}
