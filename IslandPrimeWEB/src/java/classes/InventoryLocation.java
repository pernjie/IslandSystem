/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import entity.Item;
import entity.Shelf;
import entity.ShelfSlot;

public class InventoryLocation {
    
    private Long invItem;
    private Item item;
    private Integer itemType;
    private String zone;
    private Shelf shelve;
    private ShelfSlot shelfSlot;
    private Integer qty;
    private boolean move;

    public InventoryLocation() {
        this.invItem = 0L;
        this.item = new Item() {};
        this.zone = "";
        this.shelve = new Shelf();
        this.shelfSlot = new ShelfSlot();
        this.qty = 0;
        this.move = false;
    }
    
    public InventoryLocation(Long invItem, Item item, Integer itemType, String zone, Shelf shelf, ShelfSlot shelfSlot, Integer qty, boolean move) {
        this.invItem = invItem;
        this.item = item;
        this.itemType = 0;
        this.zone = zone;
        this.shelve = shelf;
        this.shelfSlot = shelfSlot;
        this.qty = qty;
        this.move = move;
    }
    
    public Long getInvItem() {
        return invItem;
    }

    public void setInvItem(Long invItem) {
        this.invItem = invItem;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Shelf getShelve() {
        return shelve;
    }

    public void setShelve(Shelf shelve) {
        this.shelve = shelve;
    }

    public ShelfSlot getShelfSlot() {
        return shelfSlot;
    }

    public void setShelfSlot(ShelfSlot shelfSlot) {
        this.shelfSlot = shelfSlot;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
