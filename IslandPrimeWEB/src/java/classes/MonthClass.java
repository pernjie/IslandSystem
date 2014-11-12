package classes;

public class MonthClass {
    private int fc;
    private int pp;
    private int inv;
    
    public MonthClass (int fc, int pp, int inv) {
        this.fc = fc;
        this.pp = pp;
        this.inv = inv;
    }

    public int getFc() {
        return fc;
    }

    public void setFc(int fc) {
        this.fc = fc;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    public int getInv() {
        return inv;
    }

    public void setInv(int inv) {
        this.inv = inv;
    }
}

