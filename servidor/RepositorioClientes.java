import java.util.Optional;

public interface RepositorioClientes {
    void salvar(Cliente cliente) throws Exception;
    Optional<Cliente> buscarPorEmail(String email);
    boolean existePorEmail(String email);
}
