package owlinone.pae.calendrier;

import java.io.Serializable;

/**
 * Created by AnthonyCOPPIN on 04/01/2017.
 */

public class EventCalendar implements Serializable {
    private int strID;
    private String strDate;
    private String strTitre;
    private int iMonth;
    private int iDay;
    private int iYear;
    private int iHeure;
    private int iMinute;
    private String strEvent;

    public EventCalendar (int strID, String strDate, String strTitre) {
        this.strID = strID;
        this.strTitre = strTitre;

        this.strDate = strDate;

    }

    public int getStrID() {
        return strID;
    }

    public void setStrID(int strID) {
        this.strID = strID;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrTitre() {
        return strTitre;
    }

    public void setStrTitre(String strTitre) {
        this.strTitre = strTitre;
    }

    public EventCalendar() {
        super();
    }

    public int getiDay() {
        return iDay;
    }

    public void setiDay(int iDay) {
        this.iDay = iDay;
    }

    public int getiMonth() {
        return iMonth;
    }

    public void setiMonth(int iMonth) {
        this.iMonth = iMonth;
    }

    public int getiYear() {
        return iYear;
    }

    public void setiYear(int iYear) {this.iYear = iYear;}

    public int getiHeure() {
        return iHeure;
    }

    public void setiHeure(int iHeure) {this.iHeure = iHeure;}

    public int getiMinute() {
        return iMinute;
    }

    public void setiMinute(int iMinute) {this.iMinute = iMinute;}

    public String getStrEvent() {
        return strEvent;
    }

    public void setStrEvent(String strEvent) {
        this.strEvent = strEvent;
    }

}
