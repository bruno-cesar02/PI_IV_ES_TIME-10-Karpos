package comum;

import java.io.Serializable;
import java.util.Objects;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nomeCompleto;
    private final String email;
    private final String data;
    private final String hashSenha;   // senha com hash (PBKDF2)
    private final String telefone;
    private final String documento;   // CPF/CNPJ somentes dígitos
    private final double tamanhoHectares;

    public Cliente(
            String nomeCompleto,
            String email,
            String data,
            String hashSenha,
            String telefone,
            String documento,
            double tamanhoHectares
    ) {
        this.nomeCompleto = Objects.requireNonNull(nomeCompleto, "nomeCompleto");
        this.email = Objects.requireNonNull(email, "email").toLowerCase();
        this.hashSenha = Objects.requireNonNull(hashSenha, "hashSenha");
        this.telefone = Objects.requireNonNull(telefone, "telefone");
        this.documento = Objects.requireNonNull(documento, "documento");
        this.tamanhoHectares = tamanhoHectares;
        this.data = Objects.requireNonNull(data, "data");
    }


    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public String getHashSenha() { return hashSenha; }
    public String getTelefone() { return telefone; }
    public String getDocumento() { return documento; }
    public double getTamanhoHectares() { return tamanhoHectares; }

    public String getData() {
        return data;
    }
    }
