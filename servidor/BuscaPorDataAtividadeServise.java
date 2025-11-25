package servidor;

import java.util.List;
import org.bson.Document;
import servidor.dbConection.*;

public class BuscaPorDataAtividadeServise {
    public  List<Document> buscarPorDataAtividadeServise(
            String email,
            String data,
            String dbConnection
    ) throws Exception{
        try{
            List<Document> busca = DBUse.buscarPorData(dbConnection, data , email);
            return busca;
        }
        catch(Exception e){
            throw e;
        }
    }
}
