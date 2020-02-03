package cn.easyar.samples.arwebview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 *
 * A WebView Manager.
 *
 * @author derzu
 */
public class WebViewManager {
	protected static final String TAG = "webview";
	private static WebView webView;
    private static boolean stopped = true;
	private static Bitmap bm;
	private static boolean reload = true;
    private static Canvas c;
    private static String currentUrl = "";


	private static Handler hand = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
            Log.i(TAG, "KEYCODE " + msg.what);
            receiveEvent(msg.what, msg.arg1, msg.arg2);
		}
	};

	public static Handler getHandler() {
		return hand;
	}

	public static WebView prepareWebView(final Activity activity) {
        webView = (WebView) activity.findViewById(R.id.webView1);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                //Log.d("TAG", cm.message() + " at " + cm.sourceId() + ":" + cm.lineNumber());
                return true;
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//				isStoragePermissionGranted(activity);
//            }
//        });

        // if you wanna run the WebViewManager.java, set to VISIBLE. And change the LAUNCHER Activity at the Manifest.
        webView.setVisibility(View.VISIBLE);
        //webView.setVisibility(View.INVISIBLE);

        return webView;
    }

    public static void receiveEvent(int keyCode, int x, int y) {
		if (webView!=null) {
		    Log.i(TAG, "EventCode " + keyCode);
			webView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode)); // PRESSIONOU
			webView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,   keyCode)); // SOLTOU
//			if (firstTime)
//				webView.dispatchTouchEvent(MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0));
//			else
//				webView.dispatchTouchEvent(MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_MOVE, x, y, 0));
            reload = true;
		}
	}

	public static void setStopped() {
        stopped = true;
    }

    public static void stop() {
        setStopped();
        //webView.stopLoading();
        //webView.clearCache(true);
        //webView.destroyDrawingCache();

        if (bm!=null)
            bm.eraseColor(Color.RED);

        //Log.i(TAG, "Loading Blank");

		//loadUrl("about:blank");

        //webView.clearView();
        //webView.loadData("<HTML><BODY><H3>Test</H3></BODY></HTML>","text/html","utf-8");

    }

	/**
	 * Must be called by the same thread that init the webview.
	 *
	 * @param url
	 */
	public static void loadUrl(String url) {
        stopped = false;
		if (!url.equals(currentUrl)) {
			Log.i(TAG, "Loading NEW URL: " + url);
			webView.loadUrl(url);
			currentUrl = url;
		}
	}


    /**
     * Printscreen the webview.
     *
     * @return the bitmap of the print.
     */
    public static Bitmap printScreen() {
	    if (stopped)
	        return null;

		//Bitmap b = printScreen(webView);
		Bitmap b = getScreenShot(webView);
		int max = 1500;
        //max = 3500;
		if (b!=null && b.getHeight()>max)
			b= Bitmap.createBitmap(b, 0,0,b.getWidth(), max);

        if (stopped)
            return null;
        return b;
    }

    /**
     * Printscreen the webview.
     *
     * @param webView the webview.
     * @return the bitmap of the print.
     */
	private static Bitmap printScreen2(WebView webView) {
		if (reload) {
			webView.measure(MeasureSpec.makeMeasureSpec(
					MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());
			webView.buildDrawingCache();
			if (webView.getMeasuredWidth()>0 && webView.getMeasuredHeight()>0) {
				bm = Bitmap.createBitmap(webView.getMeasuredWidth(), webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
				reload = false;
			}
		}

		if (bm!=null) {
			c = new Canvas(bm);

			webView.draw(c);
		}

		Log.i(TAG, "W X H " + bm.getWidth() + " x " + bm.getHeight());

		return bm;
	}

    /**
     * This method is not being used because capturePicture is deprecated.
     * @param webView
     * @return
     */
	public static Bitmap printScreen(WebView webView) {
		if (reload) {
			Picture picture = webView.capturePicture();
			if (picture.getWidth()>0 && picture.getHeight()>0) {
				bm = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
				Log.i(TAG, "width height " + bm.getWidth() + " - " + bm.getHeight());
				reload = false;
			}
		}

		c = new Canvas(bm);

		webView.draw(c);

		return bm;
	}


	public static Bitmap getScreenShot(View view) {
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		//Bitmap bitmap = view.getDrawingCache();
		view.setDrawingCacheEnabled(false);
		//Log.i(TAG, "W X H " + bitmap.getWidth() + " x " + bitmap.getHeight());
		return bitmap;
	}

}
