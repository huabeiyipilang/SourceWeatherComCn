package weathersource.weathercomcn;

import java.util.ArrayList;

import cn.kli.weather.engine.City;

public class CityParser {
	
	public static ArrayList<MyCity> parser(String res){
		ArrayList<MyCity> cities = new ArrayList<MyCity>();
		String[] cityInfos = res.split(",");
		for(String cityInfo : cityInfos){
			String[] info = cityInfo.split("\\|");
			MyCity city = new MyCity();
			city.setIndex(info[0]);
			city.name = info[1];
			cities.add(city);
		}
		return cities;
	}
	
	public static String getCode(String res){
		return res.split("\\|")[1];
	}
}
