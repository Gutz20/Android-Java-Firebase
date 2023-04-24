package com.paqta.paqtafood.utils;

import android.graphics.Color;
import android.view.Window;

public class ChangeColorBar {

    public Window window;

    public void cambiarColor(String primaryDark, String primary) {
        window.setStatusBarColor(Color.parseColor(primaryDark));
        window.setNavigationBarColor(Color.parseColor(primary));
    }

}
