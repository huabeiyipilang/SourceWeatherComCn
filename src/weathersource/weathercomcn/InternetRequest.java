package weathersource.weathercomcn;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.kli.utils.klilog;

public class InternetRequest {

    private final static int MSG_INTERNET_RESPONSE = 1;
    
    private static Object mRequestLock = new Object();
    private static final String ENCODE="UTF-8";
    private HttpClient mHttpClient;
    private boolean mRequesting = false;
    private String mRespose;
    
    private Thread mThread;
    
    private Context mContext;
    
    private Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
            case MSG_INTERNET_RESPONSE:
                klilog.info("msg get!!!");
                mRespose = (String)msg.obj;
                klilog.info("mRespose: " + mRespose);
                mRequesting = false;
                synchronized(mRequestLock){
                    mRequestLock.notifyAll();
                }
                break;
            }
            
        }
        
    };
    
    synchronized public String request(final String url){
        final String result;
        
        mRequesting = true;
        
        //request
        requestByLocalNetwork(url);
        
        synchronized(mRequestLock){
            try {
                mRequestLock.wait(20*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        result = mRespose;
        return result;
    }
    
    private void requestByLocalNetwork(final String url) {
        mThread = new Thread(){

            @Override
            public void run() {
                super.run();
                String result = null;
                HttpUriRequest req = new HttpGet(url);
                klilog.info("Internet request: " + url);
                try {
                    HttpResponse response = mHttpClient.execute(req);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        result = EntityUtils.toString(response.getEntity(), ENCODE);
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Message msg = mHandler.obtainMessage(MSG_INTERNET_RESPONSE);
                msg.obj = result;
                msg.sendToTarget();
            }
            
        };
        mThread.start();
    }
    
}
