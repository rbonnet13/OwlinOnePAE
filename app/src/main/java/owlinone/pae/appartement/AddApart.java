package owlinone.pae.appartement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import owlinone.pae.Manifest;
import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.Email;
import owlinone.pae.configuration.HideKeyboard;
import owlinone.pae.configuration.HttpHandler;
import owlinone.pae.main.MainActivity;
import owlinone.pae.main.SplashScreen;


/**
 * Created by AnthonyCOPPIN on 05/12/2016.
 */

public class AddApart extends AppCompatActivity {
    Button buttonSubmit;
    EditText nomProp, adresse_appart, ville_appart, descrip_appart, detail_appart, tel_prop, prix_appart, cp_appart,mail_prop;
    String strAdresseAppart= "", strAdresseGoogle= "",strvilleAppart= "", strDescripAppart= "", strDetailAppart= "", strTelProp= "", strPrixAppart= "", finalDescrip="";
    String sTaille="", sDispo="", sMonsieur="", sFinalNom="", strCPAppart= "", strMail_prop = null, strNomProp= "";
    String country = "France";
    Spinner spinnerAppart;
    Spinner spinnerDispo;
    Spinner spinnerMonsieur;
    CheckBox checkAdd = null;
    private Uri capturedImageUri;
    private Uri capturedImageUriSecond;
    double latitude = 0.0;
    double longitude = 0.0;
    Address addressName = new Address(Locale.FRANCE);
    Geocoder geocoder;
    private Bitmap bitmap;
    private Bitmap bitmapSecond;
    private ImageView imagePhoto;
    private ImageView imagePhotoSecond;
    private int request_code = 1;
    private int request_codeSecond = 2;
    private String selectedImagePath;
    private ExifInterface exifObject;
    private Bitmap imageRotate = null;
    private Bitmap imageRotateSecondaire = null;
    private static final int REQUEST_READ_PERMISSION = 100;
    protected String enteredPhoto = null;
    protected String enteredPhotoSecondaire = null;
    Boolean photoClick = false;
    Boolean photoClickSecond = false;

