package com.groupon.dexlazyload;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    public void onLoadImageClick(View v) {
        Log.d("TAG", "onLoadImageClick");

        final PicassoLoader picassoLoader = new PicassoLoader(this);
        // TODO: Yes ,this should not be done in the main thread. Sorry Kittens.
        picassoLoader.loadModule();
        Log.d("TAG", "module loaded");

        final PicassoWrapper picassoWrapper = PicassoWrapper.get(this);
        Log.d("TAG", "loading image");
        picassoWrapper.load("http://i.imgur.com/CqmBjo5.jpg", imageView);
    }
}
