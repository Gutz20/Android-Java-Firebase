// Generated by view binder compiler. Do not edit!
package com.paqta.paqtafood.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.card.MaterialCardView;
import com.paqta.paqtafood.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentThirdStepCartBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final MaterialCardView cardPaymentEWallet;

  @NonNull
  public final MaterialCardView cardPaymentPagoEfectivo;

  @NonNull
  public final MaterialCardView cardPaymentPaypal;

  @NonNull
  public final MaterialCardView cardPaymentVisa;

  private FragmentThirdStepCartBinding(@NonNull FrameLayout rootView,
      @NonNull MaterialCardView cardPaymentEWallet,
      @NonNull MaterialCardView cardPaymentPagoEfectivo,
      @NonNull MaterialCardView cardPaymentPaypal, @NonNull MaterialCardView cardPaymentVisa) {
    this.rootView = rootView;
    this.cardPaymentEWallet = cardPaymentEWallet;
    this.cardPaymentPagoEfectivo = cardPaymentPagoEfectivo;
    this.cardPaymentPaypal = cardPaymentPaypal;
    this.cardPaymentVisa = cardPaymentVisa;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentThirdStepCartBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentThirdStepCartBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_third_step_cart, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentThirdStepCartBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardPaymentEWallet;
      MaterialCardView cardPaymentEWallet = ViewBindings.findChildViewById(rootView, id);
      if (cardPaymentEWallet == null) {
        break missingId;
      }

      id = R.id.cardPaymentPagoEfectivo;
      MaterialCardView cardPaymentPagoEfectivo = ViewBindings.findChildViewById(rootView, id);
      if (cardPaymentPagoEfectivo == null) {
        break missingId;
      }

      id = R.id.cardPaymentPaypal;
      MaterialCardView cardPaymentPaypal = ViewBindings.findChildViewById(rootView, id);
      if (cardPaymentPaypal == null) {
        break missingId;
      }

      id = R.id.cardPaymentVisa;
      MaterialCardView cardPaymentVisa = ViewBindings.findChildViewById(rootView, id);
      if (cardPaymentVisa == null) {
        break missingId;
      }

      return new FragmentThirdStepCartBinding((FrameLayout) rootView, cardPaymentEWallet,
          cardPaymentPagoEfectivo, cardPaymentPaypal, cardPaymentVisa);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
