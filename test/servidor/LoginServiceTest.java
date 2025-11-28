package servidor;

import org.bson.Document;
import servidor.dbConection.DBUse;
import comum.Cliente;
import org.junit.jupiter.api.*;
import servidor.CadastroServiceTest;


import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    // Dados fixos para o usuário de teste
    private static final String EMAIL_OK   = "ivan.teste@teste.com";
    private static final String SENHA_OK   = "SenhaFort3#";
    private static final String TELEFONE   = "19999999999";
    private static final String CPF   = "12345678901";
    private static final String DATA       = "1997-11-16";
    private static final double HECTARES   = 25.0;

    private LoginService loginService;

    @BeforeAll
    static void prepararUsuario() throws Exception {
        // Não cadastrar 2 vezes
        // Primeiro verifica se já existe no banco
        Document d = DBUse.loginUsuario(EMAIL_OK, SENHA_OK);

        if (d == null) {
            RepositorioClientes repoEmMemoria = new RepositorioClientesEmMemoria();
            CadastroService cadastroService = new CadastroService(repoEmMemoria);

            cadastroService.cadastrar(
                    "Ivan",
                    EMAIL_OK,
                    SENHA_OK,
                    "19999999999",
                    "12345678901",
                    "1997-11-16",
                    25.0
            );
        }
    }

    @BeforeEach
    void setUp() {
        RepositorioClientesEmMemoria repoMem = new RepositorioClientesEmMemoria();
        loginService = new LoginService(repoMem);
    }

    @Test
    @DisplayName("CENÁRIO NORMAL: autenticar com email e senha corretos")
    void testCenarioNormal_AutenticarComSucesso() {
        Cliente c = loginService.autenticar(EMAIL_OK, SENHA_OK);

        assertNotNull(c, "Cliente retornado não deveria ser nulo");
        assertEquals(EMAIL_OK, c.getEmail(), "Email do cliente retornado deve ser o mesmo usado no login");
        assertTrue(
                HashSenha.confere(SENHA_OK, c.getHashSenha()),
                "O hash salvo deve conferir com a senha usada no login"
        );
    }

    @Test
    @DisplayName("VARIAÇÃO 1: email inexistente deve lançar 'Usuário não encontrado'")
    void testEmailInexistente() {
        String emailInexistente = "nao.existe+" + System.currentTimeMillis() + "@teste.com";

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> loginService.autenticar(emailInexistente, "qualquerSenha")
        );

        assertTrue(
                e.getMessage().contains("Usuário não encontrado"),
                "Mensagem da exceção deve indicar 'Usuário não encontrado'"
        );
    }

    @Test
    @DisplayName("VARIAÇÃO 2: senha incorreta deve lançar 'Senha inválida'")
    void testSenhaIncorreta() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> loginService.autenticar(EMAIL_OK, "senhaTotalmenteErrada")
        );

        assertTrue(
                e.getMessage().contains("Senha inválida"),
                "Mensagem da exceção deve indicar 'Senha inválida'"
        );
    }
}
