package session.stateless;

import entity.Component;
import entity.Country;
import entity.DistributionMFtoStore;
import entity.DistributionMFtoStoreProd;
import entity.Facility;
import entity.Ingredient;
import entity.Item;
import entity.RegionItemPrice;
import entity.Material;
import entity.MenuItem;
import entity.Product;
import entity.ProductionRecord;
import entity.PurchasePlanningRecord;
import entity.Region;
import entity.Service;
import entity.RegionServicePrice;
import entity.SetItem;
import entity.Staff;
import entity.Supplier;
import entity.SuppliesIngrToFac;
import entity.SuppliesMatToFac;
import entity.SuppliesProdToFac;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import util.exception.DetailsConflictException;
import util.exception.EntityDneException;
import util.exception.ReferenceConstraintException;

@Stateless
public class GlobalHqBean {

    private Long getId(Object object) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT MAX(o.id) FROM " + object.getClass().getName() + " AS o");
        Long id = (Long) (q.getSingleResult());
        if (id != null) {
            return ++id;
        } else {
            return 0L;
        }

    }

    private Long getItemId() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT MAX(f.id) FROM Item AS f");
        Long id = (Long) (q.getSingleResult());
        if (id != null) {
            return ++id;
        } else {
            return 0L;
        }
    }

    public List<Region> getAllRegions() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Region.findAll");
        return query.getResultList();
    }

    public void updateStaff(Staff staff) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.merge(staff);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();

        }
    }

    public List<Staff> getAllStaffs() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Staff.findAll");
        return query.getResultList();
    }

    public void removeStaff(Staff staff) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Staff removeStaff = em.merge(staff);
            em.remove(removeStaff);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Staff " + staff.getId() + " | " + staff.getName() + " due to Foreign Key constraints");
        }
    }

    public Staff getStaff(Long staffId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        return em.find(Staff.class, staffId);
    }

    public Staff getStaffByEmail(String email) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("Staff.findByEmail");
        q.setParameter("email", email);
        return (Staff) q.getSingleResult();
    }

    public Region getRegion(Long regionId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query q = em.createNamedQuery("Region.findById");
        q.setParameter("id", regionId);
        return (Region) q.getSingleResult();
    }

    public void updateFacility(Facility facility) throws
            DetailsConflictException {

        String type = facility.getType();
        Region region = facility.getRegion();
        Long id = facility.getId();
        if (!NameAlreadyExist("Facility", facility.getName(), id)) {
            if (!LocationAlreadyExist("Facility", facility.getPostalCode(), facility.getCity(), facility.getCountry(), id)) {

                if (!type.equals("Regional HQ") && (RegionTypeAlreadyExist(region, "Regional HQ", id)
                        || RegionTypeAlreadyExist(region, "Global HQ", id))) {
                    if (!type.equals("Online")) {
                        mergeFacility(facility);
                    } else if (type.equals("Online") && !RegionTypeAlreadyExist(region, "Online", id)) {
                        mergeFacility(facility);
                    } else {
                        throw new DetailsConflictException("Online Store already exists for region: " + region.getName());
                    }
                } //check only one regional HQ
                else if (type.equals("Regional HQ") && !RegionTypeAlreadyExist(region, "Regional HQ", id) && !RegionTypeAlreadyExist(region, "Global HQ", id)) {
                    mergeFacility(facility);
                } //only one online store per region
                //must have a regional HQ in the region before creating any other types of stores.
                else {
                    if (type.equals("Regional HQ")) {
                        throw new DetailsConflictException("HQ already exists for region: " + region.getName());
                    } else {
                        throw new DetailsConflictException("Create HQ first for region: " + region.getName());
                    }

                }
            } else {

                throw new DetailsConflictException("Location conflict: " + facility.getPostalCode() + " | " + facility.getCity() + " | " + facility.getCountry().getName());

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + facility.getName());

        }

    }

    public void mergeFacility(Facility facility) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {

            em.merge(facility);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();

        }
    }

    public List<Facility> getAllFacilities() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAll");
        return query.getResultList();
    }

    public List<Country> getAllCountries() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Country.findAll");
        return query.getResultList();
    }

    public Facility getFacility(long fid) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findById");
        query.setParameter("facilityId", fid);

        return (Facility) query.getSingleResult();
    }

    public Facility getFacility(String fname) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findByName");
        query.setParameter("name", fname);

        return (Facility) query.getSingleResult();
    }

    public Long addNewFacility(Facility facility) throws DetailsConflictException {

        String type = facility.getType();
        Region region = facility.getRegion();
        if (!LocationAlreadyExist("Facility", facility.getPostalCode(), facility.getCity(), facility.getCountry())) {
            if (!NameAlreadyExist("Facility", facility.getName())) {
                if (!type.equals("Regional HQ") && (RegionTypeAlreadyExist(region, "Regional HQ")
                        || RegionTypeAlreadyExist(region, "Global HQ"))) {
                    if (!type.equals("Online")) {
                        return persistFacility(facility);
                    } else if (type.equals("Online") && !RegionTypeAlreadyExist(region, "Online")) {
                        return persistFacility(facility);
                    } else {
                        throw new DetailsConflictException("Online Store already exists for region: " + region.getName());
                    }
                } //check only one regional HQ
                else if (type.equals("Regional HQ") && !RegionTypeAlreadyExist(region, "Regional HQ") && !RegionTypeAlreadyExist(region, "Global HQ")) {
                    return persistFacility(facility);
                } //only one online store per region
                //must have a regional HQ in the region before creating any other types of stores.
                else {
                    if (type.equals("Regional HQ")) {
                        throw new DetailsConflictException("HQ already exists for region: " + region.getName());
                    } else {
                        throw new DetailsConflictException("Create HQ first for region: " + region.getName());
                    }

                }
            } else {
                throw new DetailsConflictException("Name conflict: " + facility.getName());
            }
        } else {
            throw new DetailsConflictException("Location conflict: " + facility.getPostalCode() + " | " + facility.getCity() + " | " + facility.getCountry().getName());
        }
    }

    private Long persistFacility(Facility facility) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        try {
           // facility.setId(getId(facility));
            em.persist(facility);
            em.flush();
            return facility.getId();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public void removeFacility(Facility facility) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Facility removeFacility = em.merge(facility);
            em.remove(removeFacility);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Facility " + facility.getId() + " | " + facility.getName() + " due to Foreign Key constraints");
        }
    }

    public Facility getFacility(Long facId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        return em.find(Facility.class, facId);
    }

    public void updateMaterial(Material material) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Material", material.getName(), material.getId())) {
            try {
                em.merge(material);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + material.getName());
        }

    }

    public List<Material> getAllMaterials() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findAll");
        return query.getResultList();
    }

    public List<Material> getMfMaterials(String mfName) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM DistributionMFtoStore d WHERE d.mf = :fac");
        query.setParameter("fac", getFacility(mfName));
        return query.getResultList();
    }

    public Long addNewMaterial(Material material) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Material", material.getName())) {
            try {
                material.setId(getItemId());

                em.persist(material);

                return material.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + material.getName());
        }
    }

    public void removeMaterial(Material material) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Material removeMaterial = em.merge(material);
            em.remove(removeMaterial);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Material " + material.getId() + " | " + material.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateProduct(Product product) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Product", product.getName(), product.getId())) {
            try {
                em.merge(product);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + product.getName());
        }

    }

    public List<Product> getAllProducts() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Product.findAll");
        return query.getResultList();
    }

    public List<Product> getMfProducts(String mfName) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT d FROM DistributionMFtoStoreProd d WHERE d.mf = :fac");
        query.setParameter("fac", getFacility(mfName));
        return query.getResultList();
    }

    public Long addNewProduct(Product product) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Product", product.getName())) {
            try {
                product.setId(getItemId());
                em.persist(product);

                return product.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + product.getName());
        }
    }

    public void removeProduct(Product product) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Product removeProduct = em.merge(product);
            em.remove(removeProduct);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Product " + product.getId() + " | " + product.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateService(Service service) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if (!NameAlreadyExist("Service", service.getName(), service.getId())) {
            try {
                em.merge(service);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException("Name conflict: " + service.getName());
        }

    }

    public List<Service> getAllServices() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Service.findAll");
        return query.getResultList();
    }

    public Long addNewService(Service service) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Service", service.getName())) {
            try {
                service.setId(getId(service));
                em.persist(service);

                return service.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + service.getName());
        }
    }

    public void removeService(Service service) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Service removeService = em.merge(service);
            em.remove(removeService);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Service " + service.getId() + " | " + service.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateSupplier(Supplier supplier) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Supplier", supplier.getName(), supplier.getId())) {
            try {
                em.merge(supplier);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + supplier.getName());
        }

    }

    public List<Supplier> getAllSuppliers() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Supplier.findAll");
        return query.getResultList();
    }

    public Long addNewSupplier(Supplier supplier) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Supplier", supplier.getName())) {
            try {

                supplier.setId(getId(supplier));
                em.persist(supplier);

                return supplier.getId();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + supplier.getName());
        }
    }

    public void removeSupplier(Supplier supplier) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Supplier removeSupplier = em.merge(supplier);
            em.remove(removeSupplier);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Supplier " + supplier.getId() + " | " + supplier.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateSuppliesMatToFac(SuppliesMatToFac smtf)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long facId = smtf.getFac().getId();
        Long matId = smtf.getMat().getId();
        Long supId = smtf.getSup().getId();
        Long id = smtf.getId();
        String facName = smtf.getFac().getName();
        String matName = smtf.getMat().getName();
        String supName = smtf.getSup().getName();

        if (!EntityIdDNE("Facility", facId)) {
            if (!EntityIdDNE("Material", matId)) {
                if (!EntityIdDNE("Supplier", supId)) {
                    if (!SupMatFacAlreadyExist(facId, matId, supId, id)) {
                        try {
                            em.merge(smtf);

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Facility Material Supplier combination exists: " + facName + " " + " " + matName + " " + supName);
                    }
                } else {
                    throw new EntityDneException("Supplier DNE: " + supName);
                }
            } else {
                throw new EntityDneException("Material DNE: " + matName);
            }
        } else {
            throw new EntityDneException("Facility DNE: " + facName);
        }

    }

    public List<SuppliesMatToFac> getAllSuppliesMatToFacs() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("SuppliesMatToFac.findAll");
        return query.getResultList();
    }

    public Long addNewSuppliesMatToFac(Long facId, Long matId, Long supId, Integer lotSize, Double unitPrice, Integer leadTime)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Facility facility = em.find(Facility.class, facId);
        Material material = em.find(Material.class, matId);
        Supplier supplier = em.find(Supplier.class, supId);

        if (!EntityIdDNE("Facility", facId)) {
            if (!EntityIdDNE("Material", matId)) {
                if (!EntityIdDNE("Supplier", supId)) {
                    if (!SupMatFacAlreadyExist(facId, matId, supId)) {
                        try {
                            SuppliesMatToFac smtf = new SuppliesMatToFac();
                            smtf.setFac(facility);
                            smtf.setMat(material);
                            smtf.setSup(supplier);
                            smtf.setLotSize(lotSize);
                            smtf.setUnitPrice(unitPrice);
                            smtf.setLeadTime(leadTime);
                            smtf.setId(getId(smtf));

                            em.persist(smtf);

                            return smtf.getId();

                        } catch (Exception e) {
                            e.printStackTrace();

                            return null;

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Facility Material Supplier combination exists: " + facility.getName() + " " + " " + material.getName() + " " + supplier.getName());
                    }
                } else {
                    throw new EntityDneException("Supplier DNE: " + supplier.getName());
                }
            } else {
                throw new EntityDneException("Material DNE: " + material.getName());
            }
        } else {
            throw new EntityDneException("Facility DNE: " + facility.getName());
        }
    }

    public void removeSuppliesMatToFac(SuppliesMatToFac smtf) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            SuppliesMatToFac removeSuppliesMatToFac = em.merge(smtf);
            em.remove(removeSuppliesMatToFac);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete SMF Record for: " + smtf.getSup().getName() + " | " + smtf.getMat().getName() + " | " + smtf.getFac().getName() + " due to Foreign Key constraints");
        }
    }

    public void updateSuppliesProdToFac(SuppliesProdToFac smtf)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long facId = smtf.getFac().getId();
        Long prodId = smtf.getProd().getId();
        Long supId = smtf.getSup().getId();
        Long id = smtf.getId();
        String facName = smtf.getFac().getName();
        String prodName = smtf.getProd().getName();
        String supName = smtf.getSup().getName();

        if (!EntityIdDNE("Facility", facId)) {
            if (!EntityIdDNE("Product", prodId)) {
                if (!EntityIdDNE("Supplier", supId)) {
                    if (!SupProdFacAlreadyExist(facId, prodId, supId, id)) {
                        try {
                            em.merge(smtf);

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Facility Product Supplier combination exists: " + facName + " " + " " + prodName + " " + supName);
                    }
                } else {
                    throw new EntityDneException("Supplier DNE: " + supName);
                }
            } else {
                throw new EntityDneException("Product DNE: " + prodName);
            }
        } else {
            throw new EntityDneException("Facility DNE: " + facName);
        }

    }

    public List<SuppliesProdToFac> getAllSuppliesProdToFacs() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("SuppliesProdToFac.findAll");
        return query.getResultList();
    }

    public Long addNewSuppliesProdToFac(Long facId, Long prodId, Long supId, Integer lotSize, Double unitPrice, Integer leadTime)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Facility facility = em.find(Facility.class, facId);
        Product product = em.find(Product.class, prodId);
        Supplier supplier = em.find(Supplier.class, supId);
        if (!EntityIdDNE("Facility", facId)) {
            if (!EntityIdDNE("Product", prodId)) {
                if (!EntityIdDNE("Supplier", supId)) {
                    if (!SupProdFacAlreadyExist(facId, prodId, supId)) {
                        try {
                            SuppliesProdToFac smtf = new SuppliesProdToFac();
                            smtf.setFac(facility);
                            smtf.setProd(product);
                            smtf.setSup(supplier);
                            smtf.setLotSize(lotSize);
                            smtf.setUnitPrice(unitPrice);
                            smtf.setLeadTime(leadTime);
                            smtf.setId(getId(smtf));

                            em.persist(smtf);

                            return smtf.getId();

                        } catch (Exception e) {
                            e.printStackTrace();

                            return null;

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Facility Product Supplier combination exists: " + facility.getName() + " " + " " + product.getName() + " " + supplier.getName());
                    }
                } else {
                    throw new EntityDneException("Supplier DNE: " + supplier.getName());
                }
            } else {
                throw new EntityDneException("Product DNE: " + product.getName());
            }
        } else {
            throw new EntityDneException("Facility DNE: " + facility.getName());
        }
    }

    public void removeSuppliesProdToFac(SuppliesProdToFac sptf) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            SuppliesProdToFac removeSuppliesProdToFac = em.merge(sptf);
            em.remove(removeSuppliesProdToFac);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete SPF Record for: " + sptf.getFac().getName() + " | " + sptf.getProd().getName() + " | " + sptf.getSup().getName() + " due to Foreign Key constraints");
        }
    }

    public void updateDistributionMFtoStore(DistributionMFtoStore dmts)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long mfId = dmts.getMf().getId();
        Long matId = dmts.getMat().getId();
        Long storeId = dmts.getStore().getId();
        Long id = dmts.getId();

        if (!EntityIdDNE("Facility", mfId)) {
            if (!EntityIdDNE("Material", matId)) {
                if (!EntityIdDNE("Facility", storeId)) {
                    if (!DistributionMatStoreAlreadyExist(matId, storeId, id)) {
                        try {
                            em.merge(dmts);

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Material Store combination already exists: " + dmts.getMat().getName() + " " + dmts.getStore().getName());
                    }
                } else {
                    throw new EntityDneException("Store DNE: " + dmts.getStore().getName());
                }
            } else {
                throw new EntityDneException("Material DNE: " + dmts.getMat().getName());
            }
        } else {
            throw new EntityDneException("Manufacturing DNE: " + dmts.getMf().getName());
        }

    }

    public List<DistributionMFtoStore> getAllDistributionMFtoStores() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("DistributionMFtoStore.findAll");
        return query.getResultList();
    }

    public Long addNewDistributionMFtoStore(Long mfId, Long matId, Long storeId)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Facility mf = em.find(Facility.class, mfId);
        Facility store = em.find(Facility.class, storeId);
        Material material = em.find(Material.class, matId);
        if (!EntityIdDNE("Facility", mfId)) {
            if (!EntityIdDNE("Material", matId)) {
                if (!EntityIdDNE("Facility", storeId)) {
                    if (!DistributionMatStoreAlreadyExist(matId, storeId)) {
                        try {
                            DistributionMFtoStore dmts = new DistributionMFtoStore();
                            dmts.setMf(mf);
                            dmts.setMat(material);
                            dmts.setStore(store);
                            dmts.setId(getId(dmts));

                            em.persist(dmts);

                            return dmts.getId();

                        } catch (Exception e) {
                            e.printStackTrace();

                            return null;

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Material Store combination exists: " + material.getName() + " " + store.getName());
                    }
                } else {
                    throw new EntityDneException("Store DNE: " + store.getName());
                }
            } else {
                throw new EntityDneException("Material DNE: " + material.getName());
            }
        } else {
            throw new EntityDneException("Manufacturing DNE: " + mf.getName());
        }
    }

    public void removeDistributionMFtoStore(DistributionMFtoStore distributionMFtoStore) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            DistributionMFtoStore removeDistributionMFtoStore = em.merge(distributionMFtoStore);
            em.remove(removeDistributionMFtoStore);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Distribution Record for: " + distributionMFtoStore.getMf().getName() + " | " + distributionMFtoStore.getMat().getName() + " | " + distributionMFtoStore.getStore().getName() + " due to Foreign Key constraints");
        }
    }

    public void updateDistributionMFtoStoreProd(DistributionMFtoStoreProd dmtsp)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long mfId = dmtsp.getMf().getId();
        Long prodId = dmtsp.getProd().getId();
        Long storeId = dmtsp.getStore().getId();
        String mfName = dmtsp.getMf().getName();
        String prodName = dmtsp.getProd().getName();
        String storeName = dmtsp.getStore().getName();
        Long id = dmtsp.getId();

        if (!EntityIdDNE("Facility", mfId)) {
            if (!EntityIdDNE("Product", prodId)) {
                if (!EntityIdDNE("Facility", storeId)) {
                    if (!DistributionProdStoreAlreadyExist(prodId, storeId, id)) {
                        try {
                            em.merge(dmtsp);

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Product Store combination exists: " + prodName + " " + storeName);
                    }
                } else {
                    throw new EntityDneException("Store DNE: " + storeName);
                }
            } else {
                throw new EntityDneException("Product DNE: " + prodName);
            }
        } else {
            throw new EntityDneException("Manufacturing DNE: " + mfName);
        }
    }

    public List<DistributionMFtoStoreProd> getAllDistributionMFtoStoreProds() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("DistributionMFtoStoreProd.findAll");
        return query.getResultList();
    }

    public Long addNewDistributionMFtoStoreProd(Long mfId, Long prodId, Long storeId)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Facility mf = em.find(Facility.class, mfId);
        Product product = em.find(Product.class, prodId);
        Facility store = em.find(Facility.class, storeId);
        if (!EntityIdDNE("Facility", mfId)) {
            if (!EntityIdDNE("Product", prodId)) {
                if (!EntityIdDNE("Facility", storeId)) {
                    if (!DistributionProdStoreAlreadyExist(prodId, storeId)) {
                        try {
                            DistributionMFtoStoreProd dmtsp = new DistributionMFtoStoreProd();
                            dmtsp.setMf(mf);
                            dmtsp.setProd(product);
                            dmtsp.setStore(store);
                            dmtsp.setId(getId(dmtsp));

                            em.persist(dmtsp);

                            return dmtsp.getId();

                        } catch (Exception e) {
                            e.printStackTrace();

                            return null;

                        } finally {
                            em.close();
                        }
                    } else {
                        throw new DetailsConflictException("Product Store combination exists: " + product.getName() + " " + store.getName());
                    }
                } else {
                    throw new EntityDneException("Store DNE: " + store.getName());
                }
            } else {
                throw new EntityDneException("Product DNE: " + product.getName());
            }
        } else {
            throw new EntityDneException("Manufacturing DNE: " + mf.getName());
        }
    }

    public void removeDistributionMFtoStoreProd(DistributionMFtoStoreProd distributionMFtoStoreProd) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            DistributionMFtoStoreProd removeDistributionMFtoStoreProd = em.merge(distributionMFtoStoreProd);
            em.remove(removeDistributionMFtoStoreProd);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Distribution Record for: " + distributionMFtoStoreProd.getMf().getName() + " | " + distributionMFtoStoreProd.getProd().getName() + " | " + distributionMFtoStoreProd.getStore().getName() + " due to Foreign Key constraints");
        }
    }

    public void updateRegionItemPrice(RegionItemPrice fp)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long regionId = fp.getRegion().getId();
        Long itemId = fp.getItem().getId();
        Long id = fp.getId();
        String regionName = fp.getRegion().getName();
        String itemName = fp.getItem().getName();

        if (!EntityIdDNE("Region", regionId)) {
            if (!EntityIdDNE("Item", itemId)) {
                if (!PriceMatStoreAlreadyExist(itemId, regionId, id)) {
                    try {
                        em.merge(fp);

                    } catch (Exception e) {
                        e.printStackTrace();

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Region Itemcombination exists: " + regionName + " " + " " + itemName);
                }

            } else {
                throw new EntityDneException("Item DNE: " + itemName);
            }
        } else {
            throw new EntityDneException("Region DNE: " + regionName);
        }
    }

    public List<RegionItemPrice> getAllRegionItemPrices() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("RegionItemPrice.findAll");
        return query.getResultList();
    }

    public Long addNewRegionItemPrice(Long regionId, Long itemId, Double price)
            throws EntityDneException, DetailsConflictException {
        System.out.println("regionId: " + regionId);
        System.out.println("itemId: " + itemId);
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Region region = em.find(Region.class, regionId);
        Item item = em.find(Item.class, itemId);

        if (!EntityIdDNE("Region", regionId)) {
            if (!EntityIdDNE("Item", itemId)) {
                if (!PriceItemRegionAlreadyExist(itemId, regionId)) {
                    try {
                        RegionItemPrice fp = new RegionItemPrice();
                        fp.setRegion(region);
                        fp.setItem(item);
                        fp.setPrice(price);
                        fp.setId(getId(fp));

                        em.persist(fp);

                        return fp.getId();

                    } catch (Exception e) {
                        e.printStackTrace();

                        return null;

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Region Item combination exists: " + region.getName() + " " + " " + item.getName());
                }

            } else {
                throw new EntityDneException("Item DNE: " + item.getName());
            }
        } else {
            throw new EntityDneException("Region DNE: " + region.getName());
        }
    }

    public void removeRegionItemPrice(RegionItemPrice fp) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            RegionItemPrice removeRegionItemPrice = em.merge(fp);
            em.remove(removeRegionItemPrice);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Region Item Price Record for: " + fp.getRegion().getName() + " | " + fp.getRegion().getName() + " due to Foreign Key constraints");
        }
    }

    public void updateRegionServicePrice(RegionServicePrice fp)
            throws EntityDneException, DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Long regionId = fp.getRegion().getId();
        Long svcId = fp.getSvc().getId();
        Long id = fp.getId();
        String regionName = fp.getRegion().getName();
        String svcName = fp.getSvc().getName();

        if (!EntityIdDNE("Region", regionId)) {
            if (!EntityIdDNE("Service", svcId)) {
                if (!PriceSvcRegionAlreadyExist(svcId, regionId, id)) {
                    try {
                        em.merge(fp);

                    } catch (Exception e) {
                        e.printStackTrace();

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Region Service combination exists: " + regionName + " " + " " + svcName);
                }

            } else {
                throw new EntityDneException("Service DNE: " + svcName);
            }
        } else {
            throw new EntityDneException("Region DNE: " + regionName);
        }
    }

    public List<RegionServicePrice> getAllRegionServicePrices() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("RegionServicePrice.findAll");
        return query.getResultList();
    }

    public Long addNewRegionServicePrice(Long regionId, Long svcId, Double price)
            throws EntityDneException, DetailsConflictException {
        System.out.println("facId: " + regionId);
        System.out.println("svcId: " + svcId);
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Region region = em.find(Region.class, regionId);
        Service service = em.find(Service.class, svcId);

        if (!EntityIdDNE("Region", regionId)) {
            if (!EntityIdDNE("Service", svcId)) {
                if (!PriceSvcRegionAlreadyExist(svcId, regionId)) {
                    try {
                        RegionServicePrice fp = new RegionServicePrice();
                        fp.setRegion(region);
                        fp.setSvc(service);
                        fp.setPrice(price);
                        fp.setId(getId(fp));
                        em.persist(fp);

                        return fp.getId();

                    } catch (Exception e) {
                        e.printStackTrace();

                        return null;

                    } finally {
                        em.close();
                    }
                } else {
                    throw new DetailsConflictException("Region Service combination exists: " + region.getName() + " " + " " + service.getName());
                }

            } else {
                throw new EntityDneException("Service DNE: " + service.getName());
            }
        } else {
            throw new EntityDneException("Region DNE: " + region.getName());
        }
    }

    public void removeRegionServicePrice(RegionServicePrice fp) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            RegionServicePrice removeRegionServicePrice = em.merge(fp);
            em.remove(removeRegionServicePrice);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Service Price Record for: " + fp.getSvc().getName() + " | " + fp.getRegion().getName() + " due to Foreign Key constraints");
        }
    }

    public List<Facility> getAllMFs() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllMfs");
        return query.getResultList();
    }

    public List<Material> getAllFurniture() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findAllMaterial");
        return query.getResultList();
    }

    public List<Item> getAllItems() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Item.findAll");
        return query.getResultList();
    }

    public List<Material> getAllRawMat() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Material.findAllRawMat");
        return query.getResultList();
    }

    public List<Facility> getAllStores() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Facility.findAllStores");
        return query.getResultList();
    }

    public List<ProductionRecord> getProductionRecords(Facility mf, int year, int period) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + ProductionRecord.class
                .getName() + " p, " + DistributionMFtoStore.class
                .getName() + " d "
                + "WHERE p.store = d.store AND p.mat = d.mat AND d.mf = :mf AND p.year > :year OR (p.year = :year AND p.period > :period)");
        query.setParameter(
                "mf", mf);
        query.setParameter(
                "year", year);
        query.setParameter(
                "period", period);

        List<ProductionRecord> pr = query.getResultList();
        return pr;
    }

    public List<PurchasePlanningRecord> getPurchasePlanningRecords(Facility mf, int year, int period) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT p FROM " + PurchasePlanningRecord.class.getName() + " p, " + DistributionMFtoStoreProd.class.getName() + " d "
                + "WHERE p.store = d.store AND p.prod = d.prod AND d.mf = :mf AND p.year > :year OR (p.year = :year AND p.period > :period)");
        query.setParameter("mf", mf);
        query.setParameter("year", year);
        query.setParameter("period", period);

        List<PurchasePlanningRecord> pr = query.getResultList();
        return pr;
    }

    public List<PurchasePlanningRecord> getAllPurchasePlanningRecords() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("PurchasePlanningRecord.findAll");
        return query.getResultList();
    }

    private Boolean NameAlreadyExist(String entity, String name) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM " + entity + " f WHERE f.name = '" + name + "'");
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean NameAlreadyExist(String entity, String name, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM " + entity + " f WHERE f.name = '" + name + "' AND NOT f.id = " + id);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean LocationAlreadyExist(String entity, String postalCode, String city, Country country) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM " + entity + " f WHERE f.postalCode = '" + postalCode + "' AND f.city LIKE '"
                + city + "' AND f.country = :country");
        query.setParameter("country", country);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean LocationAlreadyExist(String entity, String postalCode, String city, Country country, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM " + entity + " f WHERE f.postalCode = '" + postalCode + "' AND f.city LIKE '"
                + city + "' AND f.country = :country AND NOT f.id = " + id);
        query.setParameter("country", country);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean RegionTypeAlreadyExist(Region region, String type) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM Facility f WHERE f.region= :region AND f.type = :type");
        query.setParameter("region", region);
        query.setParameter("type", type);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean RegionTypeAlreadyExist(Region region, String type, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT f FROM Facility f WHERE f.region= :region AND f.type = :type AND NOT f.id = :id");
        query.setParameter("region", region);
        query.setParameter("type", type);
        query.setParameter("id", id);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean SupMatFacAlreadyExist(Long facId, Long matId, Long supId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility fac = em.find(Facility.class, facId);
        Material mat = em.find(Material.class, matId);
        Supplier sup = em.find(Supplier.class, supId);
        Query query = em.createQuery("SELECT s FROM SuppliesMatToFac s WHERE s.fac= :inFac AND s.mat= :inMat AND s.sup= :inSup");
        query.setParameter("inFac", fac);
        query.setParameter("inMat", mat);
        query.setParameter("inSup", sup);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean SupMatFacAlreadyExist(Long facId, Long matId, Long supId, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility fac = em.find(Facility.class, facId);
        Material mat = em.find(Material.class, matId);
        Supplier sup = em.find(Supplier.class, supId);
        Query query = em.createQuery("SELECT s FROM SuppliesMatToFac s WHERE s.fac= :inFac AND s.mat= :inMat AND s.sup= :inSup AND NOT s.id = " + id);
        query.setParameter("inFac", fac);
        query.setParameter("inMat", mat);
        query.setParameter("inSup", sup);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean SupProdFacAlreadyExist(Long facId, Long prodId, Long supId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility fac = em.find(Facility.class, facId);
        Product prod = em.find(Product.class, prodId);
        Supplier sup = em.find(Supplier.class, supId);
        Query query = em.createQuery("SELECT s FROM SuppliesProdToFac s WHERE s.fac= :inFac AND s.prod= :inProd AND s.sup= :inSup");
        query.setParameter("inFac", fac);
        query.setParameter("inProd", prod);
        query.setParameter("inSup", sup);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean SupProdFacAlreadyExist(Long facId, Long prodId, Long supId, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility fac = em.find(Facility.class, facId);
        Product prod = em.find(Product.class, prodId);
        Supplier sup = em.find(Supplier.class, supId);
        Query query = em.createQuery("SELECT s FROM SuppliesProdToFac s WHERE s.fac= :inFac AND s.prod= :inProd AND s.sup= :inSup AND NOT s.id = " + id);
        query.setParameter("inFac", fac);
        query.setParameter("inProd", prod);
        query.setParameter("inSup", sup);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean DistributionMatStoreAlreadyExist(Long matId, Long storeId) {
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

    private Boolean DistributionMatStoreAlreadyExist(Long matId, Long storeId, Long id) {
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

    private Boolean PriceItemRegionAlreadyExist(Long itemId, Long regionId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Region region = em.find(Region.class, regionId);
        Item item = em.find(Item.class, itemId);
        Query query = em.createQuery("SELECT s FROM RegionItemPrice s WHERE s.region = :inRegion AND s.item= :inItem");
        query.setParameter("inRegion", region);
        query.setParameter("inItem", item);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean PriceMatStoreAlreadyExist(Long matId, Long storeId, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility store = em.find(Facility.class, storeId);
        Material mat = em.find(Material.class, matId);
        Query query = em.createQuery("SELECT s FROM FurniturePrice s WHERE s.store= :inStore AND s.mat= :inMat AND NOT s.id = " + id);
        query.setParameter("inStore", store);
        query.setParameter("inMat", mat);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean DistributionProdStoreAlreadyExist(Long prodId, Long storeId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility store = em.find(Facility.class, storeId);
        Product prod = em.find(Product.class, prodId);
        Query query = em.createQuery("SELECT s FROM DistributionMFtoStoreProd s WHERE s.store= :inStore AND s.prod= :inProd");
        query.setParameter("inStore", store);
        query.setParameter("inProd", prod);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean DistributionProdStoreAlreadyExist(Long prodId, Long storeId, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility store = em.find(Facility.class, storeId);
        Product prod = em.find(Product.class, prodId);
        Query query = em.createQuery("SELECT s FROM DistributionMFtoStoreProd s WHERE s.store= :inStore AND s.prod= :inProd AND NOT s.id = " + id);
        query.setParameter("inStore", store);
        query.setParameter("inProd", prod);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean PriceProdStoreAlreadyExist(Long prodId, Long storeId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility store = em.find(Facility.class, storeId);
        Product prod = em.find(Product.class, prodId);
        Query query = em.createQuery("SELECT s FROM ProductPrice s WHERE s.store= :inStore AND s.prod= :inProd");
        query.setParameter("inStore", store);
        query.setParameter("inProd", prod);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

//    private Boolean PriceProdStoreAlreadyExist(Long prodId, Long storeId, Long id) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Facility store = em.find(Facility.class, storeId);
//        Product prod = em.find(Product.class, prodId);
//        Query query = em.createQuery("SELECT s FROM ProductPrice s WHERE s.store= :inStore AND s.prod= :inProd AND NOT s.id = " + id);
//        query.setParameter("inStore", store);
//        query.setParameter("inProd", prod);
//        List resultList = query.getResultList();
//        if (resultList.isEmpty()) {
//            return false;
//        } else {
//            return true;
//        }
//    }
    private Boolean PriceSvcRegionAlreadyExist(Long svcId, Long regionId) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Region region = em.find(Region.class, regionId);
        Service svc = em.find(Service.class, svcId);
        Query query = em.createQuery("SELECT s FROM RegionServicePrice s WHERE s.region= :inStore AND s.svc= :inSvc");
        query.setParameter("inStore", region);
        query.setParameter("inSvc", svc);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean PriceSvcRegionAlreadyExist(Long svcId, Long regionId, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Region region = em.find(Region.class, regionId);
        Service svc = em.find(Service.class, svcId);
        Query query = em.createQuery("SELECT s FROM ServicePrice s WHERE s.region= :inRegion AND s.svc= :inSvc AND NOT s.id = " + id);
        query.setParameter("inRegion", region);
        query.setParameter("inSvc", svc);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
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

    public int verifyUser(String email, String password) {
        try {
            if (password.equals("")) {
                return 0;
            } else {
                EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
                EntityManager em = emf.createEntityManager();
                Query q;
                q = em.createQuery("SELECT s.password FROM Staff s WHERE s.email= :param");
                q.setParameter("param", email);
                String pwd = (String) q.getSingleResult();
                if (!(pwd.equals(password))) {
                    return -1;
                } else {

                    q = em.createQuery("SELECT s.role1 FROM Staff s WHERE s.email =:param");
                    q.setParameter("param", email);
                    List resultList = q.getResultList();
                    String role1 = "";
                    if (!resultList.isEmpty()) {
                        role1 = (String) q.getSingleResult();
                    }
                    q = em.createQuery("SELECT s.role2 FROM Staff s WHERE s.email= :param");
                    q.setParameter("param", email);
                    resultList = q.getResultList();
                    String role2 = "null";
                    if (!resultList.isEmpty()) {
                        role2 = (String) q.getSingleResult();
                    }
                    q = em.createQuery("SELECT s.role2 FROM Staff s WHERE s.email= :param");
                    q.setParameter("param", email);
                    resultList = q.getResultList();
                    String role3 = "null";
                    if (!resultList.isEmpty()) {
                        role3 = (String) q.getSingleResult();
                    }
                    System.out.println("role1: " + role1);
                    System.out.println("role2: " + role2);
                    System.out.println("role3: " + role3);

                    if (role1 != null && role1.equals("Global HQ") || (role2 != null && role2.equals("Global HQ")) || (role3 != null && role3.equals("Global HQ"))) {
                        return 1;
                    } else {
                        return -2;
                    }

                }
            }
        } catch (NoResultException ec) {
            System.err.println("ERROR");
            return 0;
        }
    }

    public void updateSetItem(SetItem setItem) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if (!NameAlreadyExist("SetItem", setItem.getName(), setItem.getId())) {
            try {
                em.merge(setItem);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException("Name conflict: " + setItem.getName());
        }

    }

    public List<SetItem> getAllSetItems() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT si FROM SetItem si");
        return query.getResultList();
    }

    public Long addNewSetItem(SetItem setItem) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("SetItem", setItem.getName())) {
            try {
                setItem.setId(getItemId());
                em.persist(setItem);

                return setItem.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + setItem.getName());
        }
    }

    public void removeSetItem(SetItem setItem) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            SetItem removeSetItem = em.merge(setItem);
            em.remove(removeSetItem);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete SetItem " + setItem.getId() + " | " + setItem.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateMenuItem(MenuItem menuItem) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if (!NameAlreadyExist("MenuItem", menuItem.getName(), menuItem.getId())) {
            try {
                em.merge(menuItem);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException("Name conflict: " + menuItem.getName());
        }

    }

    public List<MenuItem> getAllMenuItems() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT mi FROM MenuItem mi");
        return query.getResultList();
    }

    public Long addNewMenuItem(MenuItem menuItem) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("MenuItem", menuItem.getName())) {
            try {
                menuItem.setId(getItemId());
                em.persist(menuItem);

                return menuItem.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + menuItem.getName());
        }
    }

    public void removeMenuItem(MenuItem menuItem) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            MenuItem removeMenuItem = em.merge(menuItem);
            em.remove(removeMenuItem);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete MenuItem " + menuItem.getId() + " | " + menuItem.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateIngredient(Ingredient ingredient) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        if (!NameAlreadyExist("Ingredient", ingredient.getName(), ingredient.getId())) {
            try {
                em.merge(ingredient);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException("Name conflict: " + ingredient.getName());
        }

    }

    public List<Ingredient> getAllIngredients() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM Ingredient i");
        return query.getResultList();
    }

    public Long addNewIngredient(Ingredient ingredient) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        if (!NameAlreadyExist("Ingredient", ingredient.getName())) {
            try {
                ingredient.setId(getItemId());
                em.persist(ingredient);

                return ingredient.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Name conflict: " + ingredient.getName());
        }
    }

    public void removeIngredient(Ingredient ingredient) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Ingredient removeIngredient = em.merge(ingredient);
            em.remove(removeIngredient);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Ingredient " + ingredient.getId() + " | " + ingredient.getName() + " due to Foreign Key constraints");
        }
    }

    public void updateSuppliesIngrToFac(SuppliesIngrToFac suppliesIngrToFac) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility fac = suppliesIngrToFac.getFac();
        Ingredient ingredient = suppliesIngrToFac.getIngredient();
        Supplier sup = suppliesIngrToFac.getSup();
        Long id = suppliesIngrToFac.getId();
        if (!SupIngrFacAlreadyExist(fac, ingredient, sup, id)) {
            try {
                em.merge(suppliesIngrToFac);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException("Facility Ingredient Supplier combination exists: " + fac.getName() + " " + " " + ingredient.getName() + " " + sup.getName());
        }

    }

    public List<SuppliesIngrToFac> getAllSuppliesIngrToFacs() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM SuppliesIngrToFac i");
        return query.getResultList();
    }

    public Long addNewSuppliesIngrToFac(SuppliesIngrToFac suppliesIngrToFac) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Facility fac = suppliesIngrToFac.getFac();
        Ingredient ingredient = suppliesIngrToFac.getIngredient();
        Supplier sup = suppliesIngrToFac.getSup();
        if (!SupIngrFacAlreadyExist(fac, ingredient, sup)) {
            try {
                suppliesIngrToFac.setId(getId(suppliesIngrToFac));
                em.persist(suppliesIngrToFac);

                return suppliesIngrToFac.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Facility Ingredient Supplier combination exists: " + fac.getName() + " " + " " + ingredient.getName() + " " + sup.getName());
        }
    }

    public void removeSuppliesIngrToFac(SuppliesIngrToFac suppliesIngrToFac) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            SuppliesIngrToFac removeSuppliesIngrToFac = em.merge(suppliesIngrToFac);
            em.remove(removeSuppliesIngrToFac);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete SuppliesIngrToFac " + suppliesIngrToFac.getId() + " due to Foreign Key constraints");
        }
    }

    private Boolean SupIngrFacAlreadyExist(Facility fac, Ingredient ingredient, Supplier sup) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT s FROM SuppliesIngrToFac s WHERE s.fac= :inFac AND s.ingredient= :inIngredient AND s.sup= :inSup");
        query.setParameter("inFac", fac);
        query.setParameter("inIngredient", ingredient);
        query.setParameter("inSup", sup);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean SupIngrFacAlreadyExist(Facility fac, Ingredient ingredient, Supplier sup, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT s FROM SuppliesIngrToFac s WHERE s.fac= :inFac AND s.ingredient= :inIngredient AND s.sup= :inSup AND NOT s.id = :id");
        query.setParameter("inFac", fac);
        query.setParameter("inIngredient", ingredient);
        query.setParameter("inSup", sup);
        query.setParameter("id", id);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

//    public void updateRegionKitchenItemPrice(RegionKitchenItemPrice regionKitchenItemPrice) throws DetailsConflictException {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        KitchenItem kitchenItem = regionKitchenItemPrice.getKitchenItem();
//        Region region = regionKitchenItemPrice.getRegion();
//        Long id = regionKitchenItemPrice.getId();
//        if (!regionKitchenItemAlreadyExist(kitchenItem, region, id)) {
//            try {
//                em.merge(regionKitchenItemPrice);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                em.close();
//            }
//        } else {
//            throw new DetailsConflictException("KitchenItem Region combination exists: " + kitchenItem.getName() + " " + region.getName());
//        }
//
//    }

//    public List<RegionKitchenItemPrice> getAllRegionKitchenItemPrices() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query query = em.createQuery("SELECT i FROM RegionKitchenItemPrice i");
//        return query.getResultList();
//    }
//
//    public Long addNewRegionKitchenItemPrice(RegionKitchenItemPrice regionKitchenItemPrice) throws DetailsConflictException {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        KitchenItem kitchenItem = regionKitchenItemPrice.getKitchenItem();
//        Region region = regionKitchenItemPrice.getRegion();
//        if (!regionKitchenItemAlreadyExist(kitchenItem, region)) {
//            try {
//                em.persist(regionKitchenItemPrice);
//
//                return regionKitchenItemPrice.getId();
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
//            throw new DetailsConflictException("Kitchen Item Region combination exists: " + kitchenItem.getName() + " " + region.getName());
//        }
//    }
//
//    public void removeRegionKitchenItemPrice(RegionKitchenItemPrice regionKitchenItemPrice) throws ReferenceConstraintException {
//        try {
//            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//            EntityManager em = emf.createEntityManager();
//
//            RegionKitchenItemPrice removeRegionKitchenItemPrice = em.merge(regionKitchenItemPrice);
//            em.remove(removeRegionKitchenItemPrice);
//
//        } catch (Exception e) {
//            throw new ReferenceConstraintException("Cannot delete RegionKitchenItemPrice " + regionKitchenItemPrice.getId() + " due to Foreign Key constraints");
//        }
//    }
//
//    public List<KitchenItem> getKitchenMenuAndSetItems() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query query = em.createQuery("SELECT k FROM KitchenItem k WHERE NOT k.kitchenItemType = 'Ingredient'");
//        return query.getResultList();
//    }
//
//    public List<KitchenItem> getKitchenMenuItemsAndIngredients() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query query = em.createQuery("SELECT k FROM KitchenItem k WHERE NOT k.kitchenItemType = 'SetItem'");
//        return query.getResultList();
//    }
//
//    private Boolean regionKitchenItemAlreadyExist(KitchenItem kitchenItem, Region region) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query query = em.createQuery("SELECT s FROM RegionKitchenItemPrice s WHERE s.kitchenItem= :inKitchenItem AND s.region= :inRegion");
//        query.setParameter("inKitchenItem", kitchenItem);
//        query.setParameter("inRegion", region);
//        List resultList = query.getResultList();
//        if (resultList.isEmpty()) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    private Boolean regionKitchenItemAlreadyExist(KitchenItem kitchenItem, Region region, Long id) {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//
//        Query query = em.createQuery("SELECT s FROM RegionKitchenItemPrice s WHERE s.kitchenItem= :inKitchenItem AND s.region= :inRegion AND NOT s.id = :id");
//        query.setParameter("inKitchenItem", kitchenItem);
//        query.setParameter("inRegion", region);
//        query.setParameter("id", id);
//        List resultList = query.getResultList();
//        if (resultList.isEmpty()) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    public void updateComponent(Component component) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Item main = component.getMain();
        Item consistOf = component.getConsistOf();
        Long id = component.getId();
        if (!mainConsistOfAlreadyExist(main, consistOf, id)) {
            try {
                em.merge(component);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            throw new DetailsConflictException(" combination exists: " + main.getName() + " " + consistOf.getName());
        }

    }

    public List<Component> getAllComponents() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM Component i");
        return query.getResultList();
    }

