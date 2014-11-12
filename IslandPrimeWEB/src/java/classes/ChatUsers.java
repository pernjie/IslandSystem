package classes;

import session.stateless.ChatBeanRemote;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.primefaces.push.annotation.PathParam;

@ManagedBean
@ApplicationScoped
public class ChatUsers implements Serializable {
    
    private List<String> users;
    
   java.util.Date date = new java.util.Date();
    
   //change date into ddMMyyyy format 
    SimpleDateFormat dateformatddMMyyyy = new SimpleDateFormat("E dd/MM/yyyy");
    String date_to_string = dateformatddMMyyyy.format(date);
    
    @PostConstruct
    public void init() {
        users = new ArrayList<String>();
    }

    public List<String> getUsers() {
        return users;
    }
    
    public void remove(String user) {
        this.users.remove(user);
    }
    
    public void add(String user) {
        this.users.add(user);
    }
        
    public boolean contains(String user) {
        return this.users.contains(user);
    }
  
}