package servidor;

import servidor.dbConection.DBUse;

public class CadastroCustoServise {
    public CadastroCustoServise() {
    }
    public boolean addCusto( String data, String atividade, String texto, String usuarioEmail , Double custo, String aa) {
        try {
            Boolean cadastrar = DBUse.inserirCusto(data, atividade, texto, usuarioEmail, custo, aa);
            return cadastrar;
        }
        catch (Exception e) {
            throw e;
        }
    }
}
