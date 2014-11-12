package classes;
import entity.Item;
import java.io.Serializable;

public class CartItem implements Serializable {
    private Item item;
    private String price;
    private Integer quantity;

    
    public CartItem() {
        
    }
    
    public CartItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    
}
