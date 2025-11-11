public class PedidoDeCadastro extends Comunicado {
    private static final long serialVersionUID = 1L;

    public final String nomeCompleto;
    public final String email;
    public final String senha;
    public final String telefone;
    public final String cpfCnpj;
    public final String nomeEmpresa;
    public final String endereco;
    public final double hectares;
    public final String cultura;

    public PedidoDeCadastro(String nomeCompleto, String email, String senha, String telefone, String cpfCnpj,
                            String nomeEmpresa, String endereco, double hectares, String cultura) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpfCnpj = cpfCnpj;
        this.nomeEmpresa = nomeEmpresa;
        this.endereco = endereco;
        this.hectares = hectares;
        this.cultura = cultura;
    }
}
