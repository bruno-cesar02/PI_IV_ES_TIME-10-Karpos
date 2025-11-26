package servidor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CadastroServiceTest {

    private CadastroService cadastroService;

    @BeforeEach
    void setUp() {
        // Usa o repositório em memória que JÁ existe no projeto
        RepositorioClientes repoEmMemoria = new RepositorioClientesEmMemoria();
        cadastroService = new CadastroService(repoEmMemoria);
    }

    @Test
    @DisplayName("CENÁRIO NORMAL: Cadastrar usuário com dados válidos não deve lançar exceção")
    void testCenarioNormal_CadastroComSucesso() {
        String nome = "João da Silva";
        String email = "joao" + System.currentTimeMillis() + "@teste.com";
        String senha = "senha12345";
        String telefone = "19999998889";
        String cpfCnpj = "12345632801";
        String data = "2025-11-26";
        double hectares = 40.0;

        assertDoesNotThrow(() -> {
            cadastroService.cadastrar(nome, email, senha, telefone, cpfCnpj, data, hectares);
        });
    }

    @Test
    @DisplayName("VARIAÇÃO 1: Cadastrar com email inválido deve lançar 'Email inválido'")
    void testVariacao1_EmailInvalido() {
        String nome = "Maria";
        String emailInvalido = "maria.sem.arroba.com"; // sem @
        String senha = "senha123";
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        String data = "2025-11-26";
        double hectares = 20.0;

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(nome, emailInvalido, senha, telefone, cpfCnpj, data, hectares);
        });

        assertTrue(e.getMessage().contains("Email inválido"));
    }

    @Test
    @DisplayName("VARIAÇÃO 2: Cadastrar com senha curta deve lançar 'Senha muito curta'")
    void testVariacao2_SenhaCurta() {
        String nome = "Juliano";
        String email = "juliano" + System.currentTimeMillis() + "@teste.com";
        String senhaCurta = "123"; // < 6
        String telefone = "19999999999";
        String cpfCnpj = "12345678901";
        String data = "2025-11-26";
        double hectares = 10.0;

        Exception e = assertThrows(Exception.class, () -> {
            cadastroService.cadastrar(nome, email, senhaCurta, telefone, cpfCnpj, data, hectares);
        });

        assertTrue(e.getMessage().contains("Senha muito curta"));
    }

    // NÃO USE esse cenário de "email já cadastrado" na prova a menos que o DBUse.loginUsuario
    // esteja bem configurado com o Mongo e populado. Caso contrário, comente.
}
