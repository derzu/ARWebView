package br.ufpb.visio;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class Util {
    private static String TAG = "Util";

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG,"Permission is granted");
                return true;
            } else {
                Log.i(TAG,"Permission is revoked");
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public static void saveImage(Bitmap bm, String imgName ){
        if (bm != null) {
            try {
                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() +
                        File.separator + "Screenshots/"+imgName+".jpg";

//                Log.i(TAG, "FILE NAME: " + fileName);
//                Log.i(TAG, "FILE NAME: " + fileName);
//                Log.i(TAG, "FILE NAME: " + fileName);

                File file = new File(fileName);
                FileOutputStream fOut = new FileOutputStream(file);

                if (bm.getHeight()>2000)
                    bm=Bitmap.createBitmap(bm, 0,0,bm.getWidth(), 2000);

                bm.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
                fOut.close();

                //Log.i(TAG, "Salvo!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void transparent(Bitmap bm, int color) {
        final int qPixels = bm.getWidth() * bm.getHeight();
        final int [] pixels = new int[qPixels];
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0 , bm.getWidth(), bm.getHeight());
        for (int i = 0 ; i < qPixels ; i++) {
            if (pixels[i] == color)
                pixels[i] = pixels[i] & 0x00FFFFFF;
        }
        bm.setPixels(pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
    }
}
