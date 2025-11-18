package servidor;

import comum.*;
import org.bson.Document;
import servidor.dbConection.DBUse;

import java.util.Optional;

public class LoginService {
    private final RepositorioClientes repo;
    public LoginService(RepositorioClientes repo){ this.repo = repo; }

    public Cliente autenticar(String email, String senha) {
        Document b = DBUse.loginUsuario(email, senha);
        if (b == null) throw new IllegalArgumentException("Usuário não encontrado");
        Cliente c = new Cliente(b.getString("nome"),
                                b.getString("email"),
                                b.getString("senha"),
                                b.getString("telefone"),
                                b.getString("documento"),
                                b.getString("nomeEmpresa"),
                                b.getString("endereco"),
                                b.getDouble("tamanhoHectares"),
                                b.getString("categoria") );
        if (!HashSenha.confere(senha, c.getHashSenha())) throw new IllegalArgumentException("Senha inválida");
        return c;
    }
}
