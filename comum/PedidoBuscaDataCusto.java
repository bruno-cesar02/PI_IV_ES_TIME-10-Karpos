package comum;

public class PedidoBuscaDataCusto extends Comunicado {
    public final String data;
    public final String email;
    public PedidoBuscaDataCusto(String data, String email){
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
