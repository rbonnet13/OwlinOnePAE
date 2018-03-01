package owlinone.pae.configuration;

/**
 * Created by AnthonyCOPPIN on 24/10/2017.
 */

public class AddressUrl {

    private static String strUrl = "192.168.137.36";
    private static String strPort = "80";
    private static String strDossierPhp = "test4";

    public static final String strEmailRecup           = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/recup_mail.php";
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
    public static final String strNotifHome            = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/notificationHome.php";
    public static final String strNotifSchool          = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/notificationSchool.php";
    public static final String strTriIndexCompte       = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/indexCompte.php";
    public static final String strTriIndexGPS          = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/indexGPS.php";
    public static final String strIndexBug               = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/indexBug.php";
    public static final String strNotifUser            = "http://" + strUrl + ":"+ strPort +"/"+ strDossierPhp +"/indexNotification.php";
}
