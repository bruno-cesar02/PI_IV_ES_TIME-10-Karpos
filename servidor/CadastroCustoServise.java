package servidor;

import servidor.dbConection.DBUse;

public class CadastroCustoServise {
    public CadastroCustoServise() {
    }
    public boolean addCusto( String data, String atividade, String texto, String usuarioEmail , Double custo) {
        try {
            Boolean cadastrar = DBUse.inserirCusto(data, atividade, texto, usuarioEmail, custo);
            return cadastrar;
        }
        catch (Exception e) {
            throw e;
        }
    }
}
