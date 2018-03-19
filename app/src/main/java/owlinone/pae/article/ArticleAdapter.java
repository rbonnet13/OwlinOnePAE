package owlinone.pae.article;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import owlinone.pae.R;
import owlinone.pae.configuration.AddressUrl;
import owlinone.pae.configuration.HttpHandler;

import static owlinone.pae.R.drawable.coeur_gris;
import static owlinone.pae.R.drawable.coeur_rouge;

/**
 * Created by rudybonnet on 14/12/2016.
 */

public class ArticleAdapter extends ArrayAdapter<Article>  {

    ArrayList<Article> article;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(2);
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();

    Context context;
    int resource;
    private static SharedPreferences prefs;

    public ArticleAdapter(Context context, int resource, ArrayList<Article> article) {
        super(context, resource, article);
        this.article = article;
        this.context = context;
        this.resource= resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;
        final ArticleAdapter adapter = new ArticleAdapter(context,resource,article);
        prefs = context.getSharedPreferences("MYPREFS", 0);
        final SharedPreferences.Editor editor = prefs.edit();
        //Permet de récupérer la largeur de l'écran du portable
        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;


            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resource,null,true);
            viewHolder = new ViewHolder();
            viewHolder.article = article.get(position);
            viewHolder.likeCoeurGris = (ImageButton) convertView.findViewById(R.id.coeurLikeGris);
            viewHolder.likeCoeurGris.setTag(viewHolder.article);

        viewHolder.textView =(TextView) convertView.findViewById(R.id.titre);
            viewHolder.textView2 =(TextView) convertView.findViewById(R.id.categorie);
            viewHolder.textView3 =(TextView) convertView.findViewById(R.id.dateAgo);
            viewHolder.textView4 =(TextView) convertView.findViewById(R.id.nb_article_vue);
            viewHolder.textView5 =(TextView) convertView.findViewById(R.id.nb_like_article);
            viewHolder.heartImageView = (ImageView) convertView.findViewById(R.id.heart);
            viewHolder.circleBackground = convertView.findViewById(R.id.circleBg);
            //viewHolder.likeCoeurGris.setTag(getItem(position));
            viewHolder.imageView =(ImageView) convertView.findViewById(R.id.img);
            viewHolder.linearLike =(LinearLayout) convertView.findViewById(R.id.linearLike);
            viewHolder.linearLike.setTag(viewHolder.article);



        Log.d("TAG", "Tag: " + String.valueOf(viewHolder.likeCoeurGris.getTag()));

        final Article article = getItem(position);

        //Récupère et décode les images en Base64 depuis la BDD
        String base64 = article.getStrImg().substring(article.getStrImg().indexOf(","));
        byte[] decodedBase64 = Base64.decode(base64, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedBase64, 0, decodedBase64.length);
        viewHolder.imageView.setImageBitmap(image);

            if (prefs.getBoolean(String.valueOf(article.getStrID()), false) == true) {
                article.setLike(true);
                viewHolder.likeCoeurGris.setImageResource(coeur_rouge);
                viewHolder.textView5.setTextColor(Color.rgb(216,0,39));
            } else {
                viewHolder.likeCoeurGris.setImageResource(coeur_gris);
                viewHolder.textView5.setTextColor(Color.rgb(169,169,169));
            }

