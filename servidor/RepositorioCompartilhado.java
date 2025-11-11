package servidor;

import comum.*;

public final class RepositorioCompartilhado {

    // Repositório único, usado por todas as threads do servidor
    public static final RepositorioClientes INSTANCE = new RepositorioClientesEmMemoria();

    // construtor privado para impedir criação de instâncias
    private RepositorioCompartilhado() {}
}
