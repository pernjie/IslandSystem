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
import entity.ProductionRecord;
import entity.Staff;
import entity.SuppliesIngrToFac;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import session.stateless.CIBeanLocal;
import session.stateless.MrpBean;

@ManagedBean(name = "mrpkitBean")
@RequestScoped
public class MrpkitBean implements Serializable {

    @EJB
    private MrpBean mb;
    @EJB 
    private CIBeanLocal cib;
    private List<ItemClass> mats;
    private List<MrpItemClass> mrpials;
    private Map<Long, Integer> rawmatReq;
    private boolean persisted;
    private Facility fac;
    private WeekHelper wh = new WeekHelper();
    @Inject
    private SalesHistoryBeanKitchen shb;
    @Inject
    private DemandManagementBeanKitchen dmb;

    public MrpkitBean() {
    }

    @PostConstruct
    public void init() {
        fac = shb.getFac();
        persisted = false;
        mrpials = new ArrayList<MrpItemClass>();
        mats = (List)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("materials");
        Map<Long, Integer> matReq = new HashMap<Long, Integer>();
        for (ItemClass mat : mats) {
            for (StoreClass store : mat.getStores()) {
                matReq.put(mat.getMat().getId(), store.getMonth6().getPp());
            }
        }
        rawmatReq = mb.calcRawMats(matReq);
        System.out.println("rawmats: " + rawmatReq.toString());

        Iterator it = rawmatReq.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            //TODO mrprecord
            Item mat = mb.getItem((long) pairs.getKey());
            List<MrpRecord> mrpr = mb.getMrpRecord(fac, mat);
            SuppliesIngrToFac smtf = mb.getSitf(fac, mat);
            int leadtime = smtf.getLeadTime();
            int lotsize = smtf.getLotSize();
            int monthlyreq = (int) pairs.getValue();
            int tolerance = 0;

            int inv;
            if (mrpr.isEmpty()) {
                inv = mb.getInventoryIndiv(mb.getItem((long) pairs.getKey()),smtf.getFac());
            } else {
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

            MrpItemClass mrpial = new MrpItemClass();
            mrpial.setMat(mb.getItem((long) pairs.getKey()));
            //WeekClass week = new WeekClass(100,100,100,100);
            mrpial.setWeek1(weeks[1]);
            mrpial.setWeek2(weeks[2]);
            mrpial.setWeek3(weeks[3]);
            mrpial.setWeek4(weeks[4]);
            mrpials.add(mrpial);
        }
    }

    public List<MrpItemClass> getMrpials() {
        return mrpials;
    }

