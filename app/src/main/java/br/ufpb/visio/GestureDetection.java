package br.ufpb.visio;

import android.graphics.Point;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import cn.easyar.Image;
import cn.easyar.samples.arwebview.WebViewManager;

/**
 *
 * A WebView Manager.
 *
 * @author derzu
 */
public class GestureDetection {
    private String TAG = "GestureDetection";

    private int [] pixels_atual = null;
    private int [] pixels_anterior = null;
    private static final int MOVE_SIZE = 3;
    private int [] moveVertical = new int[MOVE_SIZE];
    private int moveVerticalIndex = 0;
    private int ind = 0;
    private Point av1, av2=null; // Avarage1,2
    private long t1=0, t2=0;

    // https://stackoverflow.com/a/12702836/1178478
    /**
     * Converts YUV420 NV21 to Y.
     *
     * @param pixels output array with the converted array o grayscale pixels
     * @param data byte array on YUV420 NV21 format.
     * @param width pixels width
     * @param height pixels height
     */
    public static void convertYUV420_NV21toY(int [] pixels, byte [] data, int width, int height) {
        //int p;
        int size = width*height;
        for(int i = 0; i < size; i++) {
            pixels[i] = data[i] & 0xFF;
        }
    }

    // https://stackoverflow.com/a/12702836/1178478
    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param data byte array on YUV420 NV21 format.
     * @param width pixels width
     * @param height pixels height
     * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    public static void convertYUV420_NV21toRGB8888(int [] pixels, byte [] data, int width, int height) {
        int size = width*height;
        int offset = size;
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = data[i  ]&0xff;
            y2 = data[i+1]&0xff;
            y3 = data[width+i  ]&0xff;
            y4 = data[width+i+1]&0xff;

            u = data[offset+k  ]&0xff;
            v = data[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            pixels[i  ] = convertYUVtoRGB(y1, u, v);
            pixels[i+1] = convertYUVtoRGB(y2, u, v);
            pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
            pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i+=width;
        }
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)(1.402f*v);
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)(1.772f*u);
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }

    private void getBitmapFromStreamerInt(Image frame, int [] pixels, boolean gray) {

        //Log.i(TAG, " ");
        //Log.i(TAG, "\n\nFrame images size: " + frame.images().size() + " text " + frame.text());
        if (!frame.empty()) {
            int size   = frame.buffer().size();
            int width  = frame.width();
            int height = frame.height();
            int format = frame.format();
            //Log.i(TAG, "Frame buffer size: " + size);
            // Log.i(TAG,"format " +  format + " " + width + "x" + height);
            if (size>0) {
                byte[] bytes = new byte[size];
                frame.buffer().copyToByteArray(bytes);

                // https://www.easyar.com/doc/EasyAR%20SDK/API%20Reference/2.0/Image.html
                // 2 == YUV_NV21
                if (format==2) {
                    if (gray)
                        convertYUV420_NV21toY(pixels, bytes, width, height);
                    else
                        convertYUV420_NV21toRGB8888(pixels, bytes, width, height);
                }
            }
        }
    }

    int mode(int v[]) {
        int q0=0;
        int qUP=0;
        int qDOWN=0;
        for (int e : v) {
            if (e==0)
                q0++;
            if (e== KeyEvent.KEYCODE_PAGE_DOWN)
                qDOWN++;
            if (e==KeyEvent.KEYCODE_PAGE_UP)
                qUP++;
        }

        if (qDOWN > q0 && qDOWN > qUP)
            return KeyEvent.KEYCODE_PAGE_DOWN;
        else if (qUP > q0 && qUP > qDOWN)
            return KeyEvent.KEYCODE_PAGE_UP;
        else
            return 0;
    }

    private void clear(int[] v) {
        for (int i=0 ; i<v.length ; i++)
            v[i] = 0;
    }

    public void processFrame(cn.easyar.Image frame) {
        if (!frame.empty()) {
            int w, h;
            float wf;
            int size1 = 0;
            int size2 = 0;
            wf = w = frame.width();
            h = frame.height();
            size1 = frame.buffer().size(); // MAIOR
            size2 = w*h;                                   // MENOR
            int mW = (int) (w*0.25);
            int mH = (int) (h*0.25);
            //Log.i(TAG, "w x h::" + w + " x " + h + "::"  + mW + "-" + mH);
            //Log.i(TAG, "SIZE1::" + size1);
            //Log.i(TAG, "SIZE2::" + size2);

            //Log.i(TAG, "processFrame::ENTER");
            if (pixels_atual == null) {
                Log.i(TAG, "processFrame::INICIANDO ARRAYS DE PIXELS!!");
                pixels_atual    = new int[size2];
                pixels_anterior = new int[size2];
                //pixels_small    = new int[size2/4];
                //pixels_diff     = new int[size2];
            }

            getBitmapFromStreamerInt(frame, pixels_atual, true);

            float diff_quant = 0;
            int diff;
            int x, y;
            Point a = new Point(0,0);
            if (pixels_atual != null) {
                diff_quant=0;
                av1 = new Point(0, 0);
                for (int i = 0; i < size2; i++) {
                    x = i%w;
                    y = (int) (i/wf);
                    diff = pixels_anterior[i] - pixels_atual[i];
//                    if (ind%5==0 && i%100==0) {
//                        Log.i(TAG, "X-Y:: " + (i%w) + "x" + (i/wf));
//                        Log.i(TAG, "pixels ATUA:: " + pixels_atual[i]);
//                        Log.i(TAG, "pixels ANTE:: " + pixels_anterior[i]);
//                        Log.i(TAG, "pixels DIFF:: " + pixels_diff[i]);
//                    }

                    if (x>mW && x<w-mW && y>mH && y<h-mH)
                        if (diff > 50 && diff<=256) {
                            diff_quant ++;
                            av1.x += x;
                            av1.y += y;
                        }
                    //else
                    //    diff = 0;
                }
                if (diff_quant==0) diff_quant=1;
                av1.x /= diff_quant;
                av1.y /= diff_quant;

                //Log.i(TAG, "diff_quant:: " + diff_quant);

                if (av2!=null) {
                    if (diff_quant > 5000) {
                        int limiar1 = 40;
                        int limiar2_1 = 300;
                        int limiar2_2 = 300;
                        int diffx = (av1.x - av2.x);
                        int diffy = (av1.y - av2.y);
                        //Log.i(TAG, "DIFF X = " + String.format("%6d",diffx) + " DIFF Y = " + String.format("%6d",diffy));
                        //Log.i(TAG, " X " + av1.x + " Y " + av1.y + " X2 " + av2.x + " Y2 " + av2.y);

                        // -L2 <-- OK --> -L1 <-- 0 --> L1 <-- OK --> L2
                        if (diffx > limiar1 && diffx < limiar2_1) {
                            Log.i(TAG, "moveu BAIXO");
                            moveVertical[moveVerticalIndex] = KeyEvent.KEYCODE_PAGE_UP;
                            moveVerticalIndex = (moveVerticalIndex + 1) % MOVE_SIZE;
                        } else if (diffx < -limiar1 && diffx > -limiar2_1) {
                            Log.i(TAG, "moveu CIMA");
                            moveVertical[moveVerticalIndex] = KeyEvent.KEYCODE_PAGE_DOWN;
                            moveVerticalIndex = (moveVerticalIndex + 1) % MOVE_SIZE;
                        }

                        t1 = System.currentTimeMillis();
                        int event = mode(moveVertical);
                        if (event>0) {
                            if ((t1-t2) > 600)
                            {
                                Message msg = WebViewManager.getHandler().obtainMessage(event, av1.x, av1.y);
                                WebViewManager.getHandler().sendMessage(msg);
                                Log.i(TAG, "USEI EVENTO!!");
                                clear(moveVertical);
                                t2 = t1;
                            }
                            //else {
                            //    Log.i(TAG, "eventos muito proximos, ignorando.");
                            //}
                        }else {
                            //Log.i(TAG, "eventos invalido (zero)");
                        }

/*
                        if (diffy > limiar1 && diffy < limiar2_2) {
                            event = KeyEvent.KEYCODE_DPAD_LEFT;
                            Log.i(TAG, "moveu ESQUERDA::" + String.format("%5d", diffy));
                        } else if (diffy < -limiar1 && diffy > -limiar2_2) {
                            event = KeyEvent.KEYCODE_DPAD_RIGHT;
                            Log.i(TAG, "moveu DIREITA ::" + String.format("%5d", diffy));
                        }
*/
                        av2.x = av1.x;
                        av2.y = av1.y;

//                        Bitmap frame_bm = getBitmapFromPixels(pixels_diff, w, h, true);
//                        WebViewManager.saveImage(frame_bm, "teste_diff"+String.format("%04d", ind));
//                        frame_bm = getBitmapFromPixels(pixels_atual, w, h, true);
//                        WebViewManager.saveImage(frame_bm, "teste_atual"+String.format("%04d", ind));
                        ind++;
                    }
                }
                else {
                    av2 = new Point(av1.x, av1.y);
                }

                //System.arraycopy(pixels_atual, 0, pixels_anterior, 0, size2);
                for (int i=0 ; i<size2 ; i++)
                    pixels_anterior[i] = pixels_atual[i];
            }
        }
    }



}
