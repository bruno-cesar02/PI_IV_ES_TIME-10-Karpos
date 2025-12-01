package servidor;

import comum.Cliente;
import org.bson.Document;
import servidor.dbConection.DBUse;

public class DeleteServise {
    public DeleteServise(){}

    public Boolean deletear(String email, String categoria, String atividade, String data) {
        try{
            Boolean b = DBUse.deleteItem(data, categoria, atividade , email);
            if (b == false) throw new IllegalArgumentException("Usuário não encontrado");
            return b;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
