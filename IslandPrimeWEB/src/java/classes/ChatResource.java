package classes;

import session.stateless.ChatBeanRemote;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.primefaces.push.EventBus;
import org.primefaces.push.RemoteEndpoint;
import org.primefaces.push.annotation.OnClose;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.OnOpen;
import org.primefaces.push.annotation.PathParam;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PushEndpoint("/{room}/{user}")
@Singleton
public class ChatResource {

    private final Logger logger = LoggerFactory.getLogger(ChatResource.class);
    
    private LinkedList<String> history;
    
      java.util.Date date = new java.util.Date();
    
    //change date into ddMMyyyy format 
    SimpleDateFormat dateformatddMMyyyy = new SimpleDateFormat("E dd/MM/yyyy");
    String date_to_string = dateformatddMMyyyy.format(date);
    
    private final static String CHANNEL = "/{room}/";
   
    @PathParam("room")
    private String room;

    @PathParam("user")
    private String email;

    @Inject
    private ServletContext ctx;
    
    @OnOpen
    public void onOpen(RemoteEndpoint r, EventBus eventBus) throws Exception {   
        ChatUsers users= (ChatUsers) ctx.getAttribute("chatUsers");
        
        eventBus.publish(room + "/*", new Message(String.format("%s has entered the room '%s'",  email, room), true));
    }

    @OnClose
    public void onClose(RemoteEndpoint r, EventBus eventBus) {
        ChatUsers users= (ChatUsers) ctx.getAttribute("chatUsers");
        users.remove(email);
        
        eventBus.publish(room + "/*", new Message(String.format("%s has left the room", email), true));
    }

    @OnMessage(decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
    public Message onMessage(Message message) {
        return message;
    }

}


