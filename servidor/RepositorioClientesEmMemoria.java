package servidor;

import comum.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioClientesEmMemoria implements RepositorioClientes {
    private final ConcurrentHashMap<String, Cliente> porEmail = new ConcurrentHashMap<>();

    @Override
    public void salvar(Cliente cliente) { porEmail.put(cliente.getEmail().toLowerCase(), cliente); }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(porEmail.get(email.toLowerCase()));
    }

    @Override
    public boolean existePorEmail(String email) {
        return porEmail.containsKey(email.toLowerCase());
    }
}
