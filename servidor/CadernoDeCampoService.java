package servidor;

import servidor.dbConection.DBUse;

public class CadernoDeCampoService {
    public CadernoDeCampoService() {};
    public boolean addAtividade(String data, String tipoAtividade, String texto, String usuarioEmail)throws Exception{
        try {
            Boolean cadastrar = DBUse.inserirAtividade(data, tipoAtividade, texto, usuarioEmail);
            return cadastrar;
        }
        catch (Exception e) {
            throw e;
        }
    }
}
