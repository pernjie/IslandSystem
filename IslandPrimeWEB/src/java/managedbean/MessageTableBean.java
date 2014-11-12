package managedbean;
 
import classes.WeekHelper;
import entity.ChatRecord;
import entity.Facility;
import entity.ProductionOrder;
import entity.ProductionRecord;
import entity.Staff;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;
import session.stateless.ChatBean;
import session.stateless.ChatBeanRemote;
import session.stateless.MrpBean;
 
@ManagedBean(name="messageTableBean")
@ViewScoped
public class MessageTableBean implements Serializable {
     
    List<ChatRecord> chatlist;
    @EJB
    private ChatBeanRemote cb;
    private int msgtype;
    private Staff user;
    
    @PostConstruct
    public void init() {
        user = cb.getUser((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email"));
        System.out.println("get chat");
        chatlist = cb.getChat(1, user);
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public List<ChatRecord> getChatlist() {
        return chatlist;
    }
    
    public void updateTable() {
        chatlist = cb.getChat(msgtype, user);
    }
 
}