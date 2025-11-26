package servidor;

import comum.*;
import org.bson.Document;
import servidor.dbConection.DBUse;

public class LoginService {
    private final RepositorioClientes repo;
    public LoginService(servidor.RepositorioClientes repo){ this.repo = repo; }

    public Cliente autenticar(String email, String senha) {
        Document b = DBUse.loginUsuario(email, senha);
        if (b == null) throw new IllegalArgumentException("Usuário não encontrado");
        Cliente c = new Cliente(b.getString("nome"),
                                b.getString("email"),
                                b.getString("data"),
                                b.getString("senha"),
                                b.getString("telefone"),
                                b.getString("documento"),
                                b.getDouble("tamanhoHectares") );
        if (!HashSenha.confere(senha, c.getHashSenha())) throw new IllegalArgumentException("Senha inválida");
        return c;
    }
}
