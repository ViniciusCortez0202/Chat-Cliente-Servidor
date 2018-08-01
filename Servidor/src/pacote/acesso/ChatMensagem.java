
package pacote.acesso;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Cadu
 */
public class ChatMensagem implements Serializable {
    
    private String nome;
    private String mensagem;
    private String destinatario;
    private Set<String> setOnlines = new HashSet<String>();
    private Action action;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    
    
    
    public enum Action
    {
        CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE;
    }
}
