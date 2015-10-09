package com.groupon.dexlazyload;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

// This class is necessary because picasso classes can't be used until the dex is loaded
// It would be nice to find a better way of handling this instead of wrapping methods here
public class PicassoWrapper {
    private Picasso picasso;
    private static PicassoWrapper instance;

    public static PicassoWrapper get(Context ctx) {
        if (instance == null) {
            synchronized (PicassoWrapper.class) {
                if (instance == null) {
                    instance = new PicassoWrapper(ctx);
                }
            }
        }

        return instance;
    }

    private PicassoWrapper(Context ctx) {
        picasso = Picasso.with(ctx);
    }

    public void load(String url, ImageView imageView) {
        picasso.load(url).into(imageView);

    }


}
