package owlinone.pae.covoiturage;

import java.io.Serializable;

/**
 * Created by rudyb on 13/02/2018.
 */

public class UserGeo implements Serializable {
    private int strID;
    private String strPrenom     = "";
    private String strNom = "";
    private String strEmail       = "";
    private String strTelephone     = "";
    private String strVille      = "";
    private String strAdresse      = "";
    private String strCP    = "";
    private String strLatitude      = "";
    private String strLongitude      = "";




    public UserGeo (int strID, String strPrenom, String strNom, String strEmail, String strTelephone, String strVille, String strAdresse, String strCP, String strLatitude, String strLongitude  ) {
        this.strID = strID;
        this.strPrenom = strPrenom;
        this.strNom = strNom;
        this.strEmail = strEmail;
        this.strTelephone = strTelephone;
        this.strVille = strVille;
        this.strAdresse = strAdresse;
        this.strCP = strCP;
        this.strLatitude = strLatitude;
        this.strLongitude = strLongitude;
    }

    public int getStrID() {
        return strID;
    }

    public void setStrID(int strID) {
        this.strID = strID;
    }

    public String getStrPrenom() {
        return strPrenom;
    }

    public void setStrPrenom(String strPrenom) {
        this.strPrenom = strPrenom;
    }

    public String getStrNom() {
        return strNom;
    }

    public void setStrNom(String strNom) {
        this.strNom = strNom;
    }

    public String getStrEmail() {
        return strEmail;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public String getStrTelephone() {
        return strTelephone;
    }

    public void setStrTelephone(String strTelephone) {
        this.strTelephone = strTelephone;
    }

    public String getStrVille() {
        return strVille;
    }

    public void setStrVille(String strVille) {
        this.strVille = strVille;
    }

    public String getStrAdresse() {
        return strAdresse;
    }

    public void setStrAdresse(String strAdresse) {
        this.strAdresse = strAdresse;
    }

    public String getStrCP() {
        return strCP;
    }

    public void setStrCP(String strCP) {
        this.strCP = strCP;
    }

    public String getStrLatitude() {
        return strLatitude;
    }

    public void setStrLatitude(String strLatitude) {
        this.strLatitude = strLatitude;
    }

    public String getStrLongitude() {
        return strLongitude;
    }

    public void setStrLongitude(String strLongitude) {
        this.strLongitude = strLongitude;
    }
}
