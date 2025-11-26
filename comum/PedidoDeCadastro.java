package comum;

public class PedidoDeCadastro extends Comunicado {
    private static final long serialVersionUID = 1L;

    public final String nomeCompleto;
    public final String email;
    public final String data;
    public final String senha;
    public final String telefone;
    public final String cpfCnpj;
    public final double hectares;

    public PedidoDeCadastro(String nomeCompleto, String email, String senha, String telefone, String cpfCnpj, String data,
                            double hectares) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpfCnpj = cpfCnpj;
        this.hectares = hectares;
        this.data = data;
    }
    public String getNomeCompleto() {
        return nomeCompleto;
    }
    public String getEmail() {
        return email;
    }
    public String getSenha() {
        return senha;
    }
    public String getTelefone() {
        return telefone;
    }
    public String getCpfCnpj() {
        return cpfCnpj;
    }
    public double getHectares() {
        return hectares;
    }
}
