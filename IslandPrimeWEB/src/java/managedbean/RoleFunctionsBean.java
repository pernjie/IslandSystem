/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author nataliegoh
 */
@ManagedBean(name = "roleFunctionsBean")
@SessionScoped
public class RoleFunctionsBean implements Serializable {

    /**
     * Creates a new instance of RoleFunctionsBean
     */
    public RoleFunctionsBean() {
    }
    
    private String role1;
    private String role2;
    private String role3;
    private List<String> rolesSet;
    
    private boolean checkSA;
    private boolean checkGHQ;
    private boolean checkRHQ;
    
    private boolean checkMRP;
    private boolean checkForecastFPdt;
    private boolean checkPdtionOrderProcessing;
    private boolean checkForecatRPdt;
    private boolean checkPOProcessing;
    private boolean checkViewPdtionRecords;
    private boolean checkViewPurchRecords;
    
    private boolean checkSCM;
    private boolean checkSCMGeneratePdtPO;
    private boolean checkGenerateRawMatPO;
    private boolean checkGenerateDeliverySchedule;
    private boolean checkSCMViewPdtPO;
    private boolean checkViewRawMatPO;
    private boolean checkViewDistributnF;
    private boolean checkViewDistributnP;
    private boolean checkViewUnpaidBills;
    private boolean checkAddBill;
    private boolean checkProductionOperation;
    
    private boolean checkSCMInvenRestock;
    private boolean checkSCMReceiveSInven;
    private boolean checkSCMViewRestockLoc;
    
    private boolean checkSCMShelf;
    private boolean checkSCMAddShelf;
    private boolean checkSCMViewShelf;
    
    private boolean checkSCMShelfType;
    private boolean checkSCMAddShelfType;
    private boolean checkSCMViewShelfType;
    
    private boolean checkSCMAdHocOrdering;
    private boolean checkSCMAdHocPdtnPlan;
    
    private boolean checkInvenRestock;
    private boolean checkReceiveSInven;
    private boolean checkViewRestockLoc;
    
    private boolean checkShelf;
    private boolean checkAddShelf;
    private boolean checkViewShelf;
    
    private boolean checkShelfType;
    private boolean checkAddShelfType;
    private boolean checkViewShelfType;
    
    private boolean checkAdHocOrdering;
    private boolean checkAdHocPdtnPlan;
    
    private boolean checkInventory;
    private boolean checkInvenRecord;
    private boolean checkAddInvenRec;
    private boolean checkViewInvenRec;
    private boolean checkGeneratePdtPO;
    private boolean checkViewPdtPO;
    
    private boolean checkReceiveMFInven;
    private boolean checkReplenishInven;
    private boolean checkConfirmRestock;
    
    private boolean checkSupplier;
    private boolean checkViewDeliverySchedule;
    
    private boolean checkOPCRM;
    private boolean checkServiceRec;
    private boolean checkViewServiceRec;
    private boolean checkAddServiceRec;
    
    private boolean checkPdtnOrders;
    private boolean checkViewPdtnOrders;
    private boolean checkAddPdtnOrders;
    
    private boolean checkRegionTransacRec;
    private boolean checkViewRegionTransacRec;
    
    private boolean checkCampaigns;
    private boolean checkViewCampaigns;
    private boolean checkAddCampaigns;
    
    private boolean checkCustomers;
    private boolean checkViewCustomers;
    private boolean checkSendMktEmail;
    
    private boolean checkSupplierExt;
    
    private boolean checkAnal;
    private boolean checkKitchen;
    
