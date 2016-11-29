package com.musicocracy.fpgk.domain.util;

import android.view.View;
import android.widget.ImageButton;

public class AndroidViewUtils {
    public static void setImgBtnEnabled(ImageButton btn, boolean enabled) {
        btn.setEnabled(enabled);
        btn.setClickable(enabled);
        btn.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
    }
}
