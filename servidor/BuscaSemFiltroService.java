package servidor;

import java.util.List;
import org.bson.Document;
import servidor.dbConection.*;

public class BuscaSemFiltroService {
    public List<Document> buscaSemFiltro(
            String email,
            String dbColection
    ) throws Exception{
        try{
            return DBUse.buscarSemFiltro(dbColection, email);
        }
        catch(Exception e){
            throw e;
        }
    }
}

