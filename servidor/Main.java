
import java.util.Scanner;
import dbConection.*;

public class Main {
    public static void main(String[] args) {
        while(true) {
            try {
                DBUse.inserirUsuario("henrique", "henrique.soarres@email.com", "123456");
            } catch (Exception e) {
                System.err.println("\n❌ Erro ao tentar inserir o usuário:");
                e.printStackTrace();
            }
            Scanner input = new Scanner(System.in);
            System.out.println("Digite seu numero");
            int opcao = input.nextInt();

            if (opcao == 1) {
                break;
            }
        }
        DBConection.fecharConexao();
        }
    }
