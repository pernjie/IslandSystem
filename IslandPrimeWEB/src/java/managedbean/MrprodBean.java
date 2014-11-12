package managedbean;

import classes.ItemClass;
import classes.ItemWeek;
import classes.MrpItemClass;
import classes.StoreClass;
import classes.StoreClassWeekly;
import classes.WeekClass;
import classes.WeekHelper;
import entity.Facility;
import entity.Item;
import entity.MrpRecord;
import entity.Product;
import entity.PurchasePlanningRecord;
import entity.Staff;
import entity.SuppliesProdToFac;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import session.stateless.CIBeanLocal;
import session.stateless.MrpBean;

@ManagedBean(name = "mrprodBean")
@ViewScoped
public class MrprodBean {

    @EJB
    private MrpBean mb;
    @EJB
    private CIBeanLocal cib;
    private List<ItemClass> prods;
    private List<MrpItemClass> mrprods;
    private boolean persisted;
    private Facility mf;
    private WeekHelper wh = new WeekHelper();
    @Inject
    private SalesHistoryBeanRetail rhb;
    @Inject
    private DemandManagementBeanRetail drb;

    public MrprodBean() {
    }

    @PostConstruct
    public void init() {
        mf = rhb.getMf();
        persisted = false;
        mrprods = new ArrayList<MrpItemClass>();
        prods = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("products");
        for (ItemClass prod : prods) {
            for (StoreClass store : prod.getStores()) {
                //TODO mrprecord
                int amount = store.getMonth6().getPp();
                List<MrpRecord> mrpr = mb.getMrpRecord(mf, prod.getMat());
                SuppliesProdToFac sptf = mb.getSptf(mf, (Product) prod.getMat());
                int leadtime = sptf.getLeadTime();
                int lotsize = sptf.getLotSize();
                int monthlyreq = amount;
                int tolerance = 0;

                int inv;
                if (mrpr.isEmpty()) {
                    System.out.println("mrp here");
                    inv = mb.getInventoryIndiv(prod.getMat(),sptf.getFac());
                } else {
                    System.out.println("mrp here2");
                    inv = mrpr.get(mrpr.size() - 1).getOnHand();
                }

                WeekClass[] weeks = {
                    new WeekClass(0, 0, inv, 0),
                    new WeekClass(),
                    new WeekClass(),
                    new WeekClass(),
                    new WeekClass()
                };

                for (int i = 1; i < 5; i++) {
                    weeks[i].setReq(wh.getWeeklyDemand(monthlyreq, i));
                    int temp = weeks[i].getReq() - weeks[i - 1].getOnh();
                    if (temp <= tolerance) {
                        weeks[i].setRec(0);
                    } else {
                        if (temp % lotsize == 0) {
                            weeks[i].setRec(temp);
                        } else {
                            weeks[i].setRec(lotsize);
                            while (weeks[i].getRec() < temp) {
                                weeks[i].setRec(weeks[i].getRec() + lotsize);
                            }
                        }
                    }
                    weeks[i].setOnh(weeks[i - 1].getOnh() + weeks[i].getRec() - weeks[i].getReq());
                    if (i > leadtime) {
                        weeks[i - leadtime].setPln(weeks[i].getRec());
                        weeks[i - leadtime].setPlnOriginal(weeks[i - leadtime].getPln());
                    }
                }
                //TODO need to update past records

                MrpItemClass mrprod = new MrpItemClass();
                mrprod.setMat(prod.getMat());
                //WeekClass week = new WeekClass(100,100,100,100);
                mrprod.setWeek1(weeks[1]);
                mrprod.setWeek2(weeks[2]);
                mrprod.setWeek3(weeks[3]);
                mrprod.setWeek4(weeks[4]);
                mrprods.add(mrprod);
            }
        }
    }

