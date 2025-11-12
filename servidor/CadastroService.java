package servidor;

import comum.*;
import servidor.dbConection.*;

public class CadastroService {
    private final RepositorioClientes repo;

    public CadastroService(RepositorioClientes repo) { this.repo = repo; }

    public void cadastrar(String nome, String email, String senhaPura, String tel, String doc,
                             String empresa, String endereco, double ha, String cultura) throws Exception {
        ValidarCadastro.validar(nome, email, senhaPura, tel, doc, empresa, endereco, ha, cultura);
        if (repo.existePorEmail(email)) throw new IllegalArgumentException("E-mail já cadastrado");
        String hash = HashSenha.gerar(senhaPura);
        String docNum = ValidarCadastro.normalizarDoc(doc);
        DBUse.inserirUsuario(nome , email , hash);
    }
}
