package com.example.fenglei.annotationstudy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anno.BindView;
import com.example.anno.ContentView;
import com.example.fenglei.annotationstudy.api.ViewInjector;


@ContentView(R.layout.activity_main)
public class MainActivity extends Activity {

    @BindView(R.id.textView)
    public TextView textView;

    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjector.injectView(this);
        textView.setText("hahahahaha");
    }
}
