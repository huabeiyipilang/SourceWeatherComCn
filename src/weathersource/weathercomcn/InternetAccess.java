package weathersource.weathercomcn;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import cn.kli.utils.klilog;

public class InternetAccess {
	private static final String ENCODE="UTF-8";
	private static InternetAccess sInstance;
	
	private InternetAccess(){
	    
	}
	
	public static InternetAccess getInstance(){
		if(sInstance == null){
			sInstance = new InternetAccess();
		}
		return sInstance;
	}
	
	   
    synchronized public String request(String url){
        String result = null;
        
        HttpUriRequest req = new HttpGet(url);
        klilog.info("Internet request: " + url);
        try {
            HttpResponse response = new DefaultHttpClient().execute(req);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), ENCODE);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
	
}
