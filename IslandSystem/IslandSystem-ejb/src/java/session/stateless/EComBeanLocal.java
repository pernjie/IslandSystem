/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session.stateless;

import entity.Country;
import entity.Customer;
import entity.Item;
import entity.Region;
import entity.RegionItemPrice;
import java.util.List;
import javax.ejb.Local;
import util.exception.DetailsConflictException;

/**
 *
 * @author nataliegoh
 */

public interface EComBeanLocal {
   public Item getItem(Long itemId);
   public Item getItembyString(String itemName);
   public List<Region> getRegions();
   public List<Item> getItems(String regionId);
   public List<RegionItemPrice> getFurnitureDisplays(String regionId);
   public Region getRegion(String regionId);
   public RegionItemPrice getRegionPriceRecord(Item item, Region region);
    public Long addNewCustomer(Customer customer) throws DetailsConflictException;
    public List<Region> getAllRegions();
    public List<Country> getAllCountries();
    public List<Customer> getAllCustomers();
    public void unsubscribe(long custId);
    public Customer getCustomerDetails(String email);
}
