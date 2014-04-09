package weathersource.weathercomcn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.kli.weather.engine.Weather;

public class WeatherParser {
	
	public static MyCity parser(MyCity city, String source){
		try {
			JSONObject jsonObject = new JSONObject(source).getJSONObject("weatherinfo");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			String date_y = jsonObject.getString("date_y");
			Calendar calendar = null;
			try {
				Date date = sdf.parse(date_y);
				calendar = new GregorianCalendar();
				calendar.setTime(date);
			} catch (ParseException e) {
				return null;
			}

			ArrayList<Weather> weathers = new ArrayList<Weather>();
			//get 'days' weather from today
			int days = 3;
			for(int i = 1; i <= days; i++){
				Weather weather = new Weather();
				weather.city_index = city.index;
				Calendar cal = (Calendar) calendar.clone();
				cal.add(Calendar.DAY_OF_MONTH, i - 1);
				weather.calendar = cal;
				if(i == 1){
					weather.currentTemp = jsonObject.getString("fchh");
				}
				String[] tempRange = jsonObject.getString("temp"+i).split("~");
				weather.maxTemp = tempRange[0].replace("℃", "");
				weather.minTemp = tempRange[1].replace("℃", "");
				weather.weather = getWeatherByName(jsonObject.getString("weather"+i));
				weather.wind = jsonObject.getString("wind"+i);
				weathers.add(weather);
			}
			
			city.weathers = weathers;
		} catch (JSONException e) {
			city = null;
			e.printStackTrace();
		}
		
		return city;
	}
	
	public static String getCurrentTemp(String source){
	    String res = null;
	    try {
            JSONObject jsonObject = new JSONObject(source).getJSONObject("weatherinfo");
            res = jsonObject.getString("temp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
	}

	private static int[] getWeatherByName(String name){
		String[] names = name.split("转");
		ArrayList<Integer> nameList = new ArrayList<Integer>();
		
		for(String item : names){
			int w = Weather.W_NO_DATA;
			if("晴".equals(item)){
				w = Weather.W_QING;
			}else if("多云".equals(item)){
				w = Weather.W_DUOYUN;
			}else if("阴".equals(item)){
				w = Weather.W_YIN;
			}else if("阵雨".equals(item)){
				w = Weather.W_ZHENYU;
			}else if("雷阵雨".equals(item)){
				w = Weather.W_LEIZHENYU;
			}else if("雷阵雨并伴有冰雹".equals(item)){
				w = Weather.W_LEIZHENYUBINGBANYOUBINGBAO;
			}else if("雨夹雪".equals(item)){
				w = Weather.W_YUJIAXUE;
			}else if("小雨".equals(item)){
				w = Weather.W_XIAOYU;
			}else if("中雨".equals(item)){
				w = Weather.W_ZHONGYU;
			}else if("大雨".equals(item)){
				w = Weather.W_DAYU;
			}else if("暴雨".equals(item)){
				w = Weather.W_BAOYU;
			}else if("大暴雨".equals(item)){
				w = Weather.W_DABAOYU;
			}else if("特大暴雨".equals(item)){
				w = Weather.W_TEDABAOYU;
			}else if("阵雪".equals(item)){
				w = Weather.W_ZHENXUE;
			}else if("小雪".equals(item)){
				w = Weather.W_XIAOXUE;
			}else if("中雪".equals(item)){
				w = Weather.W_ZHONGXUE;
			}else if("大雪".equals(item)){
				w = Weather.W_DAXUE;
			}else if("暴雪".equals(item)){
				w = Weather.W_BAOXUE;
			}else if("雾".equals(item)){
				w = Weather.W_WU;
			}else if("冻雨".equals(item)){
				w = Weather.W_DONGYU;
			}else if("沙尘暴".equals(item)){
				w = Weather.W_SHACHENBAO;
			}else if("小雨中雨".equals(item)){
				w = Weather.W_XIAOYUZHONGYU;
			}else if("浮尘".equals(item)){
				w = Weather.W_FUCHEN;
			}else if("扬沙".equals(item)){
				w = Weather.W_YANGSHA;
			}else if("强沙尘暴".equals(item)){
				w = Weather.W_QIANGSHACHENBAO;
			}else if("霾".equals(item)){
			    w = Weather.W_MAI;
			}
			nameList.add(w);
		}
		int[] res = null;
		if(nameList.size() > 0){
			res = listToPrimitive(nameList);
		}
		
		return res;
	}
	
	private static int[] listToPrimitive(List<Integer> list) {
	    if (list == null) {
	        return null;
	    }
	    int[] result = new int[list.size()];
	    for (int i = 0; i < list.size(); ++i) {
	        result[i] = list.get(i).intValue();
	    }
	    return result;
	}
}