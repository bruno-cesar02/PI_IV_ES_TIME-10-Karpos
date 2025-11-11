import java.io.Serializable;
import java.util.Objects;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nomeCompleto;
    private final String email;
    private final String hashSenha;   // senha com hash (PBKDF2)
    private final String telefone;
    private final String documento;   // CPF/CNPJ somentes dígitos
    private final String nomeEmpresa;
    private final String endereco;
    private final double tamanhoHectares;
    private final String cultura;

    public Cliente(
            String nomeCompleto,
            String email,
            String hashSenha,
            String telefone,
            String documento,
            String nomeEmpresa,
            String endereco,
            double tamanhoHectares,
            String cultura
    ) {
        this.nomeCompleto = Objects.requireNonNull(nomeCompleto, "nomeCompleto");
        this.email = Objects.requireNonNull(email, "email").toLowerCase();
        this.hashSenha = Objects.requireNonNull(hashSenha, "hashSenha");
        this.telefone = Objects.requireNonNull(telefone, "telefone");
        this.documento = Objects.requireNonNull(documento, "documento");
        this.nomeEmpresa = Objects.requireNonNull(nomeEmpresa, "nomeEmpresa");
        this.endereco = Objects.requireNonNull(endereco, "endereco");
        this.tamanhoHectares = tamanhoHectares;
        this.cultura = Objects.requireNonNull(cultura, "cultura");
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public String getHashSenha() { return hashSenha; }
    public String getTelefone() { return telefone; }
    public String getDocumento() { return documento; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getEndereco() { return endereco; }
    public double getTamanhoHectares() { return tamanhoHectares; }
    public String getCultura() { return cultura; }
}
