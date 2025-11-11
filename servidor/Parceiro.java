import com.sun.jdi.event.ExceptionEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Parceiro {
    private Socket conexao;
    private ObjectInputStream receptor;
    private ObjectOutputStream transmissor;

    private Comunicado proximoComunicado = null;

    private Semaphore mutex = new Semaphore(1, true);

    public Parceiro (Socket conexao, ObjectInputStream receptor, ObjectOutputStream transmissor) throws Exception{
        if (conexao == null) throw new Exception ("Conexao ausente");
        if (receptor == null) throw new Exception ("Receptor ausente");
        if (transmissor == null) throw new Exception ("Transmissor ausente");

        this.conexao = conexao;
        this.receptor = receptor;
        this.transmissor = transmissor;
    }

    public Comunicado espie() throws Exception{
        try{
            this.mutex.acquireUninterruptibly();
            if (this.proximoComunicado == null) this.proximoComunicado = (Comunicado) receptor.readObject();
            this.mutex.release();
            return this.proximoComunicado;
        }
        catch (Exception erro){
            throw new Exception ("Erro de recepcao");

        }
    }


    public Comunicado envie() throws Exception{
        try{
            if (this.proximoComunicado == null) this.proximoComunicado = (Comunicado) this.receptor.readObject();
            Comunicado retorno = this.proximoComunicado;
            this.proximoComunicado = null;
            return retorno;
        }
        catch (Exception erro){
            throw new Exception ("Erro de recepcao");
        }
    }


    public void receba(Comunicado c) throws Exception{
        try{
            this.transmissor.writeObject(c);
            this.transmissor.flush();

        }
        catch (IOException erro){
            throw new Exception("Erro de transmissao");
        }
    }
}
