package servidor;

import comum.Cliente;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioClientesFake implements RepositorioClientes {

    private final Map<String, Cliente> porEmail = new HashMap<>();

    @Override
    public boolean existePorEmail(String email) {
        return porEmail.containsKey(email.toLowerCase());
    }

    @Override
    public void salvar(Cliente cliente) {
        porEmail.put(cliente.getEmail(), cliente); // email já vem em lowerCase
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return Optional.ofNullable(porEmail.get(email.toLowerCase()));
    }

    // Método auxiliar só para os testes
    public Cliente getPorEmail(String email) {
        return porEmail.get(email.toLowerCase());
    }
}
