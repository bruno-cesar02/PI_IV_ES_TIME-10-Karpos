package comum;

public class PedidoBuscaDataAtividade extends Comunicado{
    public final String data;
    public final String email;
    public PedidoBuscaDataAtividade(String data, String email){
        this.data = data;
        this.email = email;
    }
    public String getData(){
        return data;
    }
    public String getEmail(){
        return email;
    }
}
