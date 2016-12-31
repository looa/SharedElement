package org.looa.sharedelement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class SubActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        imageView = (ImageView) findViewById(R.id.iv_sub);
        Prism.getInstant().initSharedElement(imageView);
    }

    @Override
    public void onBackPressed() {
        Prism.getInstant().finish(imageView);
    }
}
