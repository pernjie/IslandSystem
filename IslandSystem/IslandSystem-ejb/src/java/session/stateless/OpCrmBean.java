/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.stateless;

import entity.Facility;
import entity.ProductionOrder;
import entity.RegionServicePrice;
import entity.Service;
import entity.ServiceRecord;
import entity.ServiceRecordItem;
import entity.TransactionItem;
import entity.TransactionRecord;
import entity.TransactionService;
import enumerator.SvcRecStatus;
import enumerator.SvcCat;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;
import entity.Campaign;
import entity.Country;
import entity.Customer;
import entity.CustomerCampaignMetric;
import entity.CustomerMetric;
import entity.Item;
import entity.Material;
import entity.Region;
import entity.RegionItemPrice;
import enumerator.BusinessArea;
import enumerator.CustomerCampaignCat;
import enumerator.Gender;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import util.SMTPAuthenticator;
import util.exception.DetailsConflictException;

/**
 *
 * @author nataliegoh
 */
@Stateless
@LocalBean
public class OpCrmBean {//implements OpCrmBeanLocal {

    public Integer confirmServiceRecordPayment(Long svcRecId, Long transRecId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        TransactionRecord tr;
        ServiceRecord sr;

        try {
            sr = em.find(ServiceRecord.class, svcRecId);
            if (sr == null) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }

            if (sr.getTransact() != null) {
                return 2;
            }
         else{
            try {
                tr = em.find(TransactionRecord.class, transRecId);
                if (tr == null) {
                    return 1;
                }
            } catch (Exception e2) {
                return 1;
            }
            sr.setTransact(tr);
            sr.setStatus(SvcRecStatus.QUEUED);
            em.merge(sr);
            return 3;
        }
    }

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
            Query query = em.createQuery("SELECT c FROM " + Campaign.class
                    .getName() + " c WHERE c.promoCode= :code");
            query.setParameter(
                    "code", campaign.getPromoCode());
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
        q.setParameter("region", r);
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
                        productionOrder
                                .setMat(em.find(Material.class, matId));
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

        query.setParameter(
                "inStore", store);
        query.setParameter(
                "inMat", mat);
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

        query.setParameter(
                "inStore", store);
        query.setParameter(
                "inMat", mat);
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

        query.setParameter(
                "inMat", mat);
        query.setParameter(
                "inSvcRec", svcRec);
        query.setParameter(
                "inSvc", svc);
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
            svcRec
                    .setStore(em.find(Facility.class, storeId));
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
                sri
                        .setMat(em.find(Material.class, matId));
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
            sri
                    .setMat(em.find(Material.class, Long.valueOf(64)));
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
            if (transactionItem.getReturnedQty()
                    != null) {
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

        query.setParameter(
                "svcRec", svcRec);
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

    public List<Country> getAllCountries() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Country.findAll");
        return query.getResultList();
    }
    
