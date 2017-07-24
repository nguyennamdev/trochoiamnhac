package com.wordpress.nguyenvannamdev.trochoiamnhac;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wordpress.nguyenvannamdev.trochoiamnhac.Data.User;
import com.wordpress.nguyenvannamdev.trochoiamnhac.dal.Database;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btn_USUK;
    private Button btn_VN;
    private Button btn_play;
    private TextView txt_name;
    private TextView txt_score;
    private ImageView image_avatar;
    private AdView adBanner;
    private AdRequest adRequest;
    private Button btn_addIdea;
    private Database myDatabase;
    private int hint = 1;
    private int score = 0;
    private User user1;
    private ImageButton btn_option;
    private int question_length_USUK;
    private int question_length_VietNam;
    int length;
    private DatabaseReference mData;
    private ProgressDialog progressDialog;
    private MediaPlayer mediaPlayer;
    private Uri uriUser;
    private Toast pressBackToast;
    private long mLastBackPress;
    private static final long mBackPressThreshold = 3500;
    private CheckInternet checkInternet;
    private InterstitialAd mInterstitialAd;
    FirebaseUser user;
    private Animation animationZoom;
    private Animation animationClockwise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_main);
        image_avatar = (ImageView) findViewById(R.id.Avatar);
        txt_name = (TextView) findViewById(R.id.face_name);
        btn_addIdea = (Button) findViewById(R.id.btn_addIdea);
        txt_score = (TextView) findViewById(R.id.txt_score);
        myDatabase = new Database(this);
        checkInternet = CheckInternet.getInstance(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.highscoreteminitepandaeyes);
        mediaPlayer.start();
        btn_USUK = (Button) findViewById(R.id.imageButton_US_UK);
        btn_VN = (Button) findViewById(R.id.imageButton_VN);
        btn_option = (ImageButton) findViewById(R.id.btn_option);
        mData = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        animationZoom = AnimationUtils.loadAnimation(this,R.anim.anim_zoom);


        mData.child("QuestionLength").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Integer>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Integer>>() {
                };
                HashMap<String, Integer> question_length = dataSnapshot.getValue(genericTypeIndicator);
                length = question_length_USUK = question_length.get("US-UK");
                question_length_VietNam = question_length.get("VietNam");
                Log.d("lee", String.valueOf(question_length_VietNam));
                Log.d("ll", String.valueOf(question_length_USUK));
                Toast.makeText(getApplicationContext(),"Tải xong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        MobileAds.initialize(this, "ca-app-pub-8486025614150503~8511203671");
        registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_LOST"));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8486025614150503~8511203671");
        adBanner = (AdView) findViewById(R.id.ad_view);
        adBanner.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        adRequest = new AdRequest.Builder().build();
        adBanner.loadAd(adRequest);
        mInterstitialAd.loadAd(adRequest);

        adBanner.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                user1.setUser_name(txt_name.getText() + "");
                user1.setHigh_score(Integer.parseInt(String.valueOf(txt_score.getText())));
                hint += 1;
                user1.setHint(hint);
                myDatabase.updateData(user1);
                Toast.makeText(getApplicationContext(), "Bạn đã được thêm 1 lần gợi ý", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded() {
                adBanner.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                adBanner.setVisibility(View.GONE);
                super.onAdFailedToLoad(i);
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            txt_name.setText(name);
            uriUser = user.getPhotoUrl();
            Picasso.with(this).load(uriUser).fit().into(image_avatar);
            user1 = myDatabase.getUserByName(name);
            if (myDatabase.countSize() == 0 || user1 == null) {
                addUserToDatabase(name);
            } else if (myDatabase.countSize() > 0 && myDatabase.getUserByName(name) != null) {
                if (name.equals(user1.getUser_name())) {
                    txt_score.setText(user1.getHigh_score() + "");
                    hint = user1.getHint();
                }
            }

        } else {
            goLoginScreen();
        }
        animationClockwise = AnimationUtils.loadAnimation(this,R.anim.anim_setting);
        btn_option.setAnimation(animationClockwise);
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_options, null);
                Button btn_rate = (Button) v.findViewById(R.id.btn_rate);
                Button btn_logout = (Button) v.findViewById(R.id.btn_Logout);
                builder.setView(v);
                builder.show();
                btn_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout(view);
                    }
                });
                btn_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.wordpress.nguyenvannamdev.trochoiamnhac")));
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.wordpress.nguyenvannamdev.trochoiamnhac")));
                        }
                    }
                });
            }
        });

        btn_play = (Button) findViewById(R.id.button_play);
        btn_play.setAnimation(animationZoom);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_USUK.getVisibility() == View.INVISIBLE) {
                    btn_VN.setVisibility(View.VISIBLE);
                    btn_USUK.setVisibility(View.VISIBLE);
                    btn_VN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!checkInternet.isConnected()) {
                                showDialogInternet();
                            } else {
                                if (length != 0) {
                                    goPlayPage("VietNam");
                                }
                            }
                        }
                    });
                    btn_USUK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!checkInternet.isConnected()) {
                                showDialogInternet();
                            } else {
                                if (length != 0) {
                                    goPlayPage("US-UK");
                                }
                            }
                        }
                    });
                } else {
                    btn_VN.setVisibility(View.INVISIBLE);
                    btn_USUK.setVisibility(View.INVISIBLE);
                }
            }
        });

        pressBackToast = Toast.makeText(getApplicationContext(), "press_back_again_to_exit",
                Toast.LENGTH_SHORT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_zoom);
        btn_addIdea.setAnimation(animation);
        btn_addIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog builder2 = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_getidea, null);
                final Button btn_clickAd = (Button) v.findViewById(R.id.btn_clickAdmob);
                Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);

                if (!checkInternet.isConnected()) {
                    showDialogInternet();
                } else {
                    btn_clickAd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            user1.setUser_name(txt_name.getText() + "");
                            user1.setHigh_score(Integer.parseInt(String.valueOf(txt_score.getText())));
                            hint += 1;
                            user1.setHint(hint);
                            myDatabase.updateData(user1);
                            startActivity(new Intent(getApplicationContext(), FullAdsActivity.class));
                        }
                    });
                }
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder2.dismiss();
                    }
                });
                builder2.setView(v);
                builder2.show();
            }
        });
    }

    public void goPlayPage(String s) {
        User user2 = new User();
        user2 = myDatabase.getUserByName(user.getDisplayName());
        Intent intent = new Intent(MainActivity.this, PlayPage.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_name", user2.getUser_name());
        bundle.putString("type", s);
        bundle.putString("uriUser", String.valueOf(uriUser));
        if (s.equals("VietNam")) {
            bundle.putInt("length", question_length_VietNam);
        } else if (s.equals("US-UK")) {
            bundle.putInt("length", question_length_USUK);
        }
        bundle.putInt("high_score", user2.getHigh_score());
        intent.putExtra("myUser", bundle);
        startActivity(intent);
        mediaPlayer.stop();
        finish();


    }


    private void showDialogInternet() {
        Toast.makeText(getApplicationContext(), "Kiểm tra Internet", Toast.LENGTH_LONG).show();
    }

    private void addUserToDatabase(String name) {
        User user = new User();
        user.setHigh_score(score);
        user.setHint(hint);
        user.setUser_name(name);
        Boolean addSuccess = myDatabase.insertData(user);
        if (addSuccess) {
            Toast.makeText(this, "Thêm tài khoản thành công", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Lỗi", Toast.LENGTH_LONG).show();
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginAcitivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout(View v) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - mLastBackPress) > mBackPressThreshold) {
            pressBackToast.show();
            mLastBackPress = currentTime;
        } else {
            pressBackToast.cancel();
            super.onBackPressed();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("Load ads");

            AdRequest adRequest = new AdRequest.Builder().build();
            adBanner.loadAd(adRequest);
            adBanner.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adBanner.setVisibility(View.VISIBLE);
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    adBanner.setVisibility(View.GONE);
                }
            });

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    public void onPause() {
        if (adBanner != null) {
            adBanner.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adBanner != null) {
            adBanner.resume();

        }
    }

    @Override
    public void onDestroy() {
        if (adBanner != null) {
            adBanner.destroy();
        }
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
