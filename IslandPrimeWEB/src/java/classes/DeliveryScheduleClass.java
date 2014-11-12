/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.Date;

public class DeliveryScheduleClass {

    private Long matid;
    private Long storeid;
    private int year;
    private int period;
    private int quantity;
    private int week;

    public DeliveryScheduleClass(Long matid, Long storeid, int year, int period, int quantity, int week) {
        this.matid = matid;
        this.storeid = storeid;
        this.year = year;
        this.period = period;
        this.quantity = quantity;
        this.week = week;
    }

    public Long getMatid() {
        return matid;
    }

    public void setMatid(Long matid) {
        this.matid = matid;
    }

    public Long getStoreid() {
        return storeid;
    }

    public void setStoreid(Long storeid) {
        this.storeid = storeid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

}
