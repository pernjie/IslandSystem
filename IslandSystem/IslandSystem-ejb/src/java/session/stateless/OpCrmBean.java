/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.stateless;

import entity.Campaign;
import entity.Country;
import entity.Customer;
import entity.CustomerCampaignMetric;
import entity.CustomerMetric;
import entity.Facility;
import entity.Material;
import entity.ProductionOrder;
import entity.Region;
import entity.RegionServicePrice;
import entity.Service;
import entity.ServiceRecord;
import entity.ServiceRecordItem;
import entity.TransactionItem;
import entity.TransactionRecord;
import entity.TransactionService;
import enumerator.CustomerCampaignCat;
import enumerator.Gender;
import enumerator.SvcRecStatus;
import enumerator.SvcCat;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

/**
 *
 * @author nataliegoh
 */
@Stateless
@LocalBean
public class OpCrmBean {//implements OpCrmBeanLocal {

    //@Override
    public void removeCampaign(Campaign campaign) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Campaign removeCampaign = em.merge(campaign);
            em.remove(removeCampaign);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Campaign " + campaign.getId() + " due to Foreign Key constraints");
        }
    }

    //@Override
    public Long addNewCampaign(Campaign campaign) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Campaign", campaign.getName())) {
            if (!PromoCodeAlreadyExist(campaign)) {
//                if (campaign.getTargetActive()==true && campaign.getRfmThreshold() != null) {
//                    if (campaign.getTargetInactive()==true && campaign.getHighestRfmThreshold() != null) {
                System.out.println("Enter if loop");
                em.persist(campaign);
                System.out.println("em.persist");
                em.flush();
                addCampaignCustomers(campaign);
                return campaign.getId();
//                    } else {
//                        throw new DetailsConflictException("Cannot target inactive customers without filling up the threshold for the highest RFM score");
//                    }
//                } else {
//                    throw new DetailsConflictException("Cannot target active customers without filling up the threshold for the current RFM score");
//                }
            } else {
                throw new DetailsConflictException("Cannot have the same Promo Code: " + campaign.getPromoCode());
            }
        } else {
            throw new DetailsConflictException("Name conflict: " + campaign.getName());
        }
    }

    //@Override
    public Boolean addCampaignCustomers(Campaign campaign) {
        System.out.println("addCampaignCustomers initialized");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Customer c WHERE c.region = :region");
            q.setParameter("region", campaign.getRegion());
            List<Customer> customers = q.getResultList();
//            for (Customer c : customers) {
//                System.out.println(c.getName());
//            }

            q = em.createQuery("SELECT cm FROM CustomerMetric cm WHERE cm.businessArea =:bizArea AND cm.customer.region = :region");
            q.setParameter("bizArea", campaign.getBusinessArea());
            q.setParameter("region", campaign.getRegion());
            List<CustomerMetric> customerMetrics = q.getResultList();
//            for (CustomerMetric c : customerMetrics) {
//                System.out.println(c.getCustomer().getName());
//            }
            Calendar currDate = Calendar.getInstance();
            Integer currYear = currDate.get(Calendar.YEAR);
            Integer earliestBirthYear = currYear - campaign.getUpperAge();
            Integer latestBirthYear = currYear - campaign.getLowerAge();
            Gender targetGender = campaign.getTargetGender();
            Boolean targetsActiveCust = campaign.getTargetActive();
            Boolean targetsInactiveCust = campaign.getTargetInactive();
            Boolean noCustomer = true;
            System.out.println("Target Inactive: " + targetsInactiveCust);

            for (Customer c : customers) {
                CustomerMetric cm = getCustomerMetric(customerMetrics, c);
                //System.out.println("CUST METRIC ID: " + cm.getId());
                Date temp = c.getDob();
                Calendar dob = Calendar.getInstance();
                dob.setTime(temp);
                Integer custBornIn = dob.get(Calendar.YEAR);
                System.out.println("Customer born in: " + custBornIn);
                System.out.println("Customer Inactive: " + cm.getInactive());
                System.out.println("LOOKING AT: CUST ID: " + cm.getCustomer().getId() + " BUINESS AREA: " + cm.getBusinessArea() + " CM Highest: " + cm.getHighestRfm() + " INACTIVE: " + cm.getInactive() + " DOB: " + cm.getCustomer().getDob());

                if ((custBornIn <= latestBirthYear && custBornIn >= earliestBirthYear) //and customer born within age range

                        && ((targetsInactiveCust && cm.getInactive() && cm.getHighestRfm() >= campaign.getHighestRfmThreshold())
                        || (targetsActiveCust && !cm.getInactive() && cm.getCurrentRfm() >= campaign.getRfmThreshold())) //only if customer is active and target act or cust is inact and target inact

                        && ((!targetGender.equals(Gender.ALL) && targetGender.equals(c.getGender())) || targetGender.equals(Gender.ALL))) { //and if cust g is target g or target g is ALL
                    System.out.println("CAMPAIGN CUSTOMER HIT, CUST METRIC ID: " + cm.getId() + " CUST ID: " + c.getId());
                    CustomerCampaignMetric ccm = new CustomerCampaignMetric();
                    ccm.setCustomer(c);
                    ccm.setCampaign(campaign);
                    ccm.setNumHits(0);
                    ccm.setNumPromoCodeUsed(0);
                    if (!cm.getInactive()) {
                        ccm.setCustomerCampaignCat(CustomerCampaignCat.ACTIVE);
                    } else if (cm.getInactive()) {
                        ccm.setCustomerCampaignCat(CustomerCampaignCat.INACTIVE);
                    }
                    em.persist(ccm);
                    noCustomer = false;
                }
            }
            if (noCustomer == true) {
                campaign.setTargetNew(true);
//                em.merge(campaign);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private CustomerMetric getCustomerMetric(List<CustomerMetric> customerMetrics, Customer cust) {
        for (CustomerMetric cm : customerMetrics) {
            if (cm.getCustomer().equals(cust)) {
                return cm;
            }
        }
        return null;
    }

    private Boolean PromoCodeAlreadyExist(Campaign campaign) {
        if (!campaign.getPromoCode().equals("") && !campaign.getPromoCode().isEmpty()) {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();
            Query query = em.createQuery("SELECT c FROM " + Campaign.class.getName() + " c WHERE c.promoCode= :code");
            query.setParameter("code", campaign.getPromoCode());
            List resultList = query.getResultList();
            if (resultList.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private Boolean NameAlreadyExist(String entity, String name) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT c FROM " + entity + " c WHERE c.name = :name");
        query.setParameter("name", name);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    //@Override
    public List<Campaign> getAllCampaigns() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Campaign.findAll");
        return query.getResultList();
    }

    public List<Campaign> getRegionCampaigns(Region r) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Campaign.findByRegion");
        q.setParameter("region",r);
        return q.getResultList();
    }

    //@Override
    public Facility getUserFacility(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT s.fac FROM Staff s WHERE s.email= :email");
        query.setParameter("email", email);
        return (Facility) query.getSingleResult();
    }

    //@Override
    public Region getUserRegion(Facility fac) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f.region FROM Facility f WHERE f = :fac");
        query.setParameter("fac", fac);
        return (Region) query.getSingleResult();
    }

    //@Override
    public void updateProductionOrder(ProductionOrder productionOrder)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long matId = productionOrder.getMat().getId();
        Long storeId = productionOrder.getStore().getId();
        Long id = productionOrder.getId();

        if (!EntityIdDNE("Material", matId)) {
            if (!EntityIdDNE("Facility", storeId)) {
                if (!MatStoreAlreadyExist(matId, storeId, id)) {
                    try {
                        em.merge(productionOrder);

                    } catch (Exception e) {
                        e.printStackTrace();

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Material Store combination exists: " + matId + " " + storeId);
                }
            } else {
                throw new EntityDneException("Store DNE: " + storeId);
            }
        } else {
            throw new EntityDneException("Material DNE: " + matId);
        }
    }

    //@Override
    public List<ProductionOrder> getAllProductionOrders() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("ProductionOrder.findAll");
        return query.getResultList();
    }

    //@Override
    public Long addNewProductionOrder(Integer period, Integer year, Integer quantity, Long matId, Long storeId)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!EntityIdDNE("Material", matId)) {
            if (!EntityIdDNE("Facility", storeId)) {
                if (!MatStoreAlreadyExist(matId, storeId)) {
                    try {
                        ProductionOrder productionOrder = new ProductionOrder();
                        productionOrder.setPeriod(period);
                        productionOrder.setYear(year);
                        productionOrder.setQuantity(quantity);
                        productionOrder.setMat(em.find(Material.class, matId));
                        productionOrder.setStore(em.find(Facility.class, storeId));

                        em.persist(productionOrder);
                        return productionOrder.getId();

                    } catch (Exception e) {
                        e.printStackTrace();

                        return null;

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Material Store combination exists: " + matId + " " + storeId);
                }
            } else {
                throw new EntityDneException("Store DNE: " + storeId);
            }
        } else {
            throw new EntityDneException("Material DNE: " + matId);
        }
    }

    //@Override
    public void removeProductionOrder(ProductionOrder productionOrder) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            ProductionOrder removeProductionOrder = em.merge(productionOrder);
            em.remove(removeProductionOrder);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Production Order " + productionOrder.getId() + " due to Foreign Key constraints");
        }
    }

    private Boolean EntityIdDNE(String entityClass, Long entityId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT e FROM " + entityClass + " e WHERE e.id= " + entityId);
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean MatStoreAlreadyExist(Long matId, Long storeId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility store = em.find(Facility.class, storeId);
        Material mat = em.find(Material.class, matId);
        Query query = em.createQuery("SELECT s FROM DistributionMFtoStore s WHERE s.store= :inStore AND s.mat= :inMat");
        query.setParameter("inStore", store);
        query.setParameter("inMat", mat);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean MatStoreAlreadyExist(Long matId, Long storeId, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility store = em.find(Facility.class, storeId);
        Material mat = em.find(Material.class, matId);
        Query query = em.createQuery("SELECT s FROM DistributionMFtoStore s WHERE s.store= :inStore AND s.mat= :inMat AND NOT s.id = " + id);
        query.setParameter("inStore", store);
        query.setParameter("inMat", mat);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean MatSvcAlreadyExist(Long matId, Long svcId, Long svcRecId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Material mat = em.find(Material.class, matId);
        ServiceRecord svcRec = em.find(ServiceRecord.class, svcRecId);
        Service svc = em.find(Service.class, svcId);
        Query query = em.createQuery("SELECT s FROM ServiceRecordItem s WHERE s.mat= :inMat AND s.svcRec = :inSvcRec AND s.svc =:inSvc");
        query.setParameter("inMat", mat);
        query.setParameter("inSvcRec", svcRec);
        query.setParameter("inSvc", svc);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    //@Override
    public Double getTotalMaterialPrice(ServiceRecord svcRec, Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT sri FROM ServiceRecordItem sri WHERE sri.svcRec = :svcRec");
            query.setParameter("svcRec", svcRec);
            Double totalPrice = 0.00;
            if (!query.getResultList().isEmpty()) {
                for (ServiceRecordItem sri : (List<ServiceRecordItem>) query.getResultList()) {
                    if (sri.getMat() != null) {
                        query = em.createQuery("SELECT rip.price FROM RegionItemPrice rip WHERE rip.region = :region AND rip.item =:mat");
                        query.setParameter("mat", sri.getMat());
                        query.setParameter("region", region);
                        totalPrice += (Double) query.getSingleResult() * sri.getQuantity().doubleValue();
                    }
                }
            }
            return totalPrice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    //@Override
    public Service getDeliveryForServiceRecord(Region region, Double totalMatPrice) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT rsp FROM RegionServicePrice rsp WHERE rsp.region = :region AND rsp.svc.category = :delivery");
            query.setParameter("delivery", SvcCat.DELIVERY);
            query.setParameter("region", region);
            Double lowestDLTP = 0.00;
            Service selectedDelivery = null;
            for (RegionServicePrice rsp : (List<RegionServicePrice>) query.getResultList()) {
                if (rsp.getSvc().getDeliveryLowerThresholdPrice() != null) {
                    Double comparisonDLTP = rsp.getSvc().getDeliveryLowerThresholdPrice();
                    System.out.println("comparisonDLTP name is" + rsp.getSvc().getName());
                    System.out.println("comparisonDLTP is" + comparisonDLTP);
                    System.out.println("loweestDLTP is" + lowestDLTP);
                    if (totalMatPrice >= comparisonDLTP && comparisonDLTP >= lowestDLTP) {
                        lowestDLTP = comparisonDLTP;
                        selectedDelivery = rsp.getSvc();
                        System.out.println(selectedDelivery.getName());
                    }
                }
            }
            return selectedDelivery;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    //@Override
    public ServiceRecord addNewServiceRecord(String custName, String address, Date svcDate, Long storeId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            ServiceRecord svcRec = new ServiceRecord();
            svcRec.setCustName(custName);
            svcRec.setStatus(SvcRecStatus.UNPAID);
            svcRec.setStore(em.find(Facility.class, storeId));
            svcRec.setOrderTime(Calendar.getInstance().getTime());
            svcRec.setSvcDate(svcDate);
            svcRec.setAddress(address);
            em.persist(svcRec);
            return svcRec;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    //@Override
    public Long addNewServiceRecordItem(Long svcId, Long matId, Long svcRecId, Integer svcRecItemQty) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if (!MatSvcAlreadyExist(matId, svcId, svcRecId)) {
            try {
                System.out.println("addNewServiceRecordItem try");
                ServiceRecordItem sri = new ServiceRecordItem();
                sri.setMat(em.find(Material.class, matId));
                sri.setSvc(em.find(Service.class, svcId));
                sri.setSvcRec(em.find(ServiceRecord.class, svcRecId));
                sri.setQuantity(svcRecItemQty);
                em.persist(sri);
                return sri.getId();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException("Material Service combination exists: " + matId + " " + svcId);
        }
    }

    //@Override
    public void addOverallDelivery(Service delivery, ServiceRecord selectedServiceRecord) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            ServiceRecordItem sri = new ServiceRecordItem();
            sri.setSvc(delivery);
            sri.setSvcRec(selectedServiceRecord);
            sri.setQuantity(1);
            sri.setMat(em.find(Material.class, Long.valueOf(64)));
            em.persist(sri);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }
//call this method when customer makes payment for service. This attaches the transaction id to the service and changes the status to QUEUED.

    //@Override
    public Boolean payServiceRecord(Long transactRecId, Long svcRecId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            ServiceRecord svcRec = em.find(ServiceRecord.class, svcRecId);
            svcRec.setTransact(em.find(TransactionRecord.class, transactRecId));
            svcRec.setStatus(SvcRecStatus.QUEUED);
            em.merge(svcRec);
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;

        } finally {
            em.close();
        }
    }

    //@Override
    public Boolean fulfillServiceRecord(ServiceRecord svcRec) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            svcRec.setStatus(SvcRecStatus.COMPLETED);
            em.merge(svcRec);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    //@Override
    public void removeServiceRecord(ServiceRecord serviceRecord) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            ServiceRecord removeServiceRecord = em.merge(serviceRecord);
            em.remove(removeServiceRecord);
        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Service Record" + serviceRecord.getId() + " due to Foreign Key constraints");
        }
    }

    //@Override
    public void removeServiceRecordItem(ServiceRecordItem serviceRecordItem) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            ServiceRecordItem removeServiceRecordItem = em.merge(serviceRecordItem);
            em.remove(removeServiceRecordItem);
        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Service Record Item" + serviceRecordItem.getId() + " due to Foreign Key constraints");
        }
    }

    //@Override
    public void updateServiceRecord(ServiceRecord serviceRecord)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(serviceRecord);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    //@Override
    public void updateServiceRecordItem(ServiceRecordItem serviceRecordItem)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(serviceRecordItem);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    //@Override
    public void updateTransactionRecord(TransactionRecord tr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(tr);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    //@Override
    public void updateTransactionItem(TransactionItem transactionItem)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            TransactionItem oldTransactionItem = em.find(TransactionItem.class, transactionItem.getId());
            if (transactionItem.getReturnedQty() != null) {
                if (oldTransactionItem.getReturnedQty() != null) {
                    Integer oldReturnedQty = oldTransactionItem.getReturnedQty();
                    Integer newReturnedQty = transactionItem.getReturnedQty();
                    Query query = em.createQuery("SELECT ti.price FROM TransactionItem ti WHERE ti = :ti");
                    query.setParameter("ti", transactionItem);
                    Double returnedAmount = getTwoDigitPrice((Double) query.getSingleResult() * (newReturnedQty - oldReturnedQty) * countryTaxRate(transactionItem.getTransact().getFac().getCountry()));
                    System.out.println("returnAmount is: " + returnedAmount);
                    updateTransactionRecordWithReturns(returnedAmount, transactionItem.getTransact());
                } else if (oldTransactionItem.getReturnedQty() == null) {
                    Integer newReturnedQty = transactionItem.getReturnedQty();
                    Query query = em.createQuery("SELECT ti.price FROM TransactionItem ti WHERE ti = :ti");
                    query.setParameter("ti", transactionItem);
                    Double returnedAmount = getTwoDigitPrice((Double) query.getSingleResult() * (newReturnedQty) * countryTaxRate(transactionItem.getTransact().getFac().getCountry()));
                    System.out.println("returnAmount is: " + returnedAmount);
                    updateTransactionRecordWithReturns(returnedAmount, transactionItem.getTransact());
                }
                em.merge(transactionItem);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    //@Override
    public Double countryTaxRate(Country country) {
        return (100 - country.getTaxPercent()) / 100;
    }

    //@Override
    public Double getTwoDigitPrice(Double a) {
        return Math.round(a * 100.0) / 100.0;
    }

    //@Override
    public void updateTransactionRecordWithReturns(Double returnedAmount, TransactionRecord tr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
            if (tr.getReturnAmount() != null) {
                Double oldReturnedAmount = tr.getReturnAmount();
                tr.setReturnAmount(returnedAmount + oldReturnedAmount);
            } else {
                tr.setReturnAmount(returnedAmount);
            }
            tr.setNetTotalAmount(tr.getGrossTotalAmount() - tr.getTaxAmount() - tr.getReturnAmount());
            em.merge(tr);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    //@Override
    public TransactionRecord getSelectedTransactionRecord(Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        return em.find(TransactionRecord.class, id);
    }

    //@Override
    public List<TransactionItem> getTransactionItems(TransactionRecord tr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT ti FROM TransactionItem ti WHERE ti.transact = :tr");
        query.setParameter("tr", tr);
        return query.getResultList();
    }

    //@Override
    public List<TransactionService> getTransactionServices(TransactionRecord tr) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT ts FROM TransactionService ts WHERE ts.transact = :tr");
        query.setParameter("tr", tr);
        return query.getResultList();
    }

    //@Override
    public List<TransactionRecord> getRegionTransactionRecords(Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT tr FROM TransactionRecord tr WHERE tr.fac.region = :region");
        query.setParameter("region", region);
        return query.getResultList();
    }

    //@Override
    public List<ServiceRecordItem> getServiceRecordItems(Long svcRecId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        ServiceRecord svcRec = em.find(ServiceRecord.class, svcRecId);
        Query query = em.createQuery("SELECT sri FROM ServiceRecordItem sri WHERE sri.svcRec= :svcRec");
        query.setParameter("svcRec", svcRec);
        return query.getResultList();
    }

    //@Override
    public List<ServiceRecord> getServiceRecords(Facility store) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT sr FROM ServiceRecord sr WHERE sr.store = :store");
        query.setParameter("store", store);
        return query.getResultList();
    }

    //@Override
    public List<Material> getMaterials(Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT rip.item FROM RegionItemPrice rip WHERE rip.region= :region AND rip.item.ItemType = :type");
        query.setParameter("region", region);
        query.setParameter("type", "Material");
        return query.getResultList();
    }

    //@Override
    public List<Service> getNonDeliveryServices(Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT rsp.svc FROM RegionServicePrice rsp WHERE rsp.region = :region AND NOT rsp.svc.category = :delivery");
        query.setParameter("region", region);
        query.setParameter("delivery", SvcCat.DELIVERY);
        return query.getResultList();
    }

    //@Override
    public List<ServiceRecord> getAllServiceRecords() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("ServiceRecord.findAll");
        return query.getResultList();
    }

    //@Override
    public List<ProductionOrder> getProductionOrders(Facility store) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM ProductionOrder p WHERE p.store= :store");
        query.setParameter("store", store);

        List<ProductionOrder> po = query.getResultList();
        return po;
    }

    //@Override
    public List<Customer> getAllCustomers() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Customer.findAll");
        return query.getResultList();
    }

    //@Override
    public List<TransactionRecord> getAllTransactionRecords() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("TransactionRecord.findAll");
        return query.getResultList();
    }

    //@Override
    public List<Facility> getAllMFs() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllMfs");
        return query.getResultList();
    }

    //@Override
    public List<Material> getAllFurniture() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findAllFurniture");
        return query.getResultList();
    }

    //@Override
    public List<Material> getAllRawMat() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findAllRawMat");
        return query.getResultList();
    }

    //@Override
    public List<Facility> getAllStores() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllStores");
        return query.getResultList();
    }

    //@Override
    public List<Facility> getAllFacilities() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAll");
        return query.getResultList();
    }

    //@Override
    public Customer getCustomerDetails(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT c FROM Customer c where c.email= :param");
        q.setParameter("param", email);
        Customer customer = (Customer) q.getSingleResult();
        return customer;
    }

    //@Override
    public void unsubscribe(long custId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT c FROM Customer c where c.id= :param");
        q.setParameter("param", custId);
        Customer customer = (Customer) q.getSingleResult();
        customer.setUnsubscribed(true);
        em.persist(customer);
    }

    //@Override
