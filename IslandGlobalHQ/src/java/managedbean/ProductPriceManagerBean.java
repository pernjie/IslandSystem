///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package managedbean;
//
//import entity.Facility;
//import entity.Product;
//import entity.ProductPrice;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.ManagedBean;
//import javax.faces.context.FacesContext;
//import javax.faces.event.ActionEvent;
//import javax.faces.view.ViewScoped;
//import org.primefaces.event.RowEditEvent;
//import session.stateless.GlobalHqBean;
//import util.exception.DetailsConflictException;
//import util.exception.EntityDneException;
//import util.exception.ReferenceConstraintException;
//
///**
// *
// * @author dyihoon90
// */
//@ManagedBean(name = "productPriceManagerBean")
//@ViewScoped
//public class ProductPriceManagerBean implements Serializable {
//
//    @EJB
//    private GlobalHqBean globalHqBean;
//    private Long id;
//    private String store;
//    private String prod;
//    private String storeName;
//    private String prodName;
//    private String priceS;
//    private List<Facility> stores;
//    private List<Product> products;
//    private Long newProductPriceId;
//    private String statusMessage;
//    private ProductPrice selectedProductPrice;
//    private List<ProductPrice> filteredProductPrices;
//    private List<ProductPrice> productPrices;
//    private ProductPrice newProductPrice = new ProductPrice();
//
//    @PostConstruct
//    public void init() {
//        productPrices = globalHqBean.getAllProductPrices();
//        stores = globalHqBean.getAllStores();
//        products = globalHqBean.getAllProducts();
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("stores", stores);
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("products", products);
//    }
//
//    public void saveNewProductPrice(ActionEvent event) {
//        Long storeId = Long.valueOf(store);
//        Long prodId = Long.valueOf(prod);
//        Double price = Double.valueOf(priceS);
// 
//        try {
//            newProductPriceId = globalHqBean.addNewProductPrice(storeId, prodId, price);
//            statusMessage = "New Product Price Record Saved Successfully.";
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ProductPrice Result: "
//                    + statusMessage + " (New ProductPrice ID is " + newProductPriceId + ")", ""));
//        } catch (EntityDneException edx) {
//            statusMessage = edx.getMessage();
//            newProductPriceId = -1L;
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New ProductPrice Result: "
//                    + statusMessage, ""));
//        } catch (DetailsConflictException dcx) {
//            statusMessage = dcx.getMessage();
//            newProductPriceId = -1L;
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Add New ProductPrice Result: "
//                    + statusMessage, ""));
//        } catch (Exception ex) {
//            newProductPriceId = -1L;
//            statusMessage = "New Product Price Record Failed.";
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Add New ProductPrice Result: "
//                    + statusMessage, ""));
//            ex.printStackTrace();
//        }
//    }
//
//    public void deleteProductPrice() {
//        try {
//            globalHqBean.removeProductPrice(selectedProductPrice);
//            productPrices = globalHqBean.getAllProductPrices();
//            FacesMessage msg = new FacesMessage("Product Price Record Deleted");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        } catch (ReferenceConstraintException ex) {
//            productPrices = globalHqBean.getAllProductPrices();
//            statusMessage = ex.getMessage();
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
//                    + statusMessage, ""));
//        }
//    }
//
//    public void onRowEdit(RowEditEvent event) {
//
//        try {
//            globalHqBean.updateProductPrice((ProductPrice) event.getObject());
//            productPrices = globalHqBean.getAllProductPrices();
//            FacesMessage msg = new FacesMessage("ProductPrice Edited", ((ProductPrice) event.getObject()).getId().toString());
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        } catch (DetailsConflictException dcx) {
//            productPrices = globalHqBean.getAllProductPrices();
//            statusMessage = dcx.getMessage();
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Changes not saved: "
//                    + statusMessage, ""));
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//        }
//
//    }
//
//    public void onRowCancel(RowEditEvent event) {
//        FacesMessage msg = new FacesMessage("Edit Cancelled", ((ProductPrice) event.getObject()).getId().toString());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//    }
//
//    public ProductPrice getNewProductPrice() {
//        return newProductPrice;
//    }
//
//    public void setNewProductPrice(ProductPrice newProductPrice) {
//        this.newProductPrice = newProductPrice;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public List<ProductPrice> getFilteredProductPrices() {
//        return filteredProductPrices;
//    }
//
//    public void setFilteredProductPrices(List<ProductPrice> filteredProductPrices) {
//        this.filteredProductPrices = filteredProductPrices;
//    }
//
//    public String getStatusMessage() {
//        return statusMessage;
//    }
//
//    public void setStatusMessage(String statusMessage) {
//        this.statusMessage = statusMessage;
//    }
//
//    public GlobalHqBean getGlobalHqBean() {
//        return globalHqBean;
//    }
//
//    public void setGlobalHqBean(GlobalHqBean globalHqBean) {
//        this.globalHqBean = globalHqBean;
//    }
//
//    public List<ProductPrice> getProductPrices() {
//        return productPrices;
//    }
//
//    public ProductPrice getSelectedProductPrice() {
//        return selectedProductPrice;
//    }
//
//    public void setSelectedProductPrice(ProductPrice selectedProductPrice) {
//        this.selectedProductPrice = selectedProductPrice;
//    }
//
//    /**
//     * Creates a new instance of ProductPriceManagerBean
//     */
//    public ProductPriceManagerBean() {
//
//    }
//
//    public Long getNewProductPriceId() {
//        return newProductPriceId;
//    }
//
//    public void setNewProductPriceId(Long newProductPriceId) {
//        this.newProductPriceId = newProductPriceId;
//    }
//
//    public String getStore() {
//        return store;
//    }
//
//    public void setStore(String store) {
//        this.store = store;
//    }
//
//    public List<Facility> getStores() {
//        return stores;
//    }
//
//    public void setStores(List<Facility> stores) {
//        this.stores = stores;
//    }
//
//    public String getPriceS() {
//        return priceS;
//    }
//
//    public void setPriceS(String priceS) {
//        this.priceS = priceS;
//    }
//
//    public String getProd() {
//        return prod;
//    }
//
//    public void setProd(String prod) {
//        this.prod = prod;
//    }
//
//    public String getStoreName() {
//        return storeName;
//    }
//
//    public void setStoreName(String storeName) {
//        this.storeName = storeName;
//    }
//
//    public String getProdName() {
//        return prodName;
//    }
//
//    public void setProdName(String prodName) {
//        this.prodName = prodName;
//    }
//
//    public List<Facility> getFacilities() {
//        return stores;
//    }
//
//    public void setFacilities(List<Facility> stores) {
//        this.stores = stores;
//    }
//
//    public List<Product> getProducts() {
//        return products;
//    }
//
//    public void setProducts(List<Product> products) {
//        this.products = products;
//    }
//
//    public List<String> getFacNames() {
//        List<String> facName = new ArrayList<String>();
//        for (int i = 0; i < stores.size(); i++) {
//            facName.add(i, stores.get(i).getName());
//        }
//        return facName;
//    }
//
//    public List<String> getProdNames() {
//        List<String> prodName = new ArrayList<String>();
//        for (int i = 0; i < products.size(); i++) {
//            prodName.add(i, products.get(i).getName());
//        }
//        return prodName;
//    }
//
//}