    public Boolean unsubscribe(Customer customer) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        customer.setUnsubscribed(true);
        try {
            em.merge(customer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean resetPassword(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
        q.setParameter("email", email);
        try {
            Customer customer = (Customer) q.getSingleResult();
            String name = customer.getName();
            String password = generateRandomPassword();
            customer.setPassword(encryptPassword(email, password));
            em.merge(customer);
            emailPassword(name, email, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateRandomPassword() {
        // Pick from some letters that won't be easily mistaken for each
        // other. So, for example, omit o O and 0, 1 l and L.
        String letters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789!@#$%^&*";
        Random RANDOM = new SecureRandom();

        String pw = "";
        for (int i = 0; i < 8; i++) {
            int index = (int) (RANDOM.nextDouble() * letters.length());
            pw += letters.substring(index, index + 1);
        }
        return pw;
    }

    public void emailPassword(String name, String email, String password) {
        String emailServerName = "mailauth.comp.nus.edu.sg";
        String emailFromAddress = "Island Furniture System Administrator <a0101309@u.nus.edu>";
        String mailer = "JavaMailer";
        String toEmailAddress = email;

        try {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "true");
            javax.mail.Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            Message msg = new MimeMessage(session);
            if (msg != null) {
                msg.setFrom(InternetAddress.parse(emailFromAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Welcome to Island Furniture!" + "\n\n");

                //Create and fill first part
                MimeBodyPart header = new MimeBodyPart();
                header.setText("Welcome to Island Furniture, " + name + ".\n\n");

                //Create and fill second part
                MimeBodyPart body = new MimeBodyPart();
                body.setText("Here's your newly reset password: " + password + "\n\n");

                //Create and fill third part
                MimeBodyPart linkText = new MimeBodyPart();
                linkText.setText("You can log in with this new password at our website. Thank you.");

                //Create the Multipart
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(header);
                mp.addBodyPart(body);
                mp.addBodyPart(linkText);

                //Set Message Content
                msg.setContent(mp);

                //String messageText = "Welcome to Island Furniture Family, " +name+ ".\n\n Here's the autogenerated password: " + password +"\n\n";
                //msg.setText(messageText);
                //msg.setDisposition(Part.INLINE);
                msg.setHeader("X-Mailer", mailer);
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);
                Transport.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e.getMessage());
        }
    }

    public void changePassword(String email, String currpassword, String newpassword) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        currpassword = encryptPassword(email, currpassword);
        Query q = em.createQuery("SELECT c FROM Customer c where c.email= :param");
        q.setParameter("param", email);
        Customer customer = (Customer) q.getSingleResult();
        if (customer.getPassword().equals(currpassword)) {
            newpassword = encryptPassword(email, newpassword);
            customer.setPassword(newpassword);
            em.merge(customer);
        } else {
            throw new DetailsConflictException("Wrong Current Password");
        }

    }

    public String encryptPassword(String email, String password) {
        String encrypted = null;
        if (password == null) {
            return null;
        } else {
            try {
                password = password.concat(email);
                System.err.println("encrypt Password: password = " + password);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes(), 0, password.length());
                encrypted = new BigInteger(1, md.digest()).toString(16);
                System.err.println("encrypt Password: encrypted = " + encrypted);
            } catch (NoSuchAlgorithmException ex) {
            }
        }
        return encrypted;

    }

    public Boolean verifyUser(String email, String password) {
        password = encryptPassword(email, password);
        try {
            if (password.equals("")) {
                return false;
            } else {
                EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
                EntityManager em = emf.createEntityManager();
                Query q;
                q = em.createQuery("SELECT c.password FROM Customer c WHERE c.email= :param");
                q.setParameter("param", email);
                String pwd = (String) q.getSingleResult();
                if (!(pwd.equals(password))) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (NoResultException ec) {
            System.err.println("ERROR");
            return false;
        }
    }

    public List<RegionItemPrice> getFurnitureDisplays(long regionId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("Region.findById");
        q.setParameter("id", regionId);

        Region region = (Region) q.getSingleResult();

        System.out.println("IN GET FURNITURE!");
        System.out.print("REGION " + region);
        Query query = em.createQuery("SELECT r FROM " + RegionItemPrice.class.getName() + " r WHERE r.region =:region AND r.item IN (SELECT m FROM " + Material.class.getName() + " m WHERE NOT m.id = 64)");
        query.setParameter("region", region);

        System.out.println("IN GET FURNITURE 2!");

        return query.getResultList();
    }

    //@Override
    public Material getItem(Long itemId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        System.out.println("itemid: !" + itemId);
        Query q = em.createNamedQuery("Material.findById");
        q.setParameter("id", itemId);

        Material result = (Material) q.getSingleResult();
        return result;
    }

    //@Override
    public Material getItembyString(String itemName) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("Material.findByName");
        q.setParameter("name", itemName);

        try {
            Material result = (Material) q.getSingleResult();
            System.out.println("IN GET ITEM!");
            System.out.println(result);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    //@Override
    public List<Region> getRegions() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("Region.findAll");

        System.out.println("IN GET REGION!");
        System.out.println(q.getResultList().size());
        return q.getResultList();
    }

    //@Override
    public Region getRegion(long regionId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("Region.findById");
        q.setParameter("id", regionId);

        return (Region) q.getSingleResult();
    }

    //@Override
    public RegionItemPrice getRegionPriceRecord(Item item, Region region) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("RegionItemPrice.findByRegionItem");
        q.setParameter("region", region);
        q.setParameter("item", item);

        return (RegionItemPrice) q.getSingleResult();
    }

    //@Override
    public List<Item> getItems(long regionId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        System.out.println("IN GET ITEMS!");

        Integer genCategory = 0;
        List<Item> resItems = new ArrayList<Item>();

        Query q = em.createNamedQuery("Region.findById");
        q.setParameter("id", regionId);

        Region resultRegion = (Region) q.getSingleResult();

        Query query = em.createQuery("SELECT r FROM " + RegionItemPrice.class.getName() + " r WHERE r.region =:region AND r.item IN (SELECT m FROM " + Material.class.getName() + " m WHERE NOT m.genCategory = :genCategory)");
        query.setParameter("region", resultRegion);
        query.setParameter("genCategory", genCategory);
        List<RegionItemPrice> mats = query.getResultList();

        for (RegionItemPrice m : mats) {
            System.out.println(m.getId());
            resItems.add(m.getItem());
        }

        return resItems;
    }

    //@Override
//    public void unsubscribe(long custId) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query q = em.createQuery("SELECT c FROM Customer c where c.id= :param");
//        q.setParameter("param", custId);
//        Customer customer = (Customer) q.getSingleResult();
//        customer.setUnsubscribed(true);
//        em.persist(customer);
//    }

    //@Override
    public Long addNewCustomer(Customer customer) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        String email = customer.getEmail();
        if (!emailAlreadyExist(email)) {
            try {
                customer.setPassword(encryptPassword(customer.getEmail(), customer.getPassword()));
                em.persist(customer);
                createCustomerMetrics(customer);
                checkOngoingCampaign(customer);
                return customer.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Email exists: " + customer.getEmail());
        }
    }

    private void createCustomerMetrics(Customer customer) {
        CustomerMetric cmkit = new CustomerMetric();
        CustomerMetric cmmat = new CustomerMetric();
        CustomerMetric cmprod = new CustomerMetric();
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        cmmat.setCustomer(customer);
        cmkit.setCustomer(customer);
        cmprod.setCustomer(customer);

        cmmat.setBusinessArea(BusinessArea.FURNITURE);
        cmprod.setBusinessArea(BusinessArea.PRODUCT);
        cmkit.setBusinessArea(BusinessArea.KITCHEN);

        cmmat.setCurrentRfm(0);
        cmkit.setCurrentRfm(0);
        cmprod.setCurrentRfm(0);

        cmprod.setHighestRfm(0);
        cmkit.setHighestRfm(0);
        cmmat.setHighestRfm(0);

        cmmat.setInactive(false);
        cmkit.setInactive(false);
        cmprod.setInactive(false);

        em.persist(cmkit);
        em.persist(cmprod);
        em.persist(cmmat);
    }

    private void checkOngoingCampaign(Customer customer) {
        System.out.println("checkOngoingCampaign initialized");
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Date currDate = Calendar.getInstance().getTime();
        Integer custAge = translateDobToAge(customer.getDob());
        System.out.println("custAge: " + custAge);

        Gender gender = customer.getGender();
        System.out.println("gender: " + gender);
        Region region = customer.getRegion();
        System.out.println("region: " + region.getId());

        Query q = em.createQuery("SELECT c FROM Campaign c WHERE c.region = :region AND c.targetNew = :true "
                + "AND ( :custAge BETWEEN c.lowerAge AND c.upperAge ) AND ( :currDate BETWEEN c.startDate AND c.endDate)");
        q.setParameter("region", region);
        q.setParameter("custAge", custAge);
        q.setParameter("currDate", currDate);
        q.setParameter("true", true);
        if (!q.getResultList().isEmpty()) {
            List<Campaign> campaigns = (List<Campaign>) q.getResultList();
            for (Campaign camp : campaigns) {
                if (camp.getTargetGender().equals(Gender.ALL) || camp.getTargetGender().equals(customer.getGender())) {
                    CustomerCampaignMetric ccm = new CustomerCampaignMetric();
                    ccm.setCampaign(camp);
                    ccm.setCustomer(customer);
                    ccm.setCustomerCampaignCat(CustomerCampaignCat.NEW);
                    ccm.setNumHits(0);
                    ccm.setNumPromoCodeUsed(0);
                    em.persist(ccm);
                }
            }
        }
    }

    public Integer translateDobToAge(Date date) {
        Calendar currDate = Calendar.getInstance();
        Integer currYear = currDate.get(Calendar.YEAR);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer bornYear = cal.get(Calendar.YEAR);
        return currYear - bornYear;
    }


    private Boolean emailAlreadyExist(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email");
        query.setParameter("email", email);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
