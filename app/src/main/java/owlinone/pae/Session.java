package owlinone.pae;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by rudyb on 12/12/2017.
 */

public class Session {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "AndroidExamplePref";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL= "email";
    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PRENOM = "prenom";
    public static final String KEY_NOM = "nom";
    public static final String KEY_VILLE = "ville";
    public static final String KEY_ADRESSE = "adresse";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_CP = "cp";
    public static final String KEY_PHOTO = "photo";


    // Constructor
    public Session(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String name,String prenom,String nom,String ville,String adresse,String latitude, String longitude, String cp,String email, String password, String photo){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PRENOM, prenom);
        editor.putString(KEY_NOM, nom);
        editor.putString(KEY_VILLE, ville);
        editor.putString(KEY_ADRESSE, adresse);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);

        editor.putString(KEY_CP, cp);

        // Storing email in pref
        editor.putString(KEY_PASSWORD, password);
        // Storing email in pref
        editor.putString(KEY_PHOTO, photo);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainLogin.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }



    /**
     * Get stored session dataF
     * */
    public HashMap<String, String> getUserDetails(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_PRENOM, pref.getString(KEY_PRENOM, null));
        user.put(KEY_NOM, pref.getString(KEY_NOM, null));
        user.put(KEY_VILLE, pref.getString(KEY_VILLE, null));
        user.put(KEY_ADRESSE, pref.getString(KEY_ADRESSE, null));
        user.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, null));
        user.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, null));
        user.put(KEY_CP, pref.getString(KEY_CP, null));

        // user photo id
        user.put(KEY_PHOTO, pref.getString(KEY_PHOTO, "sans image"));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, MainActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
