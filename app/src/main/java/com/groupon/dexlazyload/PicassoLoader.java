package com.groupon.dexlazyload;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PicassoLoader {
    private static final String PICASSO_DEX_NAME = "picasso-2.5.2.dex.jar";

    private Context ctx;

    public PicassoLoader(Context ctx) {
        this.ctx = ctx;
    }

    // TODO: getCodeCacheDir() is a API 21 method.
    // This need to be changed to support older versions.
    private File getOutDexDir() {
        return ctx.getCodeCacheDir();
    }

    // loadModule will return true if the module was loaded correctly.
    public boolean loadModule() {
        final long startTime = System.currentTimeMillis();
        Log.d("TAG", "Start loading Picasso");

        final File dexInternalStoragePath = new File(getOutDexDir(), PICASSO_DEX_NAME);
        if (!dexInternalStoragePath.exists()) {
            final long startPrepareDex = System.currentTimeMillis();
            if (prepareDex(dexInternalStoragePath)) {
                Log.d("TAG", "dex moved in " + (System.currentTimeMillis() - startPrepareDex));
            } else {
                return false;
            }
        }

        final File optimizedDexOutputPath = getOutDexDir();

        final List<File> list = new ArrayList<>(1);
        list.add(dexInternalStoragePath);
        try {
            MultiDex.installSecondaryDexes(ctx.getClassLoader(), optimizedDexOutputPath, list);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        Log.d("TAG", "Whole process took: " + (System.currentTimeMillis() - startTime));
        return true;
    }


    private static final int BUF_SIZE = 8 * 1024;
    private boolean prepareDex(File dexInternalStoragePath) {
        BufferedInputStream bis = null;
        OutputStream dexWriter = null;

        try {
            final AssetManager mngr = ctx.getAssets();
            final InputStream is = mngr.open(PICASSO_DEX_NAME);
            bis = new BufferedInputStream(is);
            dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            final byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
            return true;

        } catch (IOException e) {
            if (dexWriter != null) {
                try {
                    dexWriter.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            Log.d("General I/O exception: ", e.getMessage());
            return false;
        }
    }
}
