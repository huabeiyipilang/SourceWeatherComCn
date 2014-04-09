package weathersource.weathercomcn;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import cn.kli.utils.klilog;
import cn.kli.weather.engine.City;
import cn.kli.weather.engine.ErrorCode;
import cn.kli.weather.engine.WeatherSource;

public class SourceWeatherComCn extends WeatherSource {
	private final static String WEATHER_URL = "http://m.weather.com.cn/data/xxx.html";
    private final static String WEATHER_SK_URL = "http://www.weather.com.cn/data/sk/xxx.html";
	private final static String CITY_URL = "http://m.weather.com.cn/data5/cityxxx.xml";
	private final static String SOURCE = "weather.com.cn";
	
	private static boolean mIniting = false;
	private Context mContext;
	private DataProxy mDataProxy;
	private InternetAccess mAccess;
	
	public SourceWeatherComCn(Context context){
		mContext = context;
	}
	
	@Override
	public String getSourceName() {
		return SOURCE;
	}

	@Override
	public int onInit() {
		mDataProxy = DataProxy.getInstance(mContext);
		mAccess = InternetAccess.getInstance();
		
		klilog.info("prepared = "+mDataProxy.getDataPrepared()+", mIniting = "+mIniting);
		if(!mDataProxy.getDataPrepared() &&!mIniting){
			mIniting = true;
			try {
				ArrayList<MyCity> cities = getCitiesByIndex("");
				saveCities(cities);
				mDataProxy.setDataPrepared(true);
			} catch (Exception e) {
				return ErrorCode.UNKOWN;
			}
		}else{
			
		}
		return ErrorCode.SUCCESS;
	}

	private void saveCities(ArrayList<MyCity> cities){
		for(MyCity city : cities){
			mDataProxy.addCity(city);
			if(city.level <= 2){
				ArrayList<MyCity> childCities = getCitiesByIndex(city.index);
				if(city.level == 2){
					checkCodeByCity(childCities);
				}
				saveCities(childCities);
			}
		}
	}
	
	private ArrayList<MyCity> getCitiesByIndex(String index){
		if(index == null){
			index = "";
		}
		String url = CITY_URL.replace("xxx", index);
		ArrayList<MyCity> cities = new ArrayList<MyCity>();
		String response = mAccess.request(url);
		klilog.info("response = "+response);
		if(!TextUtils.isEmpty(response)){
			cities = CityParser.parser(response);
		}else{
			klilog.info("error respose null");
			throw new NullPointerException();
		}
		return cities;
	}
	
	private void checkCodeByCity(ArrayList<MyCity> cities){
		if(cities == null){
			return;
		}
		for(MyCity city : cities){
			String url = CITY_URL.replace("xxx",city.index);
			String response = mAccess.request(url);
			klilog.info("response = "+response);
			if(!TextUtils.isEmpty(response)){
				city.code = response.split("\\|")[1];
			}else{
				klilog.info("error respose null");
				throw new NullPointerException();
			}
		}
	}
	
	

	@Override
    protected int requestWeatherByCity(City city) {
        MyCity myCity = mDataProxy.getCityByIndex(city.index);
        String url = WEATHER_URL.replace("xxx", myCity.code);
        String response = mAccess.request(url);
        klilog.info("response = "+response);
        if(!TextUtils.isEmpty(response)){
            myCity = WeatherParser.parser(myCity, response);
            city.weathers = myCity.weathers;
            klilog.info("finish");
        }else{
            klilog.info("error respose null");
            return ErrorCode.ERROR_SERVER_RESPONSE;
        }
        String skUrl = WEATHER_SK_URL.replace("xxx", myCity.code);
        String skResponse = mAccess.request(skUrl);
        city.weathers.get(0).currentTemp = WeatherParser.getCurrentTemp(skResponse);
        return ErrorCode.SUCCESS;
    }

    @Override
    protected int requestCityList(City city, List<City> responseList) {
        responseList.addAll(mDataProxy.getCityList(city));
        return ErrorCode.SUCCESS;
    }
}
