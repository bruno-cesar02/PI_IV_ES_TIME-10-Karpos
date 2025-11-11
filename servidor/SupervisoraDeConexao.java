import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SupervisoraDeConexao extends Thread
{
    private Parceiro usuario;
    private final Socket conexao;
    private final ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception
    {
        if (conexao == null)
            throw new Exception("Conexao ausente");
        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.conexao  = conexao;
        this.usuarios = usuarios;
    }

    @Override
    public void run()
    {
        ObjectOutputStream transmissor = null;
        ObjectInputStream  receptor    = null;

        try
        {
            // Cria streams nesta ordem (ObjectOutputStream primeiro evita deadlock)
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
            receptor    = new ObjectInputStream (this.conexao.getInputStream());

            // Registra o parceiro desta conexão
            this.usuario = new Parceiro(this.conexao, receptor, transmissor);
            synchronized (this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            // >>> Integração com AUTENTICAÇÃO <<<
            // Sobe a tratadora que cuida de PedidoDeCadastro/PedidoDeLogin.
            // Ela ficará lendo do mesmo receptor e respondendo pelo transmissor.
            Thread tratadora = new Thread(
                    new TratadoraDePedidos(receptor, transmissor, RepositorioCompartilhado.INSTANCE),
                    "auth-" + this.conexao.getPort()
            );
            tratadora.setDaemon(true);
            tratadora.start();


            tratadora.join();

        }
        catch (Exception e)
        {
            // (opcional) logar erro
        }
        finally
        {
            // Tira o parceiro da lista e encerra recursos
            synchronized (this.usuarios) {
                if (this.usuario != null)
                    this.usuarios.remove(this.usuario);
            }
            try { if (transmissor != null) transmissor.close(); } catch (Exception ignore) {}
            try { if (receptor    != null) receptor.close();    } catch (Exception ignore) {}
            try { this.conexao.close(); } catch (Exception ignore) {}
        }
    }
}
