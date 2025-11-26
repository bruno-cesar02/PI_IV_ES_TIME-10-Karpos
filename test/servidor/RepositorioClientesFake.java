package servidor;

import comum.Cliente;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação FAKE de RepositorioClientes só para testes.
 * Guarda clientes em memória usando um Map, indexado por email.
 */
public class RepositorioClientesFake implements RepositorioClientes {

    private final Map<String, Cliente> porEmail = new HashMap<>();

    @Override
    public boolean existePorEmail(String email) {
        return porEmail.containsKey(email);
    }

    @Override
    public void salvar(Cliente cliente) {
        porEmail.put(cliente.getEmail(), cliente);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return Optional.empty();
    }

    // Método auxiliar só para os testes
    public Cliente getPorEmail(String email) {
        return porEmail.get(email);
    }

    // Se a interface/classe RepositorioClientes tiver mais métodos obrigatórios,
    // o IntelliJ vai avisar. Aí você implementa aqui com corpo vazio ou comportamento simples.
}
