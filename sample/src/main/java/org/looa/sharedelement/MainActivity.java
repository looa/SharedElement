package org.looa.sharedelement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.looa.vision.PrismSE;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private RecyclerView recycleView;
    private LinearLayoutManager mLayoutManager;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add((i + 1) + ". this is XiaoMei coser.");
        }

        mLayoutManager = new LinearLayoutManager(this);

        adapter = new SimpleAdapter();
        adapter.setData(data);
        adapter.setOnItemClickListener(new SimpleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SubActivity.class);
                PrismSE.getInstant().startActivity(view, intent, true);
            }
        });

        recycleView = (RecyclerView) findViewById(R.id.rv_main);
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setAdapter(adapter);
    }
}