    List<Address> addresses = new List<Address>() {
        @Override
        public int size() {
            return 0;
        }
        @Override
        public boolean isEmpty() {
            return false;
        }
        @Override
        public boolean contains(Object o) {
            return false;
        }
        @NonNull
        @Override
        public Iterator<Address> iterator() {
            return null;
        }
        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }
        @NonNull
        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }
        @Override
        public boolean add(Address address) {
            return false;
        }
        @Override
        public boolean remove(Object o) {
            return false;
        }
        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }
        @Override
        public boolean addAll(Collection<? extends Address> collection) {
            return false;
        }
        @Override
        public boolean addAll(int i, Collection<? extends Address> collection) {
            return false;
        }
        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }
        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }
        @Override
        public void clear() {
        }
        @Override
        public Address get(int i) {
            return null;
        }
        @Override
        public Address set(int i, Address address) {
            return null;
        }
        @Override
        public void add(int i, Address address) {
        }
        @Override
        public Address remove(int i) {
            return null;
        }
        @Override
        public int indexOf(Object o) {
            return 0;
        }
        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }
        @Override
        public ListIterator<Address> listIterator() {
            return null;
        }
        @NonNull
        @Override
        public ListIterator<Address> listIterator(int i) {
            return null;
        }
        @NonNull
        @Override
        public List<Address> subList(int i, int i1) {
            return null;
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_appart);
        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.setupUI(findViewById(R.id.layout_add_appart));
        photoClick = false;
        photoClickSecond = false;

        //Initialisation du Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //Affichage de la flèche de retour-----------------------------------
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Appartement.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        final Context context = getApplicationContext();
        final CharSequence text = "Veuillez remplir les champs obligatoires";
        final int duration = Toast.LENGTH_LONG;

        //Identification de lEditText----------------------------------------
        nomProp = (EditText) findViewById(R.id.nom_prop_add);
        adresse_appart = (EditText) findViewById(R.id.adresse_appart_add);
        ville_appart = (EditText) findViewById(R.id.ville_appart_add);
        descrip_appart = (EditText) findViewById(R.id.descrip_appart_add);
        detail_appart = (EditText) findViewById(R.id.detail_appart_add);
        tel_prop = (EditText) findViewById(R.id.tel_prop_add);
        prix_appart = (EditText) findViewById(R.id.prix_appart_add);
        cp_appart = (EditText) findViewById(R.id.CP_appart_add);
        mail_prop = (EditText) findViewById(R.id.mail_prop_add);
        checkAdd = (CheckBox) findViewById(R.id.checkboxAddAppart);

        //------------------SPINNER TAILLE APPART----------------------
        spinnerAppart = (Spinner) findViewById(R.id.spinnerAppart);
        List<String> list = new ArrayList<String>();
        list.add("Studio");
        list.add("T1");
        list.add("T1 bis");
        list.add("F1");
        list.add("T2");
        list.add("T2 bis");
        list.add("F2");
        list.add("T3");
        list.add("T4");
        list.add("T5");
        list.add("T6");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinnerAppart.setAdapter(dataAdapter);

        //------------------SPINNER DISPONIBILITE----------------------
        spinnerDispo = (Spinner) findViewById(R.id.spinnerDispo);
        List<String> listDispo = new ArrayList<String>();
        listDispo.add("Disponible");
        listDispo.add("Non disponible");
        ArrayAdapter<String> dataAdapterDispo = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,listDispo);
        dataAdapterDispo.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinnerDispo.setAdapter(dataAdapterDispo);

        //-------------------SPINNER MONSIEUR---------------------------
        spinnerMonsieur = (Spinner) findViewById(R.id.spinnerMonsieur);
        List<String> listMonsieur = new ArrayList<String>();
        listMonsieur.add("M.");
        listMonsieur.add("MME");
        listMonsieur.add("MLLE");
        ArrayAdapter<String> dataAdapterMonsieur = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,listMonsieur);
        dataAdapterMonsieur.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinnerMonsieur.setAdapter(dataAdapterMonsieur);

        //-------------------PHOTO 1st---------------------------
        imagePhoto = (ImageView) findViewById(R.id.photo_appartfirst);
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoClick = true;
                photoClickSecond = false;

                Intent i;
                //verificacion de la version de plataforma
                if (Build.VERSION.SDK_INT < 19) {
                    //android 4.3  y anteriores
                    i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);

                } else {
                    //android 4.4 y superior
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.setAction(Intent.ACTION_PICK);
                }
                i.setType("image/*");
                startActivityForResult(i, request_code);
            }
        });

        //-------------------PHOTO 2st---------------------------
        imagePhotoSecond = (ImageView) findViewById(R.id.photo_appartsecond);
        imagePhotoSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoClickSecond = true;
                photoClick = false;

                Intent i;
                //verificacion de la version de plataforma
                if (Build.VERSION.SDK_INT < 19) {
                    //android 4.3  y anteriores
                    i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);

                } else {
                    //android 4.4 y superior
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.setAction(Intent.ACTION_PICK);
                }
                i.setType("image/*");
                startActivityForResult(i, request_codeSecond);
            }
        });

        //Bouton pour ajouter l'appartement-------------------------------
        buttonSubmit = (Button) findViewById(R.id.buttonAddAppart);
        buttonSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Nom du propriétaire------------------------------------
                sMonsieur        = String.valueOf(spinnerMonsieur.getSelectedItem());
                strNomProp       = nomProp.getText().toString();
                strNomProp       = strNomProp.toUpperCase();
                sFinalNom        = sMonsieur + " " + strNomProp;
                sFinalNom        = sFinalNom.replace("'","''");
                //Adresse de l'appartement-------------------------------
                strAdresseAppart = adresse_appart.getText().toString();
                strAdresseAppart = strAdresseAppart.replace("'","''");
                Log.e("nom Ville:", strAdresseAppart);
                //Ville de l'appartement---------------------------------
                strvilleAppart   = ville_appart.getText().toString();
                strAdresseGoogle = strvilleAppart;
                strvilleAppart   = strvilleAppart.replace("'","''");
                strvilleAppart   = strvilleAppart.toUpperCase();
                Log.e("nom Ville:", strvilleAppart);
                //Description de l'appartement---------------------------
                sTaille          = String.valueOf(spinnerAppart.getSelectedItem());
                strDescripAppart = descrip_appart.getText().toString();
                finalDescrip     = sTaille + " " + strDescripAppart + " m²";
                finalDescrip     = finalDescrip.replace("'","''");
                //Disponibilité------------------------------------------
                sDispo           = String.valueOf(spinnerDispo.getSelectedItem());
                //Détail de l'appartement--------------------------------
                strDetailAppart  = detail_appart.getText().toString();
                strDetailAppart  =strDetailAppart.replace("'","''");
                //Téléphone, Prix, CP, Mail----------------------------------------------
                strTelProp       = tel_prop.getText().toString();
                strPrixAppart    = prix_appart.getText().toString();
                strCPAppart      = cp_appart.getText().toString();
                String strFinalAdresse = strAdresseAppart + "," + strAdresseGoogle + " "+ strCPAppart
                        + ", "+ country;
                strMail_prop     = mail_prop.getText().toString().trim();

                //Si l'image principale et si l'image secondaire est null
                if(imageRotate != null) {
                    enteredPhoto = convertirImgString(imageRotate);
                }
                if(imageRotateSecondaire != null){
                    enteredPhotoSecondaire = convertirImgString(imageRotateSecondaire);
                }

                //Récupération de la longitude et de la latitude de l'addresse finale
                geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    for (int i=0; i<10;i++)
                    {
                        addresses = geocoder.getFromLocationName(strFinalAdresse, 1);
                        if(addresses != null && addresses.size() > 0){
                            addressName = addresses.get(0);
                        }

                        if(addressName.getLongitude() != 0.0)
                        {
                            longitude = addressName.getLongitude();
                            latitude = addressName.getLatitude();
                            i=10;
                        }
                        i++;
                    }
                    if(addressName.getLongitude() == 0.0){
                        Toast.makeText(getApplicationContext(),
                                "Erreur de localisation veuillez vous déconnecter",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                //Si téléphone mauvais nombre de chiffre ou avec le +--------------------------------------
                boolean dixNumero = false;
                boolean douzeNumero = false;
                if(strTelProp.trim().length()== 10) dixNumero = true;
                if(strTelProp.trim().length()== 12) douzeNumero = true;

                //Si Nom propriétaire est nul------------------------------------------------
                if(strNomProp.length() == 0) {
                    nomProp.setError("Veuillez saisir votre nom");
                    Toast.makeText(context, text, duration).show();
                }
                else if ((strMail_prop.trim().length() > 0) && (Email.isEmailValid(strMail_prop) == false))
                {
                    mail_prop.setError("Veuillez saisir une addresse mail valide");
                    Toast.makeText(context, text, duration).show();
                }

                //Si téléphone propriétaire est nul------------------------------------------------
                else if(!dixNumero && !douzeNumero) {
                    tel_prop.setError("Veuillez saisir un numéro correcte");
                    Toast.makeText(context, text, duration).show();
                }
                //Si photo principale est nulle
                else if(enteredPhoto == null || "null".equals(enteredPhoto)) {
                    Toast.makeText(context, "Veuillez enregistrer une photo principale", duration).show();
                }

                //Si adresse appartement est nul------------------------------------------------
                else if(strAdresseAppart.trim().length() == 0) {
                    adresse_appart.setError("Veuillez saisir une adresse");
                    Toast.makeText(context, text, duration).show();
                }

                //Si code postal est nul------------------------------------------------
                else if(strCPAppart.trim().length() != 5) {
                    cp_appart.setError("Veuillez saisir un code postal");
                    Toast.makeText(context, text, duration).show();
                }

                //Si ville appartement est nul------------------------------------------------
                else if(strvilleAppart.trim().length() == 0 || strvilleAppart.matches("^[\\w\\.-]")) {
                    ville_appart.setError("Veuillez vérifier votre num de ville");
                    Toast.makeText(context, text, duration).show();
                }

                //Si taille appartement est nul------------------------------------------------
                else if(strDescripAppart.trim().length() == 0 || sTaille.length() == 0) {
                    descrip_appart.setError("Veuillez saisir la superficie");
                    Toast.makeText(context, text, duration).show();
                }

                //Si prix appartement est nul--------------------------------------------------------
                else if(strPrixAppart.trim().length()==0 || strPrixAppart.length() > 4 ) {
                    prix_appart.setError("Veuillez saisir un prix à 4 chiffres maximum");
                    Toast.makeText(context, text, duration).show();
                }

                //Si prix appartement est nul--------------------------------------------------------
                else if(strDescripAppart.trim().length()==0 || strDescripAppart.length() > 3 ) {
                    descrip_appart.setError("Merci de vérifier la superficie de votre appartement");
                    Toast.makeText(context, text, duration).show();
                }
                //S'il n'a pas coché l'accord------------------------------------------------
                else if (!checkAdd.isChecked())
                {
                    checkAdd.setError("Veuillez confirmer vos informations");
                    Toast.makeText(context, text, duration).show();
                }
                else{
                    //On execute la requete et on passe à la page SplashScreen
                    new sendAppart().execute();
                    Intent intentAppart = new Intent(context, SplashScreen.class);
                    intentAppart.putExtra("activity","first");
                    startActivity(intentAppart);
                }

            }
        });
    }
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    // Convertir image en string
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String convertirImgString(Bitmap imageRotate) {
        String imagenString;
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        if(imageRotate!=null){
            Bitmap reSizeBitmap = getResizedBitmap(imageRotate, 500);
            reSizeBitmap.compress(Bitmap.CompressFormat.JPEG,30,array);
            byte[] imagenByte=array.toByteArray();
            imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        }else{
            imagenString = "no image"; //se enviara este string en caso de no haber imagen
        }

        return imagenString;
    }
    //Récupération du chemin de la photo
    private String getRealPathFromURIPath(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //Vérification et rotation de la photo si besoin
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == request_code ) {
            Log.e("photoClick:", String.valueOf(photoClick));
            Log.e("photoClickSecond:", String.valueOf(photoClickSecond));

            if (photoClick == true) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddApart.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
                    } else {
                        capturedImageUri = data.getData();
                        selectedImagePath = getRealPathFromURIPath(getApplicationContext(), capturedImageUri);
                        imagePhoto.setImageURI(data.getData());
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        try {
                            exifObject = new ExifInterface(selectedImagePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        imageRotate = rotateBitmap(bitmap, orientation);
                        imagePhoto.setImageBitmap(imageRotate);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(resultCode == RESULT_OK && requestCode == request_codeSecond ) {

            if (photoClickSecond == true) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddApart.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
                    } else {
                        capturedImageUriSecond = data.getData();
                        selectedImagePath = getRealPathFromURIPath(getApplicationContext(), capturedImageUriSecond);
                        imagePhotoSecond.setImageURI(data.getData());
                        bitmapSecond = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        try {
                            exifObject = new ExifInterface(selectedImagePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        imageRotateSecondaire = rotateBitmap(bitmapSecond, orientation);
                        imagePhotoSecond.setImageBitmap(imageRotateSecondaire);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class sendAppart extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddApart.this);
            pd.setMessage("Chargement");
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
        }
        Exception exception;
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                HttpHandler sh = new HttpHandler();
                HashMap<String, String> parameters = new HashMap<>();
                String url = AddressUrl.strAddAppart;
                parameters.put("NOM_PROP", sFinalNom);
                parameters.put("ADRESSE_APPART", strAdresseAppart);
                parameters.put("VILLE_APPART", strvilleAppart);
                parameters.put("DESCRIP_APPART", finalDescrip);
                parameters.put("DETAIL_APPART", strDetailAppart);
                parameters.put("TELEPHONE_PROP", strTelProp);
                parameters.put("DISPO_APPART", sDispo);
                parameters.put("PRIX_APPART", strPrixAppart);
                parameters.put("CP_APPART", strCPAppart);
                parameters.put("LONGITUDE_APPART", String.valueOf(longitude));
                parameters.put("LATITUDE_APPART", String.valueOf(latitude));
                parameters.put("ADRESSE_MAIL", strMail_prop);
                parameters.put("IMAGE_PRINCIPALE", enteredPhoto);
                if(enteredPhotoSecondaire == null){
                    enteredPhotoSecondaire = "NULL";
                }
                parameters.put("IMAGE_SECONDAIRE", enteredPhotoSecondaire);
                sh.performPostCall(url, parameters);
                return null;
            } catch (Exception e)
            {
                this.exception = e;
                return null;
            }
        }
    }

    // Fonction appelée quand appuie sur la touche retour
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Appartement.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}