import java.util.Optional;

public class LoginService {
    private final RepositorioClientes repo;
    public LoginService(RepositorioClientes repo){ this.repo = repo; }

    public Cliente autenticar(String email, String senha) {
        Optional<Cliente> o = repo.buscarPorEmail(email);
        if (o.isEmpty()) throw new IllegalArgumentException("Usuário não encontrado");
        Cliente c = o.get();
        if (!HashSenha.confere(senha, c.getHashSenha())) throw new IllegalArgumentException("Senha inválida");
        return c;
    }
}
