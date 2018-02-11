package owlinone.pae.article;

/**
 * Created by AnthonyCOPPIN on 18/12/2016.
 */

import java.io.Serializable;

/**
 * Created by rudybonnet on 07/12/2016.
 */

public class Article implements Serializable {

    private int strID;
    private String strTitre     = "";
    private String strCategorie = "";
    private String strImg       = "";
    private String strCorps     = "";
    private String strDate      = "";
    private int intVueArticle;
    private int intLike;
    private boolean like;

    public Article (int strID, String strTitre, String strCategorie, String strImg, String strCorps, String strDate, int intVueArticle, boolean like) {
        this.strID = strID;
        this.strTitre = strTitre;
        this.strCategorie = strCategorie;
        this.strImg = strImg;
        this.strCorps = strCorps;
        this.strDate = strDate;
        this.intVueArticle = intVueArticle;
        this.like = like;
    }

    public String getStrCategorie() {
        return " " + strCategorie + " ";
    }

    public void setStrCategorie(String strCategorie) {
        this.strCategorie = strCategorie;
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

    public Article() {
        super();
    }

    public String getStrCorps() {
        return strCorps;
    }

    public void setStrCorps(String strCorps) {
        this.strCorps = strCorps;
    }

    public String getStrTitre() {
        return strTitre;
    }

    public void setStrTitre(String strTitre) {
        this.strTitre = strTitre;
    }

    public String getStrImg() {
        return strImg;
    }

    public void setStrImg(String strImg) {
        this.strImg = strImg;}

    public String getIntVue() {
        return String.valueOf(" " + intVueArticle + " times");
    }

    public void setIntVueArticle(int intVueArticle) {
        this.intVueArticle = intVueArticle;
    }

    public boolean getLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getIntLike() {
        return String.valueOf(" " + intLike + " likes");
    }
    public int getRealLike() {
        return intLike;
    }
    public void setIntLike(int intLike) {
        this.intLike = intLike;
    }
}