    public void validateInput() throws IOException {
        System.out.println("initialize validateInput MrpialBean");
        Boolean valid = true;
        for (int i = 0; i < mrpials.size(); i++) {
            SuppliesIngrToFac smtf = mb.getSitf(fac, mrpials.get(i).getMat());
            int leadtime = smtf.getLeadTime();
            if (leadtime == 1) {
                Integer w1 = mrpials.get(i).getWeek1().getPln();
                Integer w2 = mrpials.get(i).getWeek2().getPln();
                Integer w3 = mrpials.get(i).getWeek3().getPln();
                Integer o1 = mrpials.get(i).getWeek1().getPlnOriginal();
                Integer o2 = mrpials.get(i).getWeek2().getPlnOriginal();
                Integer o3 = mrpials.get(i).getWeek3().getPlnOriginal();
                System.out.println("edited: " + w1 + " " + w2 + " " + w3);
                System.out.println("original: " + o1 + " " + o2 + " " + o3);
                if (w1 < o1 || w2 < o2 || w3 < o3) {
                    String msg = "Input for " + mrpials.get(i).getMat().getId()+": "+mrpials.get(i).getMat().getName() + " is insufficient for production.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            } else if (leadtime == 2) {
                Integer w1 = mrpials.get(i).getWeek1().getPln();
                Integer w2 = mrpials.get(i).getWeek2().getPln();
                Integer o1 = mrpials.get(i).getWeek1().getPlnOriginal();
                Integer o2 = mrpials.get(i).getWeek2().getPlnOriginal();
                System.out.println("edited: " + w1 + " " + w2 + " ");
                System.out.println("original: " + o1 + " " + o2 + " ");
                if (w1 < o1 || w2 < o2) {
                    String msg = "Input for " + mrpials.get(i).getMat().getId()+": "+mrpials.get(i).getMat().getName() + " is insufficient for production.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + msg, ""));
                    valid = false;
                }
            } else if (leadtime == 3) {
                Integer w1 = mrpials.get(i).getWeek1().getPln();
                Integer o1 = mrpials.get(i).getWeek1().getPlnOriginal();
                System.out.println("edited: " + w1);
                System.out.println("original: " + o1);
                if (w1 < o1) {
                    String msg = "Input for " + mrpials.get(i).getMat().getId()+": "+mrpials.get(i).getMat().getName() + " is insufficient for production.";
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
        for (MrpItemClass mrpial : mrpials) {
            System.out.println("onhadnafter:\n"
                    + "w4:" + mrpial.getWeek4().getOnh());
            List<MrpRecord> mrpr = mb.getMrpRecord(fac, mrpial.getMat());
            SuppliesIngrToFac smtf = mb.getSitf(fac, mrpial.getMat());
            int leadtime = smtf.getLeadTime();
            MrpRecord mr = new MrpRecord();
            
            int[] vals = {mrpial.getWeek1().getRec(), mrpial.getWeek2().getRec(), mrpial.getWeek3().getRec()};
            int [] vals2 = {mrpial.getWeek1().getPln(), mrpial.getWeek2().getPln(), mrpial.getWeek3().getPln()};
            for (int i = 0; i < 4 - leadtime; i++) {
                if (mrpr.size() > i) {
                    mrpr.get(mrpr.size() - 1 - i).setPlanned(vals[2 - i]);
                } else {
                    break;
                }
                int update = 1 + i + leadtime;
                if (update == 2)
                    mrpial.getWeek2().setRec(vals2[i]);
                else if (update == 3)
                    mrpial.getWeek3().setRec(vals2[i]);
                else if (update == 4)
                    mrpial.getWeek4().setRec(vals2[i]);
                mb.persistMrpRecords(mrpr);
            }
            
            mr.setFac(fac);
            mr.setMat(mrpial.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5));
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrpial.getWeek1().getOnh());
            mr.setPlanned(mrpial.getWeek1().getPln());
            mr.setReceipt(mrpial.getWeek1().getRec());
            mr.setRequirement(mrpial.getWeek1().getReq());
            finalrecords.add(mr);

            mr = new MrpRecord();
            mr.setFac(fac);
            mr.setMat(mrpial.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5) + 1);
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrpial.getWeek2().getOnh());
            mr.setPlanned(mrpial.getWeek2().getPln());
            mr.setReceipt(mrpial.getWeek2().getRec());
            mr.setRequirement(mrpial.getWeek2().getReq());
            finalrecords.add(mr);

            mr = new MrpRecord();
            mr.setFac(fac);
            mr.setMat(mrpial.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5) + 2);
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrpial.getWeek3().getOnh());
            mr.setPlanned(mrpial.getWeek3().getPln());
            mr.setReceipt(mrpial.getWeek3().getRec());
            mr.setRequirement(mrpial.getWeek3().getReq());
            finalrecords.add(mr);

            mr = new MrpRecord();
            mr.setFac(fac);
            mr.setMat(mrpial.getMat());
            mr.setWeek(wh.getPeriodFirstWeek(5) + 3);
            mr.setYear(wh.getYear(5));

            mr.setOnHand(mrpial.getWeek4().getOnh());
            mr.setPlanned(mrpial.getWeek4().getPln());
            mr.setReceipt(mrpial.getWeek4().getRec());
            mr.setRequirement(mrpial.getWeek4().getReq());
            finalrecords.add(mr);
        }
        mb.persistMrpRecords(finalrecords);

        List<ProductionRecord> prs = new ArrayList<ProductionRecord>();
        for (ItemWeek m : (List<ItemWeek>)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("weeklyDemand")) {
            for (StoreClassWeekly s : m.getStores()) {
                ProductionRecord pr = new ProductionRecord();
                pr.setStore(s.getStore());
                pr.setMat(m.getMat());
                pr.setPeriod(wh.getPeriod(5));
                pr.setYear(wh.getYear(5));
                pr.setQuantityW1(s.getWeek1());
                pr.setQuantityW2(s.getWeek2());
                pr.setQuantityW3(s.getWeek3());
                pr.setQuantityW4(s.getWeek4());
                prs.add(pr);
            }
        }

        if (mb.persistProductionRecords(prs)) {
            persisted = true;
        }

        if (persisted) {
            try {
                Staff staff = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staff");
                cib.addLog(staff, "Done Material Planning (Kitchen) for period " + wh.getPeriod(5));
                FacesContext.getCurrentInstance().getExternalContext().redirect("../kitchen/kitchen_Home.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FacesMessage msg = new FacesMessage("Eror", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public boolean isPersisted() {
        return persisted;
    }
}