        final ViewHolder finalViewHolder = viewHolder;
        final ViewHolder finalViewHolder1 = viewHolder;
        final View finalConvertView2 = convertView;
        final ViewHolder finalViewHolder4 = viewHolder;
        viewHolder.linearLike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {


                    final Article article = (Article) v.getTag();
                    if(article.getLike() == false)
                    {   finalViewHolder.circleBackground.setVisibility(View.VISIBLE);
                        finalViewHolder.heartImageView.setVisibility(View.VISIBLE);

                        finalViewHolder.circleBackground.setScaleY(0.1f);
                        finalViewHolder.circleBackground.setScaleX(0.1f);
                        finalViewHolder.circleBackground.setAlpha(1f);
                        finalViewHolder.heartImageView.setScaleY(0.1f);
                        finalViewHolder.heartImageView.setScaleX(0.1f);

                        AnimatorSet animatorSetHeart = new AnimatorSet();

                        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(finalViewHolder.circleBackground, "scaleY", 0.1f, 1f);
                        bgScaleYAnim.setDuration(200);
                        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(finalViewHolder.circleBackground, "scaleX", 0.1f, 1f);
                        bgScaleXAnim.setDuration(200);
                        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(finalViewHolder.circleBackground, "alpha", 1f, 0f);
                        bgAlphaAnim.setDuration(200);
                        bgAlphaAnim.setStartDelay(150);
                        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

                        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(finalViewHolder.heartImageView, "scaleY", 0.1f, 1f);
                        imgScaleUpYAnim.setDuration(300);
                        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
                        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(finalViewHolder.heartImageView, "scaleX", 0.1f, 1f);
                        imgScaleUpXAnim.setDuration(300);
                        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

                        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(finalViewHolder.heartImageView, "scaleY", 1f, 0f);
                        imgScaleDownYAnim.setDuration(300);
                        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
                        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(finalViewHolder.heartImageView, "scaleX", 1f, 0f);
                        imgScaleDownXAnim.setDuration(300);
                        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                        animatorSetHeart.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
                        animatorSetHeart.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);
                        animatorSetHeart.start();

                        AnimatorSet animatorSet = new AnimatorSet();

                        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(finalViewHolder.likeCoeurGris, "rotation", 0f, 360f);
                        rotationAnim.setDuration(300);
                        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(finalViewHolder.likeCoeurGris, "scaleX", 0.2f, 1f);
                        bounceAnimX.setDuration(300);
                        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(finalViewHolder.likeCoeurGris, "scaleY", 0.2f, 1f);
                        bounceAnimY.setDuration(300);
                        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                        bounceAnimY.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                finalViewHolder.likeCoeurGris.setImageResource(R.drawable.coeur_rouge);
                            }
                        });

                        animatorSet.play(rotationAnim);
                        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                        animatorSet.start();
                        article.setLike(true);
                        finalViewHolder1.textView5.setTextColor(Color.rgb(216,0,39));

                        Log.d("Like: ", String.valueOf(article.getLike()));
                        editor.putBoolean(String.valueOf(article.getStrID()), article.getLike());
                        editor.commit();
                        finalViewHolder.likeCoeurGris.setImageResource(coeur_rouge);

                        // Envoi à la base si on aime l'article
                        class sendLike extends AsyncTask<Void, Void, Void>
                        {
                            HttpHandler sh = new HttpHandler();
                            ProgressDialog pd;
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
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
                                    HashMap<String, String> parameters = new HashMap<>();
                                    String url = AddressUrl.strNumberLikeArticle;
                                    parameters.put("ID_ARTICLE", String.valueOf(article.getStrID()));

                                    sh.performPostCall(url, parameters);
                                    return null;
                                } catch (Exception e)
                                {
                                    this.exception = e;
                                    return null;
                                }
                            }
                        }
                        new sendLike().execute();
                        article.setIntLike(article.getRealLike() + 1);
                        finalConvertView2.setTag(finalViewHolder4);
                        setupItem(finalViewHolder4);
                        adapter.notifyDataSetChanged();

                    }
                    else
                    {
                        article.setLike(false);
                        finalViewHolder1.textView5.setTextColor(Color.rgb(169,169,169));
                        editor.remove(String.valueOf(article.getStrID()));
                        editor.commit();

                        finalViewHolder.likeCoeurGris.setImageResource(coeur_gris);
                        Log.d("Like: ", String.valueOf(article.getLike()));

                        class sendUnLike extends AsyncTask<Void, Void, Void>
                        {
                            HttpHandler sh = new HttpHandler();
                            ProgressDialog pd;
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
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
                                    HashMap<String, String> parameters = new HashMap<>();
                                    String url = AddressUrl.strNumberUnlikeArticle;
                                    parameters.put("ID_ARTICLE", String.valueOf(article.getStrID()));

                                    sh.performPostCall(url, parameters);
                                    return null;
                                } catch (Exception e)
                                {
                                    this.exception = e;
                                    return null;
                                }
                            }
                        }
                        new sendUnLike().execute();

                        article.setIntLike((article.getRealLike() - 1));
                        finalConvertView2.setTag(finalViewHolder4);
                        setupItem(finalViewHolder4);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        convertView.setTag(viewHolder);
        setupItem(viewHolder);
        return convertView;
    }

    private void setupItem(ViewHolder viewHolder) {
        viewHolder.textView.setText(viewHolder.article.getStrTitre());
        viewHolder.textView2.setText(viewHolder.article.getStrCategorie());
        viewHolder.textView3.setText(viewHolder.article.getStrDate());
        viewHolder.textView4.setText(viewHolder.article.getIntVue());
        viewHolder.textView5.setText(viewHolder.article.getIntLike());
    }

    static class ViewHolder {
        Article article;
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        ImageView imageView;
        ImageButton likeCoeurGris;
        TextView textView5;
        View circleBackground;
        ImageView heartImageView;
        LinearLayout linearLike;
    }

}