package classes;

public class WeekClass {

    private int req;
    private int rec;
    private int onh;
    private int pln;
    private int plnOriginal;
    
    public WeekClass() {
        this.req = 0;
        this.rec = 0;
        this.onh = 0;
        this.pln = -1;
    }
    
    public WeekClass(int req, int rec, int onh, int pln) {
        this.req = req;
        this.rec = rec;
        this.onh = onh;
        this.pln = pln;
    }

    public int getReq() {
        return req;
    }

    public void setReq(int req) {
        this.req = req;
    }

    public int getRec() {
        return rec;
    }

    public void setRec(int rec) {
        this.rec = rec;
    }

    public int getOnh() {
        return onh;
    }

    public void setOnh(int onh) {
        this.onh = onh;
    }

    public int getPln() {
        return pln;
    }

    public void setPln(int pln) {
        this.pln = pln;
    }

    public int getPlnOriginal() {
        return plnOriginal;
    }

    public void setPlnOriginal(int plnOriginal) {
        this.plnOriginal = plnOriginal;
    }

}
