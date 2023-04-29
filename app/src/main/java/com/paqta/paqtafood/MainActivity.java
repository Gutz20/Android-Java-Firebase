package com.paqta.paqtafood;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowInsetsAnimationController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paqta.paqtafood.screens.login.Login;
import com.paqta.paqtafood.screens.profileActivity.Profileactivity;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    FirebaseUser user;
    WindowInsetsAnimationController mViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView byTextView = findViewById(R.id.byTextView);
        TextView paqtaFoodTextView = findViewById(R.id.logoTextView);
        ImageView logoImgView = findViewById(R.id.logoImageView);

        byTextView.setAnimation(animacion1);
        paqtaFoodTextView.setAnimation(animacion2);
        logoImgView.setAnimation(animacion1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

                if (user != null && account != null) {
                    Intent intent = new Intent(MainActivity.this, Profileactivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logoImgView, "logoImageTrans");
                    pairs[1] = new Pair<View, String>(byTextView, "textTransition");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }

        }, 4000);


//        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();
//        if (user == null) {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        }

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });

    }
}