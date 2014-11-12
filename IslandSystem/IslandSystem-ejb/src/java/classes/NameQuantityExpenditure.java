package classes;

import java.io.Serializable;

public class NameQuantityExpenditure implements Serializable {

    private Long id;
    private String name;
    private Integer quantity;
    private Double expenditure;

    public NameQuantityExpenditure() {
    }

    public NameQuantityExpenditure(String nam, Integer qty, Double exp) {
        this.name = nam;
        this.quantity = qty;
        this.expenditure = exp;
    }

    public NameQuantityExpenditure(Long id, String nam, Integer qty, Double exp) {
        this.id = id;
        this.name = nam;
        this.quantity = qty;
        this.expenditure = exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Double expenditure) {
        this.expenditure = expenditure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