    public void validateInput() throws IOException {
        System.out.println("initialize validateInput MrprodBean");
        Boolean valid = true;
        for (int i = 0; i < mrprods.size(); i++) {
            SuppliesProdToFac sptf = mb.getSptf(mf, (Product)mrprods.get(i).getMat());
            int leadtime = sptf.getLeadTime();
            if (leadtime == 1) {
                Integer w1 = mrprods.get(i).getWeek1().getPln();
                Integer w2 = mrprods.get(i).getWeek2().getPln();
                Integer w3 = mrprods.get(i).getWeek3().getPln();
                Integer o1 = mrprods.get(i).getWeek1().getPlnOriginal();
                Integer o2 = mrprods.get(i).getWeek2().getPlnOriginal();
                Integer o3 = mrprods.get(i).getWeek3().getPlnOriginal();
                System.out.println("edited: " + w1 + " " + w2 + " " + w3);
                System.out.println("original: " + o1 + " " + o2 + " " + o3);
                if (w1 < o1 || w2 < o2 || w3 < o3) {
                     String msg = "Input for " + mrprods.get(i).getMat().getId()+": " + mrprods.get(i).getMat().getName() +" is insufficient for production.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            } else if (leadtime == 2) {
                Integer w1 = mrprods.get(i).getWeek1().getPln();
                Integer w2 = mrprods.get(i).getWeek2().getPln();
                Integer o1 = mrprods.get(i).getWeek1().getPlnOriginal();
                Integer o2 = mrprods.get(i).getWeek2().getPlnOriginal();
                System.out.println("edited: " + w1 + " " + w2 + " ");
                System.out.println("original: " + o1 + " " + o2 + " ");
                if (w1 < o1 || w2 < o2) {
                     String msg = "Input for " + mrprods.get(i).getMat().getId()+": " + mrprods.get(i).getMat().getName() +" is insufficient for production.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            } else if (leadtime == 3) {
                Integer w1 = mrprods.get(i).getWeek1().getPln();
                Integer o1 = mrprods.get(i).getWeek1().getPlnOriginal();
                System.out.println("edited: " + w1);
                System.out.println("original: " + o1);
                if (w1 < o1) {
                    String msg = "Input for " + mrprods.get(i).getMat().getId()+": " + mrprods.get(i).getMat().getName() +" is insufficient for production.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            }
        }
        if (valid == true) {
            persistMrprecords();
        }
    }

    public void persistMrprecords() {
        List<MrpRecord> finalrecords = new ArrayList<MrpRecord>();
        for (MrpItemClass mrprod : mrprods) {
            MrpRecord mr = new MrpRecord();
            mr.setFac(mf);
            mr.setMat((Item) mrprod.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5));
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrprod.getWeek1().getOnh());
            mr.setPlanned(mrprod.getWeek1().getPln());
            mr.setReceipt(mrprod.getWeek1().getRec());
            mr.setRequirement(mrprod.getWeek1().getReq());
            finalrecords.add(mr);

            mr = new MrpRecord();
            mr.setFac(mf);
            mr.setMat((Item) mrprod.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5) + 1);
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrprod.getWeek2().getOnh());
            mr.setPlanned(mrprod.getWeek2().getPln());
            mr.setReceipt(mrprod.getWeek2().getRec());
            mr.setRequirement(mrprod.getWeek2().getReq());
            finalrecords.add(mr);

            mr = new MrpRecord();
            mr.setFac(mf);
            mr.setMat((Item) mrprod.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5) + 2);
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrprod.getWeek3().getOnh());
            mr.setPlanned(mrprod.getWeek3().getPln());
            mr.setReceipt(mrprod.getWeek3().getRec());
            mr.setRequirement(mrprod.getWeek3().getReq());
            finalrecords.add(mr);

            mr = new MrpRecord();
            mr.setFac(mf);
            mr.setMat((Item) mrprod.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5) + 3);
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrprod.getWeek4().getOnh());
            mr.setPlanned(mrprod.getWeek4().getPln());
            mr.setReceipt(mrprod.getWeek4().getRec());
            mr.setRequirement(mrprod.getWeek4().getReq());
            finalrecords.add(mr);

            List<MrpRecord> mrpr = mb.getMrpRecord(mf, (Item) mrprod.getMat());
            SuppliesProdToFac sptf = mb.getSptf(mf, (Product)mrprod.getMat());
            int leadtime = sptf.getLeadTime();

            int[] vals = {mrprod.getWeek1().getRec(), mrprod.getWeek2().getRec(), mrprod.getWeek3().getRec()};
            for (int i = 0; i < 4 - leadtime; i++) {
                if (mrpr.size() > i) {
                    mrpr.get(mrpr.size() - 1 - i).setPlanned(vals[2 - i]);
                } else {
                    break;
                }
                mb.persistMrpRecords(mrpr);
            }

        }
        mb.persistMrpRecords(finalrecords);

        List<PurchasePlanningRecord> prs = new ArrayList<PurchasePlanningRecord>();
        for (ItemWeek m : (List<ItemWeek>)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("weeklyDemand")) {
            for (StoreClassWeekly s : m.getStores()) {
                PurchasePlanningRecord pr = new PurchasePlanningRecord();
                pr.setStore(s.getStore());
                pr.setProd((Product) m.getMat());
                pr.setPeriod(wh.getPeriod(5));
                pr.setYear(wh.getYear(5));
                pr.setQuantityW1(s.getWeek1());
                pr.setQuantityW2(s.getWeek2());
                pr.setQuantityW3(s.getWeek3());
                pr.setQuantityW4(s.getWeek4());
                prs.add(pr);
            }
        }
        
        if (mb.persistPurchasePlanningRecords(prs)) {
            persisted = true;
        }

        if (persisted) {
            try {
                Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
                cib.addLog(staff, "Done Material Planning (Retail) for period " + wh.getPeriod(5));
                FacesContext.getCurrentInstance().getExternalContext().redirect("../mrp/mrp_home.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FacesMessage msg = new FacesMessage("Error", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public List<MrpItemClass> getMrprods() {
        return mrprods;
    }

    public boolean isPersisted() {
        return persisted;
    }
}
