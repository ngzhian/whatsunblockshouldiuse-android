package com.example.zhian.sensor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ZhiAn on 16/3/2015.
 */
public class ForecastService {
    private static HashMap<DoublePair, String> memo = new HashMap<>();
    
    private static class DoublePair {
        double one;
        double two;
        public DoublePair(double one, double two) {
            this.one = one;
            this.two = two;
        }
    }
    
    public static String getForecast(double lat, double lon) throws IOException {
        DoublePair dp = new DoublePair(lat, lon);
        if (memo.containsKey(dp)) {
            return memo.get(dp);
        }
        String url = "http://iaspub.epa.gov/enviro/m_uv?lat=" + lat + "&lon=" + lon;
        Document doc = Jsoup.connect(url).get();
        Element content = doc.getElementById("content");
        Elements images = content.getElementsByTag("img");
        String uv_index = null;
        for (Element image: images) {
            String src = image.attr("src");
            if (src.contains("UV_Index")) {
                uv_index = getUVIndex(src);
                memo.put(dp, uv_index);
                break;
            }
        }
        return uv_index;
    }
    
    public static String getUVIndex(String image_src) {
        String[] splits = image_src.split("_");
        String index = splits[2];
        return index;
    }
}
