package com.paqta.paqtafood.screens.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.paqta.paqtafood.R;
import com.paqta.paqtafood.navigation.DefaultNavigationApp;
import com.paqta.paqtafood.screens.forgotPassword.ForgotPassword;
import com.paqta.paqtafood.screens.signup.Signup;
import com.paqta.paqtafood.utils.ChangeColorBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    ImageView loginImage;
    MaterialButton btnLogin;

    TextView textViewRegister, tvwLogin, tvwOlvidasteContra;
    ChangeColorBar changeColorBar = new ChangeColorBar();

    //    FIREBASE
    SignInButton mSignInButtonGoogle;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore mFirestore;
    private static final int RC_SIGN_IN = 9001;

    /**
     * Al iniciarse el layout verificara si existe un usario de darse el caso
     * sera redirigido a la ventana de perfil
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Abrira la ventana de login por google que mostrara las cuenta del dispositivo
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // COMPONENTES
        loginImage = findViewById(R.id.logoImageViewLogin);
        textViewRegister = findViewById(R.id.tvwRegister);
        editTextEmail = findViewById(R.id.txtLoginUser);
        editTextPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvwLogin = findViewById(R.id.tvwLogin);
        tvwOlvidasteContra = findViewById(R.id.tvwOlvidasteContra);

//        FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mSignInButtonGoogle = findViewById(R.id.btnGoogle);
        mFirestore = FirebaseFirestore.getInstance();

//        GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        tvwOlvidasteContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        mSignInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(v);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);

                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View, String>(loginImage, "logoImageTrans");
                pairs[1] = new Pair<View, String>(tvwLogin, "textTrans");
                pairs[2] = new Pair<View, String>(editTextEmail, "emailInputTextTrans");
                pairs[3] = new Pair<View, String>(editTextPassword, "passwordInputTextTrans");
                pairs[4] = new Pair<View, String>(btnLogin, "buttonSignInTrans");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        changeColorBar.window = getWindow();
        changeColorBar.cambiarColor("#151C48", "#151C48");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Autenticacion con google mediante firebase, obtiene las credenciales para luego
     * logearse con las mismas mediante Firebase Authentication
     *
     * @param idToken, token que se enviara para obtener la credencial del usuario de google
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> map = new HashMap<>();
                            assert user != null;
                            map.put("username", user.getDisplayName());
                            map.put("email", user.getEmail());

                            CollectionReference usuariosRef = mFirestore.collection("usuarios");
                            Query consulta = usuariosRef.whereEqualTo("email", user.getEmail());

                            registerUserBySocialNetwork(consulta, user, map);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        if (user != null) {
            irHome();
        }
    }

    private void irHome() {
        Intent intent = new Intent(Login.this, DefaultNavigationApp.class);
        startActivity(intent);
        finish();
    }

    /**
     * Valida los campos de entrada para verificar que todo este acorde
     * a como esta en la database. Asociado al login por correo
     *
     * @param v, Vista que se enviara para el snackbar
     */
    public void validate(View v) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Correo Invalido");
            return;
        } else {
            editTextEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Se necesitan más de 6 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            editTextPassword.setError("Al menos un numero");
            return;
        } else {
            editTextPassword.setError(null);
        }

        iniciarSesion(v, email, password);
    }

    /**
     * Inicia sesion con las credenciales del correo y la contraseña
     *
     * @param v,        Vista enviada al snackbar
     * @param email,    Email que el usuario ingresara
     * @param password, Password que el usuario ingresara
     */
    public void iniciarSesion(View v, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            irHome();
                        } else {
                            Snackbar.make(v, "Credenciales incorrectas, intente otra ves", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    /**
     * Verifica si no existe algun otro usuario con el mismo correo, para despues agregarlo a Firestore
     * @param consulta
     * @param user
     * @param map
     */
    public void registerUserBySocialNetwork(Query consulta, FirebaseUser user, Map map) {
        consulta.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    if (documentSnapshot.get("email").toString().matches(user.getEmail())) {
                        updateUI(user);
                        Toast.makeText(Login.this, "Logeando", Toast.LENGTH_SHORT).show();
                    } else {
                        mFirestore.collection("usuarios").add(map)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        updateUI(user);
                                        Toast.makeText(Login.this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }
        });
    }

}