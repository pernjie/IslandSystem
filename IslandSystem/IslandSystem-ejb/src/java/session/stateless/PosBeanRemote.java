/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import entity.Customer;
import entity.Facility;
import entity.Item;
import entity.Region;
import entity.Service;
import entity.TransactionItem;
import entity.TransactionRecord;
import entity.TransactionService;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author pern
 */
@Remote
public interface PosBeanRemote {

    TransactionRecord addTransactionRecord(Facility store, List<TransactionItem> transitems, List<TransactionService> transservices, List<Item> items, List<Service> services, Double amount, String promo, int tender, Customer cust, boolean redeemed);

    Customer getCustomer(String card);

    Item getItem(String id, String type);

    Double getItemPrice(Item item, Region region);

    Region getRegion(Facility store);

    Service getService(String id);

    Double getServicePrice(Service service, Region region);

    Facility getStore(String email);

    int login(String email, String password);

    boolean redeemCake(Customer cust);

    boolean verifyPromo(String code, Region region, String type);
    
}
