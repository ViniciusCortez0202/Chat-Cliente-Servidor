package pacote.servico;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.acesso.ChatMensagem;
import pacote.acesso.ChatMensagem.Action;

/**
 *
 * @author Cadu
 */
public class ServidorServico {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    public ServidorServico() {
        try {
            serverSocket = new ServerSocket(5555);
            while (true) {
                System.err.println("Servirdor em esperea");
                socket = serverSocket.accept();
                System.err.println("Cliente conectado");
                new Thread(new ListenerSocket(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            ChatMensagem mensagem = null;
            try {

                while ((mensagem = (ChatMensagem) input.readObject()) != null) {
                    Action action = mensagem.getAction();
                    System.out.println(mensagem.getAction());
                    if (action.equals(Action.CONNECT)) {
                        boolean isConnect = connect(mensagem, output);
                        if (isConnect) {
                            mapOnlines.put(mensagem.getNome(), output);
                            usersOnline(mensagem, output);
                        }
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(mensagem, output);
                    } else if (action.equals(Action.SEND_ONE)) {
                        sendOne(mensagem, output);
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(mensagem);
                    } else if (action.equals(Action.USERS_ONLINE)) {
                        usersOnline(mensagem, output);
                    }
                }
            } catch (IOException ex) {
                disconnect(mensagem, output);
                Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean connect(ChatMensagem mensagem, ObjectOutputStream output) {
        if (mapOnlines.isEmpty()) {
            mensagem.setMensagem("OK");
            sendOne(mensagem, output);
            return true;
        }

        if (mapOnlines.containsKey(mensagem.getNome())) {
            mensagem.setMensagem("ERRO");
            sendOne(mensagem, output);
            return false;
        }

        mensagem.setMensagem("OK");
        sendOne(mensagem, output);

        return true;
    }

    private void disconnect(ChatMensagem mensagem, ObjectOutputStream output) {
        mensagem.setMensagem("Saiu!");
        mapOnlines.remove(mensagem.getNome());

        mensagem.setAction(Action.SEND_ALL);

        sendAll(mensagem);

        System.err.println("Usuário:" + mensagem.getNome() + " saiu do cliente");
    }

    private void sendOne(ChatMensagem mensagem, ObjectOutputStream output) {

        try {
            output.writeObject(mensagem);
        } catch (IOException ex) {
            Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendAll(ChatMensagem mensagem) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                try {
                    kv.getValue().writeObject(mensagem);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }

    private void usersOnline(ChatMensagem mensagem, ObjectOutputStream output) {

        Set<String> nomes = new HashSet<String>();
        String nome = mensagem.getNome();

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            nomes.add(kv.getKey());
        }

        mensagem = new ChatMensagem();
        
        mensagem.setNome(nome);
        mensagem.setSetOnlines(nomes);
        mensagem.setAction(Action.USERS_ONLINE);
        sendAll(mensagem);

    }

}
