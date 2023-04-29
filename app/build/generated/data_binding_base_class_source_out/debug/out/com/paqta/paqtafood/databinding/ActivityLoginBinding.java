// Generated by view binder compiler. Do not edit!
package com.paqta.paqtafood.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.paqta.paqtafood.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final MaterialButton btnLogin;

  @NonNull
  public final ImageView decoratorBottom;

  @NonNull
  public final ImageView decoratorTop;

  @NonNull
  public final SignInButton loginGoogle;

  @NonNull
  public final ImageView logoImageViewLogin;

  @NonNull
  public final MaterialCardView materialCardView;

  @NonNull
  public final TextView textView2;

  @NonNull
  public final TextView tvwLogin;

  @NonNull
  public final TextView tvwRegister;

  @NonNull
  public final TextInputLayout txtLayoutPassword;

  @NonNull
  public final TextInputLayout txtLayoutUser;

  @NonNull
  public final TextInputEditText txtLoginPassword;

  @NonNull
  public final TextInputEditText txtLoginUser;

  private ActivityLoginBinding(@NonNull ConstraintLayout rootView, @NonNull MaterialButton btnLogin,
      @NonNull ImageView decoratorBottom, @NonNull ImageView decoratorTop,
      @NonNull SignInButton loginGoogle, @NonNull ImageView logoImageViewLogin,
      @NonNull MaterialCardView materialCardView, @NonNull TextView textView2,
      @NonNull TextView tvwLogin, @NonNull TextView tvwRegister,
      @NonNull TextInputLayout txtLayoutPassword, @NonNull TextInputLayout txtLayoutUser,
      @NonNull TextInputEditText txtLoginPassword, @NonNull TextInputEditText txtLoginUser) {
    this.rootView = rootView;
    this.btnLogin = btnLogin;
    this.decoratorBottom = decoratorBottom;
    this.decoratorTop = decoratorTop;
    this.loginGoogle = loginGoogle;
    this.logoImageViewLogin = logoImageViewLogin;
    this.materialCardView = materialCardView;
    this.textView2 = textView2;
    this.tvwLogin = tvwLogin;
    this.tvwRegister = tvwRegister;
    this.txtLayoutPassword = txtLayoutPassword;
    this.txtLayoutUser = txtLayoutUser;
    this.txtLoginPassword = txtLoginPassword;
    this.txtLoginUser = txtLoginUser;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnLogin;
      MaterialButton btnLogin = ViewBindings.findChildViewById(rootView, id);
      if (btnLogin == null) {
        break missingId;
      }

      id = R.id.decorator_bottom;
      ImageView decoratorBottom = ViewBindings.findChildViewById(rootView, id);
      if (decoratorBottom == null) {
        break missingId;
      }

      id = R.id.decorator_top;
      ImageView decoratorTop = ViewBindings.findChildViewById(rootView, id);
      if (decoratorTop == null) {
        break missingId;
      }

      id = R.id.loginGoogle;
      SignInButton loginGoogle = ViewBindings.findChildViewById(rootView, id);
      if (loginGoogle == null) {
        break missingId;
      }

      id = R.id.logoImageViewLogin;
      ImageView logoImageViewLogin = ViewBindings.findChildViewById(rootView, id);
      if (logoImageViewLogin == null) {
        break missingId;
      }

      id = R.id.materialCardView;
      MaterialCardView materialCardView = ViewBindings.findChildViewById(rootView, id);
      if (materialCardView == null) {
        break missingId;
      }

      id = R.id.textView2;
      TextView textView2 = ViewBindings.findChildViewById(rootView, id);
      if (textView2 == null) {
        break missingId;
      }

      id = R.id.tvwLogin;
      TextView tvwLogin = ViewBindings.findChildViewById(rootView, id);
      if (tvwLogin == null) {
        break missingId;
      }

      id = R.id.tvwRegister;
      TextView tvwRegister = ViewBindings.findChildViewById(rootView, id);
      if (tvwRegister == null) {
        break missingId;
      }

      id = R.id.txtLayoutPassword;
      TextInputLayout txtLayoutPassword = ViewBindings.findChildViewById(rootView, id);
      if (txtLayoutPassword == null) {
        break missingId;
      }

      id = R.id.txtLayoutUser;
      TextInputLayout txtLayoutUser = ViewBindings.findChildViewById(rootView, id);
      if (txtLayoutUser == null) {
        break missingId;
      }

      id = R.id.txtLoginPassword;
      TextInputEditText txtLoginPassword = ViewBindings.findChildViewById(rootView, id);
      if (txtLoginPassword == null) {
        break missingId;
      }

      id = R.id.txtLoginUser;
      TextInputEditText txtLoginUser = ViewBindings.findChildViewById(rootView, id);
      if (txtLoginUser == null) {
        break missingId;
      }

      return new ActivityLoginBinding((ConstraintLayout) rootView, btnLogin, decoratorBottom,
          decoratorTop, loginGoogle, logoImageViewLogin, materialCardView, textView2, tvwLogin,
          tvwRegister, txtLayoutPassword, txtLayoutUser, txtLoginPassword, txtLoginUser);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