    public List<String> setAccess() {
        rolesSet = new ArrayList<String>();
        //List<String> allRoles = new ArrayList<String>();
        //allRoles = roleService.getRoles();
        rolesSet.add("Profile");
        if (getRole1().equals("System Admin"))
            rolesSet.add("System Admin");
        if (getRole2() != null && getRole2().equals("System Admin"))
            rolesSet.add("System Admin");
        if (getRole3() != null && getRole3().equals("System Admin"))
            rolesSet.add("System Admin");
        
        if (getRole1().equals("Global HQ")) {
            rolesSet.add("Global HQ");
        }
        if (getRole2() != null && getRole2().equals("Global HQ")) {
            rolesSet.add("Global HQ");
        }
        if (getRole3() != null && getRole3().equals("Global HQ")) {
            rolesSet.add("Global HQ");
        }
        
        if (getRole1().equals("Regional HQ")) {
            rolesSet.add("Regional HQ");
        }
        if (getRole2() != null && getRole2().equals("Regional HQ")) {
            rolesSet.add("Regional HQ");
        }
        if (getRole3() != null && getRole3().equals("Regional HQ")) {
            rolesSet.add("Regional HQ");
        }
        
        if (getRole1().equals("MRP Controller")) {
            rolesSet.add("MRP");
            rolesSet.add("ForecastFPdt");
            rolesSet.add("PdtionOrderProcessing");
            rolesSet.add("ForecatRPdt");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("SCMGeneratePdtPO");
            rolesSet.add("GenerateRawMatPO");
            rolesSet.add("GenerateDeliverySchedule");
            rolesSet.add("SCMViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("SCMViewUnpaidBills");
            rolesSet.add("AddBill");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMAddShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMAddShelfType");
            rolesSet.add("SCMViewShelfType");
            
            rolesSet.add("SCMAdHocOrdering");
            rolesSet.add("SCMAdHocPdtnPlan");
        }
        if (getRole2() != null && getRole2().equals("MRP Controller")) {
            rolesSet.add("MRP");
            rolesSet.add("ForecastFPdt");
            rolesSet.add("PdtionOrderProcessing");
            rolesSet.add("ForecatRPdt");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("SCMGeneratePdtPO");
            rolesSet.add("GenerateRawMatPO");
            rolesSet.add("GenerateDeliverySchedule");
            rolesSet.add("SCMViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("SCMViewUnpaidBills");
            rolesSet.add("AddBill");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMAddShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMAddShelfType");
            rolesSet.add("SCMViewShelfType");
            
            rolesSet.add("SCMAdHocOrdering");
            rolesSet.add("SCMAdHocPdtnPlan");
        }
        if (getRole3() != null && getRole3().equals("MRP Controller")) {
            rolesSet.add("MRP");
            rolesSet.add("ForecastFPdt");
            rolesSet.add("PdtionOrderProcessing");
            rolesSet.add("ForecatRPdt");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("SCMGeneratePdtPO");
            rolesSet.add("GenerateRawMatPO");
            rolesSet.add("GenerateDeliverySchedule");
            rolesSet.add("SCMViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("SCMViewUnpaidBills");
            rolesSet.add("AddBill");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMAddShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMAddShelfType");
            rolesSet.add("SCMViewShelfType");
            
            rolesSet.add("SCMAdHocOrdering");
            rolesSet.add("SCMAdHocPdtnPlan");
        }
        
        if (getRole1().equals("MF Manager")) {
            rolesSet.add("MRP");
            rolesSet.add("ForecastFPdt");
            rolesSet.add("PdtionOrderProcessing");
            rolesSet.add("ForecatRPdt");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("SCMGeneratePdtPO");
            rolesSet.add("GenerateRawMatPO");
            rolesSet.add("GenerateDeliverySchedule");
            rolesSet.add("SCMViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("SCMViewUnpaidBills");
            rolesSet.add("AddBill");
            rolesSet.add("ProductionOperation");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMAddShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMAddShelfType");
            rolesSet.add("SCMViewShelfType");
            
            rolesSet.add("SCMAdHocOrdering");
            rolesSet.add("SCMAdHocPdtnPlan");
        }
        if (getRole2() != null && getRole2().equals("MF Manager")) {
            rolesSet.add("MRP");
            rolesSet.add("ForecastFPdt");
            rolesSet.add("PdtionOrderProcessing");
            rolesSet.add("ForecatRPdt");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("SCMGeneratePdtPO");
            rolesSet.add("GenerateRawMatPO");
            rolesSet.add("GenerateDeliverySchedule");
            rolesSet.add("SCMViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("SCMViewUnpaidBills");
            rolesSet.add("AddBill");
            rolesSet.add("ProductionOperation");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMAddShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMAddShelfType");
            rolesSet.add("SCMViewShelfType");
            
            rolesSet.add("SCMAdHocOrdering");
            rolesSet.add("SCMAdHocPdtnPlan");
        }
        if (getRole3() != null && getRole3().equals("MF Manager")) {
            rolesSet.add("MRP");
            rolesSet.add("ForecastFPdt");
            rolesSet.add("PdtionOrderProcessing");
            rolesSet.add("ForecatRPdt");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("SCMGeneratePdtPO");
            rolesSet.add("GenerateRawMatPO");
            rolesSet.add("GenerateDeliverySchedule");
            rolesSet.add("SCMViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("SCMViewUnpaidBills");
            rolesSet.add("AddBill");
            rolesSet.add("ProductionOperation");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMAddShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMAddShelfType");
            rolesSet.add("SCMViewShelfType");
            
            rolesSet.add("SCMAdHocOrdering");
            rolesSet.add("SCMAdHocPdtnPlan");
        }  
        
        if (getRole1().equals("MF Staff")) {
            rolesSet.add("MRP");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            rolesSet.add("AddBill");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMViewShelfType");
        }
        if (getRole2() != null && getRole2().equals("MF Staff")) {
            rolesSet.add("MRP");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            rolesSet.add("AddBill");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMViewShelfType");
        }
        if (getRole3() != null && getRole3().equals("MF Staff")) {
            rolesSet.add("MRP");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            rolesSet.add("AddBill");
            
            rolesSet.add("SCMInvenRestock");
            rolesSet.add("SCMReceiveSInven");
            rolesSet.add("SCMViewRestockLoc");
            
            rolesSet.add("SCMShelf");
            rolesSet.add("SCMViewShelf");
            
            rolesSet.add("SCMShelfType");
            rolesSet.add("SCMViewShelfType");
        }
        
        if (getRole1().equals("MF Purchasing Manager")) {
            rolesSet.add("MRP");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            rolesSet.add("AddBill");
        }
        if (getRole2() != null && getRole2().equals("MF Purchasing Manager")) {
            rolesSet.add("MRP");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            rolesSet.add("AddBill");
        }
        if (getRole3() != null && getRole3().equals("MF Purchasing Manager")) {
            rolesSet.add("MRP");
            rolesSet.add("POProcessing");
            rolesSet.add("ViewPdtionRecords");
            rolesSet.add("ViewPurchRecords");
            
            rolesSet.add("SCM");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewRawMatPO");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            rolesSet.add("AddBill");
        }
        
        if (getRole1().equals("Store Manager")) {
            rolesSet.add("SCM");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            
            rolesSet.add("Inventory");
            rolesSet.add("InvenRecord");
            rolesSet.add("AddInvenRec");
            rolesSet.add("ViewInvenRec");
            
            rolesSet.add("InvenRestock");
            rolesSet.add("ReceiveSInven");
            rolesSet.add("ViewRestockLoc");
            rolesSet.add("ReceiveMFInven");
            rolesSet.add("ReplenishInven");
            rolesSet.add("ConfirmRestock");
            
            rolesSet.add("Shelf");
            rolesSet.add("AddShelf");
            rolesSet.add("ViewShelf");
            
            rolesSet.add("ShelfType");
            rolesSet.add("AddShelfType");
            rolesSet.add("ViewShelfType");
            
            rolesSet.add("AdHocOrdering");
            rolesSet.add("AdHocPdtnPlan");
            
            rolesSet.add("Supplier");
            rolesSet.add("GeneratePdtPO");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewDeliverySchedule");
            
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("RegionTransacRec");
            rolesSet.add("ViewRegionTransacRec");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
        }
        if (getRole2() != null && getRole2().equals("Store Manager")) {
            rolesSet.add("SCM");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            
            rolesSet.add("Inventory");            
            rolesSet.add("InvenRecord");
            rolesSet.add("AddInvenRec");
            rolesSet.add("ViewInvenRec");
            
            rolesSet.add("InvenRestock");
            rolesSet.add("ReceiveSInven");
            rolesSet.add("ViewRestockLoc");
            rolesSet.add("ReceiveMFInven");
            rolesSet.add("ReplenishInven");
            rolesSet.add("ConfirmRestock");
            
            rolesSet.add("Shelf");
            rolesSet.add("AddShelf");
            rolesSet.add("ViewShelf");
            
            rolesSet.add("ShelfType");
            rolesSet.add("AddShelfType");
            rolesSet.add("ViewShelfType");
            
            rolesSet.add("AdHocOrdering");
            rolesSet.add("AdHocPdtnPlan");
            
            rolesSet.add("Supplier");
            rolesSet.add("GeneratePdtPO");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewDeliverySchedule");
            
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("RegionTransacRec");
            rolesSet.add("ViewRegionTransacRec");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
        }
        if (getRole3() != null && getRole3().equals("Store Manager")) {
            rolesSet.add("SCM");
            rolesSet.add("ViewDistributnF");
            rolesSet.add("ViewDistributnP");
            rolesSet.add("ViewUnpaidBills");
            
            rolesSet.add("Inventory");
            rolesSet.add("InvenRecord");
            rolesSet.add("AddInvenRec");
            rolesSet.add("ViewInvenRec");
            
            rolesSet.add("InvenRestock");
            rolesSet.add("ReceiveSInven");
            rolesSet.add("ViewRestockLoc");
            rolesSet.add("ReceiveMFInven");
            rolesSet.add("ReplenishInven");
            rolesSet.add("ConfirmRestock");
            
            rolesSet.add("Shelf");
            rolesSet.add("AddShelf");
            rolesSet.add("ViewShelf");
            
            rolesSet.add("ShelfType");
            rolesSet.add("AddShelfType");
            rolesSet.add("ViewShelfType");
            
            rolesSet.add("AdHocOrdering");
            rolesSet.add("AdHocPdtnPlan");
            
            rolesSet.add("Supplier");
            rolesSet.add("GeneratePdtPO");
            rolesSet.add("ViewPdtPO");
            rolesSet.add("ViewDeliverySchedule");
            
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("RegionTransacRec");
            rolesSet.add("ViewRegionTransacRec");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
        }
		
        if (getRole1().equals("Store Staff")) {
            rolesSet.add("Inventory");      
            rolesSet.add("InvenRecord");
            rolesSet.add("ViewInvenRec");
            
            rolesSet.add("InvenRestock");
            rolesSet.add("ViewRestockLoc");
            rolesSet.add("ReplenishInven");
            rolesSet.add("ConfirmRestock");
            
            rolesSet.add("Shelf");
            rolesSet.add("ViewShelf");
            
            rolesSet.add("Supplier");
            rolesSet.add("ViewDeliverySchedule");
            
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
        }
        if (getRole2() != null && getRole2().equals("Store Staff")) {
            rolesSet.add("Inventory");      
            rolesSet.add("InvenRecord");
            rolesSet.add("ViewInvenRec");
            
            rolesSet.add("InvenRestock");
            rolesSet.add("ViewRestockLoc");
            rolesSet.add("ReplenishInven");
            rolesSet.add("ConfirmRestock");
            
            rolesSet.add("Shelf");
            rolesSet.add("ViewShelf");
            
            rolesSet.add("Supplier");
            rolesSet.add("ViewDeliverySchedule");
            
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
        }
        if (getRole3() != null && getRole3().equals("Store Staff")) {
            rolesSet.add("Inventory");      
            rolesSet.add("InvenRecord");
            rolesSet.add("ViewInvenRec");
            
            rolesSet.add("InvenRestock");
            rolesSet.add("ViewRestockLoc");
            rolesSet.add("ReplenishInven");
            rolesSet.add("ConfirmRestock");
            
            rolesSet.add("Shelf");
            rolesSet.add("ViewShelf");
            
            rolesSet.add("Supplier");
            rolesSet.add("ViewDeliverySchedule");
            
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
        }
        
        if (getRole1().equals("Store Purchasing Manager")) {
            rolesSet.add("Inventory");
            rolesSet.add("Supplier");
            rolesSet.add("ViewProdPO");
            rolesSet.add("GeneratePO");
        }
        if (getRole2() != null && getRole2().equals("Store Purchasing Manager")) {
            rolesSet.add("Inventory");
            rolesSet.add("Supplier");
            rolesSet.add("ViewProdPO");
            rolesSet.add("GeneratePO");
        }
        if (getRole3() != null && getRole3().equals("Store Purchasing Manager")) {
            rolesSet.add("Inventory");
            rolesSet.add("Supplier");
            rolesSet.add("ViewProdPO");
            rolesSet.add("GeneratePO");
        }
		
	if (getRole1().equals("Customer Service")) {
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
            rolesSet.add("SendMktEmail");
            
        }
        if (getRole2() != null && getRole2().equals("Customer Service")) {
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
            rolesSet.add("SendMktEmail");
        }
        if (getRole3() != null && getRole3().equals("Customer Service")) {
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
            rolesSet.add("SendMktEmail");
        }
        
        if (getRole1().equals("Kitchen Staff")) {
            rolesSet.add("Kitchen");
        }
        if (getRole2() != null && getRole2().equals("Kitchen Staff")) {
            rolesSet.add("Kitchen");
        }
        if (getRole3() != null && getRole3().equals("Kitchen Staff")) {
            rolesSet.add("Kitchen");
        }
        
        if (getRole1().equals("Marketing Staff")) {
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
            rolesSet.add("SendMktEmail");
            
            rolesSet.add("Anal");
        }
        if (getRole2() != null && getRole2().equals("Marketing Staff")) {
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
            rolesSet.add("SendMktEmail");
            
            rolesSet.add("Anal");
            
        }
        if (getRole3() != null && getRole3().equals("Marketing Staff")) {
            rolesSet.add("OpCRM");
            rolesSet.add("ServiceRec");
            rolesSet.add("ViewServiceRec");
            rolesSet.add("AddServiceRec");
            
            rolesSet.add("PdtnOrders");
            rolesSet.add("ViewPdtnOrders");
            rolesSet.add("AddPdtnOrders");
            
            rolesSet.add("Campaigns");
            rolesSet.add("ViewCampaigns");
            rolesSet.add("AddCampaigns");
            
            rolesSet.add("Customers");
            rolesSet.add("ViewCustomers");
            rolesSet.add("SendMktEmail");
            
            rolesSet.add("Anal");
        }
        
        return rolesSet;
    }

    public List<String> getRolesSet() {
        return setAccess();
    }

    public void setRolesSet(List<String> rolesSet) {
        this.rolesSet = rolesSet;
    }
    
    public String getRole1() {
        role1 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role");
        return role1;
    }

    public void setRole1(String role1) {
        this.role1 = role1;
    }

    public String getRole2() {
        role2 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role2");
        return role2;
    }

    public void setRole2(String role2) {
        this.role2 = role2;
    }

    public String getRole3() {
        role3 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("role3");
        return role3;
    }

    public void setRole3(String role3) {
        this.role3 = role3;
    }
    
    public boolean isCheckSA() {
        checkSA = getRolesSet().contains("System Admin");
        return checkSA;
    }

    public void setCheckSA(boolean checkSA) {   
        this.checkSA = checkSA;
    }

    public boolean isCheckGHQ() {
        checkGHQ = getRolesSet().contains("Global HQ");
        return checkGHQ;
    }

    public void setCheckGHQ(boolean checkGHQ) { 
        this.checkGHQ = checkGHQ;
    }

    public boolean isCheckMRP() {
        checkMRP = getRolesSet().contains("MRP");
        return checkMRP;
    }

    public void setCheckMRP(boolean checkMRP) { 
        this.checkMRP = checkMRP;
    }

    public boolean isCheckSCM() {
        checkSCM = getRolesSet().contains("SCM");
        return checkSCM;
    }

    public void setCheckSCM(boolean checkSCM) {
        this.checkSCM = checkSCM;
    }

    public boolean isCheckRHQ() {
        return getRolesSet().contains("RHQ");
    }

    public boolean isCheckForecastFPdt() {
        return getRolesSet().contains("ForecastFPdt");
    }

    public boolean isCheckPdtionOrderProcessing() {
        return getRolesSet().contains("PdtionOrderProcessing");
    }

    public boolean isCheckForecatRPdt() {
        return getRolesSet().contains("ForecatRPdt");
    }

    public boolean isCheckPOProcessing() {
        return getRolesSet().contains("POProcessing");
    }

    public boolean isCheckViewPdtionRecords() {
        return getRolesSet().contains("ViewPdtionRecords");
    }

    public boolean isCheckViewPurchRecords() {
        return getRolesSet().contains("ViewPurchRecords");
    }

    public boolean isCheckGeneratePdtPO() {
        return getRolesSet().contains("GeneratePdtPO");
    }

    public boolean isCheckGenerateRawMatPO() {
        return getRolesSet().contains("GenerateRawMatPO");
    }

    public boolean isCheckGenerateDeliverySchedule() {
        return getRolesSet().contains("GenerateDeliverySchedule");
    }

    public boolean isCheckViewPdtPO() {
        return getRolesSet().contains("ViewPdtPO");
    }

    public boolean isCheckViewRawMatPO() {
        return getRolesSet().contains("ViewRawMatPO");
    }

    public boolean isCheckViewDistributnF() {
        return getRolesSet().contains("ViewDistributnF");
    }

    public boolean isCheckViewDistributnP() {
        return getRolesSet().contains("ViewDistributnP");
    }

    public boolean isCheckViewUnpaidBills() {
        return getRolesSet().contains("ViewUnpaidBills");
    }

    public boolean isCheckInvenRestock() {
        return getRolesSet().contains("InvenRestock");
    }

    public boolean isCheckReceiveSInven() {
        return getRolesSet().contains("ReceiveSInven");
    }

    public boolean isCheckViewRestockLoc() {
        return getRolesSet().contains("ViewRestockLoc");
    }

    public boolean isCheckShelf() {
        return getRolesSet().contains("Shelf");
    }

    public boolean isCheckAddShelf() {
        return getRolesSet().contains("AddShelf");
    }

    public boolean isCheckViewShelf() {
        return getRolesSet().contains("ViewShelf");
    }

    public boolean isCheckShelfType() {
        return getRolesSet().contains("ShelfType");
    }

    public boolean isCheckAddShelfType() {
        return getRolesSet().contains("AddShelfType");
    }

    public boolean isCheckViewShelfType() {
        return getRolesSet().contains("ViewShelfType");
    }

    public boolean isCheckAdHocOrdering() {
        return getRolesSet().contains("AdHocOrdering");
    }

    public boolean isCheckAdHocPdtnPlan() {
        return getRolesSet().contains("AdHocPdtnPlan");
    }

    public boolean isCheckInventory() {
        return getRolesSet().contains("Inventory");
    }

    public boolean isCheckInvenRecord() {
        return getRolesSet().contains("InvenRecord");
    }

    public boolean isCheckAddInvenRec() {
        return getRolesSet().contains("AddInvenRec");
    }

    public boolean isCheckViewInvenRec() {
        return getRolesSet().contains("ViewInvenRec");
    }

    public boolean isCheckReceiveMFInven() {
        return getRolesSet().contains("ReceiveMFInven");
    }

    public boolean isCheckReplenishInven() {
        return getRolesSet().contains("ReplenishInven");
    }

    public boolean isCheckConfirmRestock() {
        return getRolesSet().contains("ConfirmRestock");
    }

    public boolean isCheckSupplier() {
        return getRolesSet().contains("Supplier");
    }

    public boolean isCheckViewDeliverySchedule() {
        return getRolesSet().contains("ViewDeliverySchedule");
    }

    public boolean isCheckOPCRM() {
        return getRolesSet().contains("OpCRM");
    }

    public boolean isCheckServiceRec() {
        return getRolesSet().contains("ServiceRec");
    }

    public boolean isCheckViewServiceRec() {
        return getRolesSet().contains("ViewServiceRec");
    }

    public boolean isCheckAddServiceRec() {
        return getRolesSet().contains("AddServiceRec");
    }

    public boolean isCheckPdtnOrders() {
        return getRolesSet().contains("PdtnOrders");
    }

    public boolean isCheckViewPdtnOrders() {
        return getRolesSet().contains("ViewPdtnOrders");
    }

    public boolean isCheckAddPdtnOrders() {
        return getRolesSet().contains("AddPdtnOrders");
    }

    public boolean isCheckRegionTransacRec() {
        return getRolesSet().contains("RegionTransacRec");
    }

    public boolean isCheckViewRegionTransacRec() {
        return getRolesSet().contains("ViewRegionTransacRec");
    }

    public boolean isCheckCampaigns() {
        return getRolesSet().contains("Campaigns");
    }

    public boolean isCheckViewCampaigns() {
        return getRolesSet().contains("ViewCampaigns");
    }

    public boolean isCheckAddCampaigns() {
        return getRolesSet().contains("AddCampaigns");
    }

    public boolean isCheckCustomers() {
        return getRolesSet().contains("Customers");
    }

    public boolean isCheckViewCustomers() {
        return getRolesSet().contains("ViewCustomers");
    }

    public boolean isCheckSendMktEmail() {
        return getRolesSet().contains("SendMktEmail");
    }

    public boolean isCheckSupplierExt() {
        return getRolesSet().contains("SupplierExt");
    }

    public boolean isCheckAnal() {
        return getRolesSet().contains("Anal");
    }

    public boolean isCheckKitchen() {
        return getRolesSet().contains("Kitchen");
    }

    public boolean isCheckAddBill() {
        return getRolesSet().contains("AddBill");
    }

    public boolean isCheckSCMGeneratePdtPO() {
        return getRolesSet().contains("SCMGeneratePdtPO");
    }

    public boolean isCheckSCMViewPdtPO() {
        return getRolesSet().contains("SCMViewPdtPO");
    }

    public boolean isCheckSCMInvenRestock() {
        return getRolesSet().contains("SCMInvenRestock");
    }

    public boolean isCheckSCMReceiveSInven() {
        return getRolesSet().contains("SCMReceiveSInven");
    }

    public boolean isCheckSCMViewRestockLoc() {
        return getRolesSet().contains("SCMViewRestockLoc");
    }

    public boolean isCheckSCMShelf() {
        return getRolesSet().contains("SCMShelf");
    }

    public boolean isCheckSCMAddShelf() {
        return getRolesSet().contains("SCMAddShelf");
    }

    public boolean isCheckSCMViewShelf() {
        return getRolesSet().contains("SCMViewShelf");
    }

    public boolean isCheckSCMShelfType() {
        return getRolesSet().contains("SCMShelfType");
    }

    public boolean isCheckSCMAddShelfType() {
        return getRolesSet().contains("SCMAddShelfType");
    }

    public boolean isCheckSCMViewShelfType() {
        return getRolesSet().contains("SCMViewShelfType");
    }

    public boolean isCheckSCMAdHocOrdering() {
        return getRolesSet().contains("SCMAdHocOrdering");
    }

    public boolean isCheckSCMAdHocPdtnPlan() {
        return getRolesSet().contains("SCMAdHocPdtnPlan");
    }

    public boolean isCheckProductionOperation() {
        return getRolesSet().contains("ProductionOperation");
    }

    
}
