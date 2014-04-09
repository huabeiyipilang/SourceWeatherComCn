package weathersource.weathercomcn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import cn.kli.utils.klilog;

class Utils {
	public static boolean copyDatabaseFile(Context context, boolean isfored, String dbName) {
		String DATABASE_NAME = dbName;
		String DATABASES_DIR = "/data/data/"+context.getPackageName()+"/databases/";
		InputStream in = null;
		try {
			in = context.getAssets().open(DATABASE_NAME);
		} catch (IOException e1) {
			return false;
		}
		
		klilog.info("DATABASES_DIR = "+DATABASES_DIR);
        
        File dir = new File(DATABASES_DIR);  
        if (!dir.exists() || isfored) {  
            try {  
                dir.mkdir();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
          
        File dest = new File(dir, DATABASE_NAME);  
        if(dest.exists() && !isfored){  
            return false;  
        }  
          
        try {  
            if(dest.exists()){  
                dest.delete();  
            }  
            dest.createNewFile();     
            int size = in.available();  
            byte buf[] = new byte[size];  
            in.read(buf);  
            in.close();  
            FileOutputStream out = new FileOutputStream(dest);  
            out.write(buf);  
            out.close();
            return true;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return false;
    }
}
