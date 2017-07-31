package com.wordpress.nguyenvannamdev.trochoiamnhac.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.wordpress.nguyenvannamdev.trochoiamnhac.data.Question;
import com.wordpress.nguyenvannamdev.trochoiamnhac.data.User;
import com.wordpress.nguyenvannamdev.trochoiamnhac.R;
import com.wordpress.nguyenvannamdev.trochoiamnhac.data.Database;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlayPage extends AppCompatActivity {

    private ImageView img_music;
    private Animation anim_rotate;
    Animation anim_fade;
    private DatabaseReference mData;
    private TextView txt_question;
    private String urlPath = "gs://trochoiamnhac-b9165.appspot.com/audio/";
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaChoose;
    private MediaPlayer mediaGameOver;
    private List<Integer> oldQuestionIndex = new LinkedList<>();
    private List<Integer> oldChoiceButton = new LinkedList<>();
    private List<Integer> oldHint = new LinkedList<>();
    private int currentQuestionIndex;
    private Random random = new Random();
    private String answerChoose = null;
    private String reAnswer = null;
    private Button[] btn_choice;
    private String[] lineAnswer;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutAnswer;
    private Button[] btn_answer;
    private byte countAnswer = 0;
    private int countHint;
    private byte countChoose;
    private byte countQuestion = 0;
    private int countScore = 0;
    private Button btn_idea;
    private Random r = new Random();
    private String user_name;
    private TextView txt_score;
    private Database myDatabase;
    private int high_score;
    private User user;
    private ObjectAnimator animator;
    private ProgressBar progressBar;
    int question_length;
    private long Remimingtime = 0;
    private String typePlay;
    private boolean isPause = false;
    private boolean isCancel = false;
    private Uri uri;
    private Toast pressBackToast;
    private long mLastBackPress;
    private static final long mBackPressThreshold = 3500;
    private CountDownTimer timer;
    private byte countFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_play_page);

        View view = findViewById(R.id.activity_play_page);
        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int randomColor = androidColors[new Random().nextInt(androidColors.length)];
        view.setBackgroundColor(randomColor);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_score = (TextView) findViewById(R.id.textScore);
        btn_idea = (Button) findViewById(R.id.btn_idea);
        txt_question = (TextView) findViewById(R.id.txt_question);
        img_music = (ImageView) findViewById(R.id.musicPlaying);
        anim_rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        anim_fade = AnimationUtils.loadAnimation(this, R.anim.anim_fade);
        animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 2100);
        animator.setDuration(21000);
        animator.setInterpolator(new DecelerateInterpolator());
        myDatabase = new Database(this);
        storage = FirebaseStorage.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        //get user from Main
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("myUser");
        user_name = bundle.getString("user_name");
        high_score = bundle.getInt("high_score");
        countHint = myDatabase.getUserByName(user_name).getHint();
        typePlay = bundle.getString("type");
        Log.d("type", typePlay);
        question_length = bundle.getInt("length");
        uri = Uri.parse(bundle.getString("uriUser"));
        btn_idea.setText(String.valueOf(countHint));
        user = new User(user_name, countHint, high_score);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(R.style.AppTheme);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        if (typePlay.equals("VietNam")) {
            urlPath = urlPath + "Vietnam/";
        } else {
            urlPath = urlPath + "US-UK/";
        }
        //Set Back Click
        pressBackToast = Toast.makeText(getApplicationContext(), "press_back_again_to_exit",
                Toast.LENGTH_SHORT);

        //get data
        InitializeQuizData(randomQuestion());

    }

    private void InitializeQuizData(final int current) {
        isCancel = true;
        anim_fade.reset();
        txt_score.setText(String.valueOf(countScore));
        txt_score.startAnimation(anim_fade);
        btn_idea.startAnimation(anim_fade);
        mData.child("Quiz").child(String.valueOf(typePlay)).child(String.valueOf(current)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question question = new Question();
                question = dataSnapshot.getValue(Question.class);
                storage.getReferenceFromUrl(urlPath + question.getUrlPath() + ".mp3")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                        isCancel = false;
                        isPause = false;
                        mediaPlayer.setLooping(false);
                        mediaPlayer.start();
                        animator.start();
                        img_music.startAnimation(anim_rotate);
                        if (mediaPlayer.isPlaying()) {
                            timer = new CountDownTimer(21000, 1000) {
                                @Override
                                public void onTick(long l) {
                                    if (isPause || isCancel) {
                                        cancel();
                                    } else {
                                        Remimingtime = l;
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    Remimingtime = 0;
                                    goPageResult("Lose");
                                }
                            }.start();
                        }
                    }

                });
                txt_question.setText(question.getQuestion());
                CustomButton(question);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void CustomButton(Question question) {
        if (question != null) {
            lineAnswer = question.getAnswer().split("\\s");
            btn_choice = new Button[lineAnswer.length];
            btn_answer = new Button[lineAnswer.length];

            linearLayoutAnswer = (LinearLayout) findViewById(R.id.linearLayout_answer);
            linearLayout = (LinearLayout) findViewById(R.id.linearLayout_btn_answer);
            linearLayoutAnswer.setWeightSum(lineAnswer.length);
            linearLayout.setWeightSum(lineAnswer.length);
            randomButton();
            Log.d("lengthAnswer", String.valueOf(lineAnswer.length));
            countChoose = 0;
            countFail = 0;
            mediaChoose = MediaPlayer.create(this,R.raw.button16);
            for (int j = 0; j < lineAnswer.length; j++) {
                Log.d("lineAnswer", lineAnswer[j].toString());
                btn_Choice_Click(j);
                btn_Answer_Click(j);
                linearLayout.addView(btn_choice[j]);
                linearLayoutAnswer.addView(btn_answer[j]);
            }
        }

        btn_idea.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                if (countHint > 0) {
                    animator.pause();
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    isPause = true;
                    final int lengthMedia = mediaPlayer.getCurrentPosition();
                    final AlertDialog builder = new AlertDialog.Builder(PlayPage.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View v = inflater.inflate(R.layout.dialog_help, null);
                    Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
                    TextView hint = (TextView) v.findViewById(R.id.hint);
                    TextView char_hint = (TextView) v.findViewById(R.id.txt_charHint);
                    int i = randomHint();
                    hint.setText("Chữ cái thứ " + (i + 1) + " là chữ");
                    char_hint.setText(lineAnswer[i].toString());
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                            countDownTimer(lengthMedia);
                            animator.resume();
                        }
                    });
                    builder.setView(v);
                    countHint--;
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            countDownTimer(lengthMedia);
                            mediaPlayer.start();
                            animator.resume();
                        }
                    });
                    builder.show();
                    user.setHint(countHint);
                    user.setHigh_score(high_score);
                    myDatabase.updateData(user);
                    btn_idea.setText(String.valueOf(countHint));
                } else {
                    Toast.makeText(getApplicationContext(), "Đã hết sô lần gợi ý", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void btn_Choice_Click(final int index) {
        btn_choice[index] = new Button(this);
        btn_choice[index].setText(lineAnswer[oldChoiceButton.get(index)].toString());
        btn_choice[index].setTextSize(20);
        btn_choice[index].setBackground(getResources().getDrawable(R.drawable.boder_button));
        btn_choice[index].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) btn_choice[index].getLayoutParams();
        params.setMargins(100, 0, 100, 10);
        btn_choice[index].setLayoutParams(params);
        btn_choice[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    if (btn_choice[index].getText().toString() != null) {
                        answerChoose = String.valueOf(btn_choice[index].getText());
                        mediaChoose.start();
                        for (int k = 0; k < lineAnswer.length; k++) {
                            if (answerChoose != null) {
                                if (btn_answer[k].getText() == "") {
                                    btn_answer[k].setText(answerChoose);
                                    countChoose++;
                                    Log.d("countChoose", String.valueOf(countChoose));
                                    checkAnswer(k);
                                    break;
                                }
                            }
                        }
                        if (countAnswer < lineAnswer.length) {
                            btn_choice[index].setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });


    }

    private int randomQuestion() {
        do {
            currentQuestionIndex = random.nextInt(question_length);
        } while (oldQuestionIndex.contains(currentQuestionIndex));
        oldQuestionIndex.add(currentQuestionIndex);
        countQuestion++;
        return currentQuestionIndex;
    }

    private int randomHint() {
        int i;
        do {
            i = random.nextInt(lineAnswer.length);
        } while (oldHint.contains(i));
        oldQuestionIndex.add(i);
        return i;
    }


    private List<Integer> randomButton() {
        int i;
        oldChoiceButton.removeAll(oldChoiceButton);
        for (int j = 0; j < lineAnswer.length; ) {
            do {
                i = r.nextInt(lineAnswer.length);
            } while (oldChoiceButton.contains(i));
            oldChoiceButton.add(i);
            j++;
        }
        return oldChoiceButton;
    }

    private void btn_Answer_Click(final int index) {
        btn_answer[index] = new Button(this);
        btn_answer[index].setBackground(getResources().getDrawable(R.drawable.boder_button_answer));
        btn_answer[index].setTextColor(Color.BLACK);
        btn_answer[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_answer[index].getText().toString() != null) {
                    countChoose--;
                    mediaChoose.start();
                    reAnswer = btn_answer[index].getText().toString();
                    for (int k = 0; k < lineAnswer.length; k++) {
                        if (reAnswer != null) {
                            if (btn_choice[k].getText() == reAnswer) {
                                btn_choice[k].setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    if (reAnswer == lineAnswer[index].toString()) {
                        countAnswer--;
                    }
                    btn_answer[index].setText("");
                }
            }

        });

        btn_answer[index].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, linearLayoutAnswer.getWeightSum() / btn_choice.length));
        LinearLayout.MarginLayoutParams params_answer = (LinearLayout.MarginLayoutParams) btn_answer[index].getLayoutParams();
        params_answer.setMargins(10, 0, 10, 0);
        btn_answer[index].setLayoutParams(params_answer);
    }

    private void checkAnswer(int i) {
        Log.d("iiii", String.valueOf(i));
        if (btn_answer[i].getText().equals(lineAnswer[i].toString())) {
            if (i < lineAnswer.length) {
                countAnswer++;
                Log.d("countAnswer", String.valueOf(countAnswer));
                if (countAnswer == lineAnswer.length) {
                    isCancel = true;
                    timer.cancel();
                    countAnswer = 0;
                    linearLayout.removeAllViewsInLayout();
                    linearLayoutAnswer.removeAllViewsInLayout();
                    mediaPlayer.stop();
                    if (countQuestion < question_length) {
                        countScore++;
                        if (countScore > high_score) {
                            high_score = countScore;
                            user.setHigh_score(countScore);
                            myDatabase.updateData(user);
                        }
                        if (Remimingtime > 0) {
                            mediaPlayer.release();
                            InitializeQuizData(randomQuestion());
                        }

                    } else {
                        isCancel = true;
                        goPageResult("Win");
                    }
                }
            }
        }
        else {
            countFail++;
            if (countChoose == lineAnswer.length && countFail > 0) {
                Remimingtime = 0;
                goPageResult("Lose");
            }
        }
    }

    private void countDownTimer(int lengthMedia) {
        isCancel = false;
        isPause = false;
        new CountDownTimer(Remimingtime, 1000) {
            @Override
            public void onTick(long l) {
                if (isPause || isCancel) {
                    cancel();
                } else {
                    Remimingtime = l;
                }
            }

            @Override
            public void onFinish() {
                goPageResult("Lose");
            }
        }.start();
        mediaPlayer.start();
        mediaPlayer.seekTo(lengthMedia);
    }

    private void goPageResult(String s) {
        if (Remimingtime <= 0) {
            isPause = true;
            isCancel = true;
            mediaPlayer.stop();
            final AlertDialog builder = new AlertDialog.Builder(PlayPage.this).create();
            final LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.layout_end, null);
            ImageButton btn_share = (ImageButton) v.findViewById(R.id.btn_share);
            ImageButton btn_replay = (ImageButton) v.findViewById(R.id.btn_replay);
            ImageButton btn_home = (ImageButton) v.findViewById(R.id.btn_home);
            ImageView imageView = (ImageView) v.findViewById(R.id.img_avatar_result);
            TextView txt_highScore = (TextView) v.findViewById(R.id.txt_highScore);
            TextView txt_score = (TextView) v.findViewById(R.id.txt_scoreCurrent);
            ImageView img_result = (ImageView) v.findViewById(R.id.imageViewResult);
            mediaGameOver = MediaPlayer.create(PlayPage.this, R.raw.button10);
            mediaGameOver.start();
            if (s.equals("Win")) {
                img_result.setImageResource(R.drawable.win);
                txt_score.setText(String.valueOf(countScore));
            } else if (s.equals("Lose")) {
                img_result.setImageResource(R.drawable.lose);
                txt_score.setText(String.valueOf(countScore));
            }
            txt_highScore.setText(String.valueOf(high_score));
            Picasso.with(getApplicationContext()).load(uri).into(imageView);
            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBody = user_name + " vừa ghi được số điểm " + String.valueOf(countScore)
                            + " trong Trò Chơi Âm Nhạc. Bạn có thể tham gia tại : \n"
                            + "https://play.google.com/store/apps/details?id=com.wordpress.nguyenvannamdev.trochoiamnhac";
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(intent, "Chia sẻ điểm"));
                }
            });
            btn_replay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(getIntent());
                }
            });
            btn_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(PlayPage.this, MainActivity.class));
                }
            });

            builder.setView(v);
            builder.setCanceledOnTouchOutside(false);
            builder.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
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


}
