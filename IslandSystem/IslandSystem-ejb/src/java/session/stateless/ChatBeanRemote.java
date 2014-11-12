/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;


import entity.ChatRecord;
import entity.Staff;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author nataliegoh
 */
@Remote
public interface ChatBeanRemote {

    public void persistChatlog(ChatRecord cr);
    public List<ChatRecord> getChat(int type, Staff user);
    public Staff getUser(String email);
}