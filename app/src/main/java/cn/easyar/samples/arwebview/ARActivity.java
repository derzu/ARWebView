//================================================================================================================================
//
// Copyright (c) 2015-2019 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.arwebview;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.app.Activity;
import java.util.HashMap;

import android.webkit.WebView;
import android.widget.Toast;

import cn.easyar.CameraDevice;
import cn.easyar.Engine;
import cn.easyar.ImageTracker;
import cn.easyar.VideoPlayer;

public class ARActivity extends Activity
{
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: ARWebView
    *      Package Name: cn.easyar.samples.arwebview
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "JqEEQSKyHF061ItFCjUZAywtwUAmS9n1C70sOhaTMmoigzR3Fo4jOlnCM30RmiJYAIl5bQWQNTYBknU0QY02axeFJVMGmR58QdpmNEGMPnsGjiR9EMJtQxjCNW0NhDt9KoQkOlm7CjRBljZqCoE5bBDCbUNBgzh1DpU5cReZdUVPwid0ApQxdxGNJDpZu3VvCo4zdxSTdTRBjTZ7Qb17Og6PM20PhSQ6Wbt1awaOJH1NqTp5BIUDagKDPHENh3U0QZMydhCFeVsPjyJ8MYU0dwSOPmwKjzk6T8IkfQ2TMjYxhTR3EYQ+dgTCezoQhTlrBs4YegmFNGw3kjZ7CIk5f0HMdWsGjiR9TbMiagWBNH03kjZ7CIk5f0HMdWsGjiR9TbMneRGTMksTgSNxAowaeRPCezoQhTlrBs4adxeJOHY3kjZ7CIk5f0HMdWsGjiR9TaQydhCFBGgClD55D602aEHMdWsGjiR9TaMWXDeSNnsIiTl/Qb17OgaYJ3ERhQNxDoUEbAKNJzpZjiJ0D8x1cRCsOHsCjHUiBYE7awade2NBgiJ2B4wyUQeTdSI4wjR2TYU2axqBJTYQgTpoD4UkNgKSIH0Blj59FMIKNEGWNmoKgTlsEMJtQ0GDOHUOlTlxF5l1RU/CJ3QClDF3EY0kOlm7dXkNhCV3CoR1RU/COncHlTt9EMJtQ0GTMnYQhXlRDoEwfTeSNnsIiTl/Qcx1awaOJH1Nozt3FoQFfQCPMHYKlD53DcJ7OhCFOWsGzgV9AI8lfAqOMDpPwiR9DZMyNiyCPX0AlANqAoM8cQ2HdTRBkzJ2EIV5SxaSMXkAhQNqAoM8cQ2HdTRBkzJ2EIV5SxOBJWsGsyd5F4k2dC6BJzpPwiR9DZMyNi6PI3EMjgNqAoM8cQ2HdTRBkzJ2EIV5XAaOJH0wkDZsCoE7VQKQdTRBkzJ2EIV5WyKkA2oCgzxxDYd1RU/CMmATiSV9N4k6fTCUNnUTwm12Fow7NEGJJFQMgzZ0QdoxeQ+TMmVPm3V6Fo4zdAapM2tB2gw6Qb17OhWBJXECjiNrQdoMOgCPOnUWjj5sGsIKNEGQO3kXhjhqDpN1IjjCPncQwgo0QY04fBaMMmtB2gw6EIU5awbOHnUChzJMEYE0cwqOMDpPwiR9DZMyNiCMOG0HsjJ7DIc5cReJOHZBzHVrBo4kfU2yMnsMkjNxDYd1NEGTMnYQhXlXAYoyexe0JXkAiz52BMJ7OhCFOWsGzgRtEYY2ewa0JXkAiz52BMJ7OhCFOWsGzgRoApIkfTCQNmwKgTtVApB1NEGTMnYQhXlVDJQ+dw20JXkAiz52BMJ7OhCFOWsGzhN9DZMySxOBI3ECjBp5E8J7OhCFOWsGzhRZJ7QleQCLPnYEwgo0QYUvaAqSMkwKjTJLF4E6aEHaOW0PjHs6CpMbdwCBOzpZhjZ0EIUqRR64jLQEPYrsfNUajQkyl++1ewAWKfK6GQ/v2xAIDGRiJcnjcqWDktb+tE2CrmDY7f7Dza4xrke2FpWEEEia4LvG0fNavU1yf+Uz7xO2CjtUXBAAO7wUurlsgazc8pLkjAKCRXqKJxsfmYNUTtByYQMPYWciL3Shb5Eutm1JbkWJnW+dWZfuGjEoy1/zHJ9ULwVHwPkUW7ESKsx0zQKTV07JUApy1iLD46yrH0QFamhQdA5ZK6DxVvklDgFIrPOzE1cqSGUIun+yZ96KsQMVZRADazvoBxcaT8bd4NGebt/3++9YooEHQNQfeUsSIDED/IMjT411A0fdWtkzMOhj4FcY";
    private GLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ///////////////////////////// ADDED FOR WEBVIEW /////////////////////////////
        webView = WebViewManager.prepareWebView(this);

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
            Toast.makeText(ARActivity.this, Engine.errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (!CameraDevice.isAvailable()) {
            Toast.makeText(ARActivity.this, "CameraDevice not available.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!ImageTracker.isAvailable()) {
            Toast.makeText(ARActivity.this, "ImageTracker not available.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!VideoPlayer.isAvailable()) {
            Toast.makeText(ARActivity.this, "VideoPlayer not available.", Toast.LENGTH_LONG).show();
            return;
        }

        glView = new GLView(this);

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });

        ///////////////////////////// ADDED FOR WEBVIEW /////////////////////////////
        // starts loading the default site.
        glView.post(new Runnable() {
            @Override
            public void run() {
                WebViewManager.loadUrl("https://www.globo.com");
            }
        });

        glView.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.start();
            }
        }, 1000);
    }

    ///////////////////////////// ADDED FOR WEBVIEW /////////////////////////////
    /**
     * Each 1 second does a printscreens from the webview.
     */
    Thread t = new Thread() {
        @Override
        public void run() {
            int i=0;
            Log.i("AR ACTIVITY", "ENTREIN NO RUN");
            stopped = false;
            while (!stopped) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getHandler().sendEmptyMessage(PRINT_WEB);
            }
        }
    };

    private WebView webView;
    private static boolean stopped = false;

    public static int PRINT_WEB  = 1;
    public static int STOP_WEB   = 2;
    public static int RELOAD_URL = 3;

    private static Handler hand = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            if (stopped)
                return;
            if (msg.what == PRINT_WEB) {
                //Log.i("AR ACTIVYT", "SETTING WEB TEXTURE");
                VideoRenderer.setTextureWeb(WebViewManager.printScreen());
            }
            else if (msg.what == STOP_WEB) {
                Log.i("AR ACTIVYT", "STOPPING WEB VIEW");

                WebViewManager.stop();
                stopped = true;
            }
            else if (msg.what == RELOAD_URL) {
                Log.i("AR ACTIVYT", "RELOAD WEB VIEW " + msg.obj);

                WebViewManager.loadUrl((String) msg.obj);
            }
            else if (msg.what == KeyEvent.KEYCODE_PAGE_UP || msg.what == KeyEvent.KEYCODE_PAGE_DOWN) {
                WebViewManager.getHandler().sendMessage(msg);
            }
        }
    };

    public static Handler getHandler() {
        return hand;
    }
    ///////////////////////////// ADDED FOR WEBVIEW END /////////////////////////////


    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }
    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();

        getHandler().sendEmptyMessage(ARActivity.STOP_WEB);
    }


}