//    public List<KitchenItem> getAllKitchenItems() {
//        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
//        EntityManager em = emf.createEntityManager();
//        Query query = em.createQuery("SELECT i FROM KitchenItem i");
//        return query.getResultList();
//    }

    public Long addNewComponent(Component component) throws DetailsConflictException {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Item consistOf = component.getConsistOf();
        Item main = component.getMain();
        if (!mainConsistOfAlreadyExist(consistOf, main)) {
            try {
                component.setId(getId(component));
                em.persist(component);

                return component.getId();

            } catch (Exception e) {
                e.printStackTrace();

                return null;

            } finally {
                em.close();

            }
        } else {
            throw new DetailsConflictException("Component combination exists: " + consistOf.getName() + " " + main.getName());
        }
    }

    public void removeComponent(Component component) throws ReferenceConstraintException {
        try {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
            EntityManager em = emf.createEntityManager();

            Component removeComponent = em.merge(component);
            em.remove(removeComponent);

        } catch (Exception e) {
            throw new ReferenceConstraintException("Cannot delete Component " + component.getId() + " due to Foreign Key constraints");
        }
    }

    private Boolean mainConsistOfAlreadyExist(Item consistOf, Item main) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT s FROM Component s WHERE s.consistOf= :inConsistOf AND s.main= :inMain");
        query.setParameter("inConsistOf", consistOf);
        query.setParameter("inMain", main);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean mainConsistOfAlreadyExist(Item consistOf, Item main, Long id) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT s FROM Component s WHERE s.consistOf= :inConsistOf AND s.main= :inMain AND NOT s.id = :id");
        query.setParameter("inConsistOf", consistOf);
        query.setParameter("inMain", main);
        query.setParameter("id", id);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<Item> getAllMatAndKits() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM Item i WHERE i.ItemType LIKE 'Material' OR i.ItemType LIKE 'MenuItem' OR i.ItemType LIKE 'SetItem'");
        return query.getResultList();
    }

    public List<Item> getAllMatItems() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM Item i WHERE i.ItemType LIKE 'Material'");
        return query.getResultList();
    }

    public List<Item> getAllKitItems() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("IslandSystem-ejbPU");
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT i FROM Item i WHERE i.ItemType LIKE 'Ingredient' OR i.ItemType LIKE 'MenuItem'");
        return query.getResultList();
    }

}