//    public Long addNewCustomer(Customer customer) throws DetailsConflictException {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        String email = customer.getEmail();
//        if (!emailAlreadyExist(email)) {
//            try {
//                customer.setPassword(encryptPassword(customer.getEmail(), customer.getPassword()));
//                em.persist(customer);
//
//                return customer.getId();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//
//                return null;
//
//            } finally {
//                em.close();
//
//            }
//        } else {
//            throw new DetailsConflictException("Email exists: " + customer.getEmail());
//        }
//    }

//    private Boolean emailAlreadyExist(String email) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
//        query.setParameter("email", email);
//        List resultList = query.getResultList();
//        if (resultList.isEmpty()) {
//            return false;
//        } else {
//            return true;
//        }
//    }

//    public String encryptPassword(String email, String password) {
//        String encrypted = null;
//        if (password == null) {
//            return null;
//        } else {
//            try {
//                password = password.concat(email);
//                System.err.println("encrypt Password: password = " + password);
//                MessageDigest md = MessageDigest.getInstance("MD5");
//                md.update(password.getBytes(), 0, password.length());
//                encrypted = new BigInteger(1, md.digest()).toString(16);
//                System.err.println("encrypt Password: encrypted = " + encrypted);
//            } catch (NoSuchAlgorithmException ex) {
//            }
//        }
//        return encrypted;
//
//    }

    //@Override
    public List<Country> getAllCountries() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Country.findAll");
        return query.getResultList();
    }
}
