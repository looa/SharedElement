package org.looa.sharedelement;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class SubActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        TextView textView = (TextView) findViewById(R.id.tv_sub);
        Prism.getInstant().initSharedElement(textView);
    }
}
