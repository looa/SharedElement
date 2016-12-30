package org.looa.sharedelement;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class SubActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        ImageView imageView = (ImageView) findViewById(R.id.iv_sub);
        Prism.getInstant().initSharedElement(imageView);
    }
}
