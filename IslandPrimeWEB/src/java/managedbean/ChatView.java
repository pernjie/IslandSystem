package managedbean;

import classes.ChatUsers;
import classes.Message;
import entity.ChatRecord;
import entity.Staff;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.primefaces.push.annotation.PathParam;
import session.stateless.ChatBeanRemote;

@LocalBean
@ManagedBean
@ViewScoped
public class ChatView implements Serializable {

    //private final PushContext pushContext = PushContextFactory.getDefault().getPushContext();
    private final EventBus eventBus = EventBusFactory.getDefault().eventBus();

    @ManagedProperty("#{chatUsers}")
    private ChatUsers users;
    private String announceMessage;
    private String privateMessage;
    private String globalMessage;
    private String email;
    private Staff staffuser;
    private String password;
    private boolean loggedIn;
    private String privateUser;
    @EJB
    private ChatBeanRemote chatbean;

    private List<ChatRecord> chatlog = new ArrayList<ChatRecord>();
    
    private final static String CHANNEL = "/{IslandPublic}/";
    java.util.Date date = new java.util.Date();

    //change date into ddMMyyyy format 
    SimpleDateFormat dateformatddMMyyyy = new SimpleDateFormat("E dd/MM/yyyy");
    String date_to_string = dateformatddMMyyyy.format(date);

    //change date for chat timestamp
    SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss.S a zzz");

    @PathParam("room")
    private String room;

    @PostConstruct
    public void init() {
        email = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email");
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if (users.contains(email)) {
            loggedIn = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email taken", "Try with another email."));
            requestContext.update("growl");
        } else {
            users.add(email);
            requestContext.execute("PF('subscriber').connect('/" + email + "')");
            loggedIn = true;
        }
        
        staffuser = chatbean.getUser(email);
    }

    public ChatUsers getUsers() {
        return users;
    }

    public void setUsers(ChatUsers users) {
        this.users = users;
    }

    public String getPrivateUser() {
        return privateUser;
    }

    public void setPrivateUser(String privateUser) {
        this.privateUser = privateUser;
    }

    public String getGlobalMessage() {
        return globalMessage;
    }

    public void setGlobalMessage(String globalMessage) {
        this.globalMessage = globalMessage;
    }

    public String getAnnounceMessage() {
        return announceMessage;
    }

    public void setAnnounceMessage(String announceMessage) {
        this.announceMessage = announceMessage;
    }

    public String getPrivateMessage() {
        return privateMessage;
    }

    public void setPrivateMessage(String privateMessage) {
        this.privateMessage = privateMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String email) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void sendGlobal() {
        System.out.println("sdkfjlmsg: " + globalMessage);
        date = new java.util.Date();
        eventBus.publish(CHANNEL + "*", new Message(ft.format(date) + " " + email + ": " + globalMessage, true));
        ChatRecord cr = new ChatRecord();
        cr.setChannel(CHANNEL);
        cr.setMessage(globalMessage);
        cr.setMsgTime(date_to_string + "  " + ft.format(date));
        cr.setSender(staffuser);
        chatbean.persistChatlog(cr);
        globalMessage = null;
    }

    public void sendAnnounce() {
        date = new java.util.Date();

        eventBus.publish(CHANNEL + "*", new Message("ANNOUNCEMENT: " + ft.format(date) + " " + email + ": " + announceMessage, true));
        ChatRecord cr = new ChatRecord();
        cr.setChannel("announcement");
        cr.setMessage(announceMessage);
        cr.setMsgTime(date_to_string + "  " + ft.format(date));
        cr.setSender(staffuser);
        chatbean.persistChatlog(cr);
        announceMessage = null;
    }

    public void sendPrivate() {
        date = new java.util.Date();
        eventBus.publish(CHANNEL + privateUser, new Message(ft.format(date) + " " + "[PM] " + email + ": " + privateMessage, true));
        
        ChatRecord cr = new ChatRecord();
        cr.setChannel("PM");
        cr.setMessage(privateMessage);
        cr.setMsgTime(date_to_string + "  " + ft.format(date));
        cr.setSender(staffuser);
        cr.setRecipient(chatbean.getUser(privateUser));
        chatbean.persistChatlog(cr);

        privateMessage = null;
    }

    public void login() {
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if (users.getUsers().isEmpty()) {
        }
        if (users.contains(email)) {
            loggedIn = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email taken", "Try with another username."));
            requestContext.update("growl");
        } else {
            users.add(email);
            requestContext.execute("PF('subscriber').connect('/" + email + "')");
            loggedIn = true;
        }
    }

    public void disconnect() {
        //chatbean.persistChatlog(chatlog);
        //remove user and update ui
        users.remove(email);
        RequestContext.getCurrentInstance().update("form:users");

        //push leave information
        eventBus.publish(CHANNEL + "*", email + " left the channel.");

        //prints out elements in history array
        if (users.getUsers().isEmpty()) {
            loggedIn = false;
            email = null;
        }

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../common/CI_Index_Page.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}