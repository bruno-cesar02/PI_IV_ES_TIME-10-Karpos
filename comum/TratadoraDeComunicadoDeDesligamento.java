package cliente;

import comum.*;
import comum.Comunicado;
import comum.ComunicarDesligamento;
import servidor.Parceiro;

public class TratadoraDeComunicadoDeDesligamento extends Thread {

    private Parceiro servidor;

    public TratadoraDeComunicadoDeDesligamento(Parceiro servidor) throws Exception {
        if (servidor == null)
            throw new Exception("Servidor ausente");

        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Espia o próximo comunicado sem consumir
                Comunicado c = this.servidor.espie();

                if (c instanceof ComunicarDesligamento) {
                    // Consumimos de fato o comunicado (envie usa o buffer interno)
                    this.servidor.envie();

                    // Mensagem pro lado cliente (se quiser, pode trocar por JSON)
                    System.err.println("Servidor será desligado. Encerrando cliente...");

                    try {
                        this.servidor.adeus();
                    } catch (Exception ignore) {}

                    System.exit(0);
                }

                // Pequena pausa pra não ficar em busy-wait
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {}
            }
        } catch (Exception e) {
            // Se der erro de recepção, provavelmente o servidor caiu
            System.err.println("Conexão com o servidor foi perdida. Encerrando cliente...");
            System.exit(0);
        }
    }
}
