/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Owner
 */
@Remote
public interface MrpBeanRemote {

    List<List> getSalesForecast();
    
}
