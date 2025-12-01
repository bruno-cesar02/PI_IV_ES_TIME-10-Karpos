package comum;

public class PedidoDelete extends Comunicado {
    public final String data;
    public final String email;
    public final String atividade;
    public final String collection;

    public PedidoDelete(String data, String email, String atividade , String collection) {
        this.data = data;
        this.email = email;
        this.atividade = atividade;
        this.collection = collection;
    }
    public String getData(){
        return data;
    }
    public String getEmail() {
        return email;
    }
    public String getAtividade() {
        return atividade;
    }
    public String getCollection() {
        return collection;
    }
}
