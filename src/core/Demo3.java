package core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import netease.Api;
import netease.UrlParamPair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import secret.JSSecret;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by MiChong on 2017/11/22 0022.
 *
 * 测试搜索功能，包括搜索歌曲，歌手，用户等
 */
public class Demo3 {
    public static void main(String[] args) {
        try {
            String[] music_names=new String[]{"万有引力"};
            for (String music_name : music_names) {
                FileWriter out = new FileWriter(music_name + ".csv");
                int size = 20;
                Set<String> set = new HashSet<>();
                for (int offset=0; offset < 100; ++offset) {
                    System.out.println("offset: " + offset);
                    UrlParamPair upp = Api.SearchMusicList(music_name,"1", offset);
                    String req_str = upp.getParas().toJSONString();
                    // System.out.println("req_str:"+req_str);
                    Connection.Response
                            response = Jsoup.connect("http://music.163.com/weapi/cloudsearch/get/web?csrf_token=")
                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0")
                            .header("Accept", "*/*")
                            .header("Cache-Control", "no-cache")
                            .header("Connection", "keep-alive")
                            .header("Host", "music.163.com")
                            .header("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3")
                            .header("DNT", "1")
                            .header("Pragma", "no-cache")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("cookie", "_iuqxldmzr_=32; _ntes_nuid=4dab97e1556c62b3ccdbea67889a2d2f; WM_TID=cEhvrzoLcLtFRQBVUFM8jMSaHmWlkHrS; _ga=GA1.2.1486149224.1566821259; _ntes_nnid=4dab97e1556c62b3ccdbea67889a2d2f,1618497776714; NMTID=00ORECDCn4qYFy85Ulssyn-5zO9Vx0AAAF6LvK3NA; WEVNSM=1.0.0; WNMCID=ofwomv.1624285295561.01.0; csrfToken=7XFpvHs2q-UmEeIJ5-BWQsFO; __csrf=33940097960b59735e6b43cbba39e677; MUSIC_U=215fffadb5aeba25e67f3c3b969e7dbc5b71a147db440b10678cde310c22307f9cb4377b2d7ba249; ntes_kaola_ad=1; WM_NI=v83iXMQVsxK3BTUcG%2F4AvPEtDYEa%2FxTXemDLQGNKMwJujdDo0YgcVD2zDN%2BW%2F7X556CkruCMdoTLgLqrM7PAqWamnmdpNI0hRRInxJGv2l7Bp6eaGUOLFFJnjoU9CbDRV3Q%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eeacae47b7b78c91aa7df8868fb7d84a978e8baef43a97b084a5bc61a69cf8bbd42af0fea7c3b92ab6e8fd8adb47938abd99ca39828c9b84c625f89fbab1c88088878b82ae749888ab8ec748939982acd07d8aaba685c765f487a984d550f2a68cafaa5dede998d6b83e81b5be96c247a8b19cade648b3ae83d8d34bb389af8eed67a8b082aab364928e878ffb4285aafe8db64ab4eaab82c65f8d948aa7c966aab19bdac26a888e9ad4d437e2a3; JSESSIONID-WYYY=DOR5807lXuHV0KqRdGjU7NiB7f%5CUdmmKQCoRIzmapBdlqpQB6Ee%2Frr4AVt2KjBP4YuE%2F6Bel5hIzu%5CDcgOX%5CNAqb%5CAxFjWgoWTdDKA0MPedFbNp4eg78W6S%2Bh7oP%2F95%5CjYjEGCGGzcQAy1jxoPZ%5Cu%2BByGNDTB4X3bAlm6RMmfaBls%2FCc%3A1624374619775")
                            .data(JSSecret.getDatas(req_str))
                            .method(Connection.Method.POST)
                            .ignoreContentType(true)
                            .timeout(10000)
                            .execute();
                    TimeUnit.SECONDS.sleep(10);
                    String list = response.body();
                    System.out.println(list);
                    System.out.println("-----------------------");
                    JSONObject jsonObject = (JSONObject) JSONObject.parse(list);
                    JSONObject result = (JSONObject) jsonObject.get("result");
                    JSONArray songs = (JSONArray) result.get("songs");
                    size = songs.size();
                    if (size == 0) {
                        break;
                    }
                    for (int i = 0; i < songs.size(); i++) {
                        JSONObject song = songs.getJSONObject(i);
                        String name = song.getString("name");
                        String url = "https://music.163.com/#/song?id=" + song.getString("id");

                        JSONArray ar = (JSONArray) song.get("ar");
                        String author = ar.getJSONObject(0).getString("name");
                        System.out.println(name + "," + url + "," + author);
                        if (set.contains(song.getString("id"))) {
                            System.out.println(song.getString("id") + "alread exist \n");
                            continue;
                        }
                        set.add(song.getString("id"));
                        out.write(name + "," + url + "," + author + "\n");
                    }
                }
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
