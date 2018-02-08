package owlinone.pae;

/**
 * Created by AnthonyCOPPIN on 24/10/2017.
 */

public class AddressUrl {

    private static String strUrl = "192.168.0.23";
    private static String strPort = "80";
    private static String strDossierPhp = "test4";

    public static final String strChemin               = "http://" + strUrl;
    public static final String strAddAppart            = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/addAppart.php";
    public static final String strModifierDispo        = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/modifierDispo.php";
    public static final String strArticle              = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/article.php";
    public static final String strConnexionAppartement = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/connexionappartement.php";
    public static final String strConnexionEvent       = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/connexionevent.php";
    public static final String strNumberLikeArticle    = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/number_like_article.php";
    public static final String strNumberUnlikeArticle  = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/number_unlike_article.php";
    public static final String strNumberViewArticle    = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/number_view_article.php";
    public static final String strTriAppartDispo       = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/triAppartementDispo.php";
    public static final String strTriAppartPrix        = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/triAppartementPrix.php";
    public static final String strTriAppartPrixDispo   = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/triAppartementPrixDispo.php";
    public static final String strTriIndex             = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/index.php";
    public static final String strTriIndexCompte       = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/indexCompte.php";
    public static final String strTriIndexGPS          = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/indexGPS.php";
    public static final String strPhoto                = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp + "/";






    public String getStrTriAppartPrixDispo() {
        return strTriAppartPrixDispo;
    }

    public String getStrTriAppartPrix() {
        return strTriAppartPrix;
    }

    public String getStrTriAppartDispo() {
        return strTriAppartDispo;
    }

    public String getStrNumberViewArticle() {
        return strNumberViewArticle;
    }

    public String getStrNumberLikeArticle() {
        return strNumberLikeArticle;
    }

    public String getStrNumberUnlikeArticle() {
        return strNumberUnlikeArticle;
    }

    public String getStrConnexionEvent() {
        return strConnexionEvent;
    }

    public String getStrConnexionAppartement() {
        return strConnexionAppartement;
    }

    public String getStrArticle() {
        return strArticle;
    }

    public String getStrModifierDispo() {
        return strModifierDispo;
    }

    public String getStrAddAppart() {
        return strAddAppart;
    }

    public String getStrTriIndex() {
        return strTriIndex;
    }

    public String getStrTriIndexCompte() {
        return strTriIndexCompte;
    }

    public String strTriIndexGPS() {
        return strTriIndexGPS;
    }

    public String getStrPhoto() {
       return strPhoto;
   }

    public String getStrChemin() {
        return strChemin;
    }



}
