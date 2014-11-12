/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Product;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.RowEditEvent;
import services.ProductService;
import session.stateless.GlobalHqBean;
import util.exception.DetailsConflictException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author dyihoon90
 */
@ManagedBean(name = "productManagerBean")
@ViewScoped
public class ProductManagerBean implements Serializable {

    @EJB
    private GlobalHqBean globalHqBean;
    private ProductService productService;
    private Long id;
    private String name;
    private String longDesc;
    private String shortDesc;
    private String category;
    private String halal;
    private String shelfLife;
    private String itemPerBox;
    private Long newProductId;
    private String statusMessage;
    private Product selectedProduct;
    private List<Product> filteredProducts;
    private List<Product> products;
    private Product newProduct = new Product();

    @PostConstruct
    
    public void init() {
        products = globalHqBean.getAllProducts();
        productService = new ProductService();
    }

    
    public ProductService getProductService() {
        return productService;
    }

    
    public Product getNewProduct() {
        return newProduct;
    }

    
    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }

    
    public List<String> getCategories() {
        return productService.getCategories();
    }

    
    public String getShortDesc() {
        return shortDesc;
    }

    
    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    
    public String getProdPerBox() {
        return itemPerBox;
    }

    
    public void setProdPerBox(String itemPerBox) {
        this.itemPerBox = itemPerBox;
    }

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getHalal() {
        return halal;
    }

    
    public void setHalal(String halal) {
        this.halal = halal;
    }

    
    public String getShelfLife() {
        return shelfLife;
    }

    
    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    
    public List<Product> getFilteredProducts() {
        return filteredProducts;
    }

    
    public void setFilteredProducts(List<Product> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }

    
    public String getStatusMessage() {
        return statusMessage;
    }

    
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    
    public GlobalHqBean getGlobalHqBean() {
        return globalHqBean;
    }

    
    public void setGlobalHqBean(GlobalHqBean globalHqBean) {
        this.globalHqBean = globalHqBean;
    }

    
    public List<Product> getProducts() {
        return products;
    }

    
    public Product getSelectedProduct() {
        return selectedProduct;
    }

    
    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    /**
     * Creates a new instance of ProductManagerBean
     */
    public ProductManagerBean() {

    }

    
    public Long getNewProductId() {
        return newProductId;
    }

    
    public void setNewProductId(Long newProductId) {
        this.newProductId = newProductId;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getLongDesc() {
        return longDesc;
    }

    
    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    
    public String getCategory() {
        return category;
    }

    
    public void setCategory(String category) {
        this.category = category;
    }

    
    public String getItemPerBox() {
        return itemPerBox;
    }

    
    public void setItemPerBox(String itemPerBox) {
        this.itemPerBox = itemPerBox;
    }
    
    

    
    public void saveNewProduct(ActionEvent event) {
        Boolean halalId = Boolean.valueOf(halal);
        Integer shelfLifeId = Integer.valueOf(shelfLife);
        try {
            newProduct = new Product();
            newProduct.setName(name);
            newProduct.setLongDesc(longDesc);
            newProduct.setShortDesc(shortDesc);
            newProduct.setCategory(category);
            newProduct.setHalal(halalId);
            newProduct.setShelfLife(shelfLifeId);
            newProduct.setItemPerBox(Integer.valueOf(itemPerBox));
            newProductId = globalHqBean.addNewProduct(newProduct);
            statusMessage = "New Product Saved Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Product Result: "
                    + statusMessage + " (New Product ID is " + newProductId + ")", ""));
        } catch (DetailsConflictException dcx) {
            statusMessage = dcx.getMessage();
            newProductId = -1L;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New Product Result: "
                    + statusMessage, ""));
        } catch (Exception ex) {
            newProductId = -1L;
            statusMessage = "New Product Failed.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New Product Result: "
                    + statusMessage, ""));
            ex.printStackTrace();
        }
    }

    
    public void deleteProduct() {
        try {
            globalHqBean.removeProduct(selectedProduct);
            products = globalHqBean.getAllProducts();
            FacesMessage msg = new FacesMessage("Product Deleted");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (ReferenceConstraintException ex) {
            products = globalHqBean.getAllProducts();
            statusMessage = ex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        }
    }

    
    public void onRowEdit(RowEditEvent event) {

        try {
            globalHqBean.updateProduct((Product) event.getObject());
            products = globalHqBean.getAllProducts();
            FacesMessage msg = new FacesMessage("Product Edited", ((Product) event.getObject()).getId().toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (DetailsConflictException dcx) {
            products = globalHqBean.getAllProducts();
            statusMessage = dcx.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
                    + statusMessage, ""));
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Product) event.getObject()).getId().toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
