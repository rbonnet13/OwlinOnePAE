package owlinone.pae.appartement;

import java.io.Serializable;

/**
 * Created by AnthonyCOPPIN on 19/03/2018.
 */

public class Appart implements Serializable {

    private int strID;
    private String strNom        = "";
    private String strAdresse    = "";
    private String strVille      = "";
    private String strDescript   = "";
    private String strDetail     = "";
    private String strTel        = "";
    private String prix          = "";
    private String strDispo      = "";
    private String strCp         = "";
    private String strMail       = "";
    private String strLongitude  = "";
    private String strLatitude   = "";
    private String strImagePrinc = "";
    private String strImageSecond= "";

    private double longitude;
    private double latitude;

    public Appart (int strID, String strNom, String strAdresse, String strVille, String strDescript,
                   String strDetail, String strTel, String prix, String strDispo, String strCp,
                   String strMail, double longitude, double latitude, String strImagePrinc, String strImageSecond) {
        this.strID         = strID;
        this.strNom        = strNom;
        this.strAdresse    = strAdresse;
        this.strVille      = strVille;
        this.strDescript   = strDescript;
        this.strDetail     = strDetail;
        this.strTel        = strTel;
        this.prix          = prix;
        this.strDispo      = strDispo;
        this.strCp         = strCp;
        this.strMail       = strMail;
        this.longitude     = longitude;
        this.latitude      = latitude;
        this.strImagePrinc = strImagePrinc;
        this.strImageSecond= strImageSecond;
    }
    public Appart() {
        super();
    }
    public int getStrID() {
        return strID;
    }

    public void setStrID(int strID) {
        this.strID = strID;
    }

    public String getStrNom() {
        return strNom;
    }

    public void setStrNom(String strNom) {
        this.strNom = strNom;
    }

    public String getStrAdresse() {
        return strAdresse;
    }

    public void setStrAdresse(String strAdresse) {
        this.strAdresse = strAdresse;
    }

    public String getStrVille() {
        return strVille;
    }

    public void setStrVille(String strVille) {
        this.strVille = strVille;
    }

    public String getStrDescript() {
        return strDescript;
    }

    public void setStrDescript(String strDescript) {
        this.strDescript = strDescript;
    }

    public String getStrDetail() {
        return strDetail;
    }

    public void setStrDetail(String strDetail) {
        this.strDetail = strDetail;
    }

    public String getStrTel() {
        return strTel;
    }

    public void setStrTel(String strTel) {
        this.strTel = strTel;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getStrDispo() {
        return strDispo;
    }

    public void setStrDispo(String strDispo) {
        this.strDispo = strDispo;
    }

    public String getStrCp() {
        return strCp;
    }

    public void setStrCp(String strCp) {
        this.strCp = strCp;
    }

    public String getStrMail() {
        return strMail;
    }

    public void setStrMail(String strMail) {
        this.strMail = strMail;
    }

    public String getStrImagePrinc() {
        return strImagePrinc;
    }

    public void setStrImagePrinc(String strImagePrinc) {
        this.strImagePrinc = strImagePrinc;
    }

    public String getStrImageSecond() {
        return strImageSecond;
    }

    public void setStrImageSecond(String strImageSecond) {
        this.strImageSecond = strImageSecond;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}