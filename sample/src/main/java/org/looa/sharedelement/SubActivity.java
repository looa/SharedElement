package org.looa.sharedelement;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import org.looa.vision.PrismSE;

public class SubActivity extends FragmentActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        imageView = (ImageView) findViewById(R.id.iv_sub);
        PrismSE.getInstant().matchSharedElement(imageView);
    }

    @Override
    public void onBackPressed() {
        PrismSE.getInstant().finish(imageView);
    }
}
