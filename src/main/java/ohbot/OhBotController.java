package ohbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ohbot.stockObj.MsgArray;
import ohbot.stockObj.StockData;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lambertyang on 2017/1/13.
 */
@LineMessageHandler
@Slf4j
@RestController
public class OhBotController {
    @Autowired
    private LineMessagingService lineMessagingService;

    @RequestMapping("/")
    public String index() {
        Greeter greeter = new Greeter();
        return greeter.sayHello();
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "city") String city) {
        String strResult = "";
        try {
            if (city != null) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/" + city + ".htm");
                CloseableHttpResponse response = httpClient.execute(httpget);
                HttpEntity httpEntity = response.getEntity();
                strResult = EntityUtils.toString(httpEntity, "utf-8");

                String dateTime = "";
                String temperature = "";
                String comfort = "";
                String weatherConditions = "";
                String rainfallRate = "";

                strResult = strResult.substring(
                strResult.indexOf("<h3 class=\"CenterTitle\">今明預報<span class=\"Issued\">"), strResult.length());
                strResult = strResult.substring(0,strResult.indexOf("</tr><tr>"));
                Pattern pattern = Pattern.compile("<th scope=\"row\">.*?</th>");
                Matcher matcher = pattern.matcher(strResult);
                while(matcher.find()){
                    dateTime = matcher.group().replaceAll("<[^>]*>", "");
                }
                pattern = Pattern.compile("<td>.*?~.*?</td>");
                matcher = pattern.matcher(strResult);
                while(matcher.find()){
                    temperature = matcher.group().replaceAll("<[^>]*>","");
                }
                pattern = Pattern.compile("title=\".*?\"");
                matcher = pattern.matcher(strResult);
                while(matcher.find()){
                    weatherConditions = matcher.group().replace("title=\"", "").replace("\"", "");
                }
                pattern = Pattern.compile("<img.*?</td>[\\s]{0,}<td>.*?</td>");
                matcher = pattern.matcher(strResult);
                while(matcher.find()){
                    comfort = matcher.group().replaceAll("<[^>]*>", "");
                }
                pattern = Pattern.compile("<td>[\\d]{0,3} %</td>");
                matcher = pattern.matcher(strResult);
                while(matcher.find()){
                    rainfallRate = matcher.group().replaceAll("<[^>]*>", "");
                }
                strResult = "氣溫"+temperature+"\n"+dateTime+"\n天氣狀況 : "+weatherConditions+"\n舒適度 : "+comfort+"\n降雨率 : "+rainfallRate;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResult;
    }

    @RequestMapping("/stock")
    public String stock(@RequestParam(value = "stock") String stock) {
        String strResult = "";
        try {
            if (stock != null) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String url="http://mis.twse.com.tw/stock/index.jsp";
                log.info(url);
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpget.setHeader("Accept-Encoding","gzip, deflate, sdch");
                httpget.setHeader("Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4");
                httpget.setHeader("Cache-Control","max-age=0");
                httpget.setHeader("Connection","keep-alive");
                httpget.setHeader("Host","mis.twse.com.tw");
                httpget.setHeader("Upgrade-Insecure-Requests", "1");
                httpget.setHeader("User-Agent",
                                  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
                CloseableHttpResponse response = httpClient.execute(httpget);
                log.info(String.valueOf(response.getStatusLine().getStatusCode()));
                url="http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_"+stock+".tw&_="+
                    Instant.now().toEpochMilli();
                log.info(url);
                httpget = new HttpGet(url);
                response = httpClient.execute(httpget);
                log.info(String.valueOf(response.getStatusLine().getStatusCode()));
                HttpEntity httpEntity = response.getEntity();
                strResult = "";

                Gson gson = new GsonBuilder().create();
                StockData stockData = gson.fromJson(EntityUtils.toString(httpEntity, "utf-8"), StockData.class);
                for(MsgArray msgArray:stockData.getMsgArray()){
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    Double nowPrice = Double.valueOf(msgArray.getZ());
                    Double yesterday = Double.valueOf(msgArray.getY());
                    Double diff = nowPrice - yesterday;
                    String change = "";
                    if (diff == 0) {
                        change = " " + diff;
                    } else if (diff > 0) {
                        change = " +" + decimalFormat.format(diff);
                    } else {
                        change = " -" + decimalFormat.format(diff*(-1));
                    }
                    strResult = msgArray.getC()+" "+msgArray.getN()+" "+change+" \n現價 : "+msgArray.getZ()+"\n開盤 : "+msgArray.getO()+"\n昨收 : "+msgArray.getY()+"\n更新 : "+msgArray.getT();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResult;
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        log.info("Received message(Ignored): {}", event);
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {
        handleTextContent(event.getReplyToken(), event, event.getMessage());
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) throws IOException {
        String text = content.getText();
        log.info(text);
        if (text.endsWith("天氣?") || text.endsWith("天氣？")) {
            weatherResult(text, replyToken);
        }

        if (text.endsWith("氣象?") || text.endsWith("氣象？")) {
            weatherResult2(text, replyToken);
        }

        if(text.startsWith("@") && text.endsWith("?")){
            stock(text, replyToken);
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            Response<BotApiResponse> apiResponse = lineMessagingService
                    .replyMessage(new ReplyMessage(replyToken, messages))
                    .execute();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void weatherResult(String text, String replyToken) throws IOException {
        text = text.replace("天氣", "").replace("?", "").replace("？", "").replace("臺", "台").trim();
        log.info(text);
        try {
            if (text.length() >= 3) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String strResult;
                switch (text) {
                    case "台北市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_63.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "新北市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_65.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "桃園市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_68.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "台南市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_67.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "台中市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_66.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "高雄市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_64.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "基隆市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10017.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "新竹市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10018.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "新竹縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10004.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "苗栗縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10005.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "彰化縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10007.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "南投縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10008.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "雲林縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10009.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "嘉義市": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10020.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "嘉義縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10010.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "屏東縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10013.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "宜蘭縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10002.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "花蓮縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10015.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "台東縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10014.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    case "澎湖縣": {
                        HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10016.txt");
                        CloseableHttpResponse response = httpClient.execute(httpget);
                        HttpEntity httpEntity = response.getEntity();
                        strResult = EntityUtils.toString(httpEntity, "utf-8");
                        break;
                    }
                    default:
                        strResult = "義大利?維大力? \nSorry 我不知道" + text + "是哪裡...";
                }
                strResult = strResult.replace("<BR><BR>", "\n");
                strResult = strResult.replaceAll("<[^<>]*?>", "");
                this.replyText(replyToken, strResult);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void weatherResult2(String text, String replyToken) throws IOException {
        text = text.replace("氣象", "").replace("?", "").replace("？", "").replace("臺", "台").trim();
        log.info(text);
        try {
            if (text.length() >= 3) {
                String strResult;
                String url ="";
                switch (text) {
                    case "台北市": {
                        url="Taipei_City.htm";
                        break;
                    }
                    case "新北市": {
                        url="New_Taipei_City.htm";
                        break;
                    }
                    case "桃園市": {
                        url="Taoyuan_City.htm";
                        break;
                    }
                    case "台南市": {
                        url="Tainan_City.htm";
                        break;
                    }
                    case "台中市": {
                        url="Taichung_City.htm";
                        break;
                    }
                    case "高雄市": {
                        url="Kaohsiung_City.htm";
                        break;
                    }
                    case "基隆市": {
                        url="Keelung_City.htm";
                        break;
                    }
                    case "新竹市": {
                        url="Hsinchu_City.htm";
                        break;
                    }
                    case "新竹縣": {
                        url="Hsinchu_County.htm";
                        break;
                    }
                    case "苗栗縣": {
                        url="Miaoli_County.htm";
                        break;
                    }
                    case "彰化縣": {
                        url="Changhua_County.htm";
                        break;
                    }
                    case "南投縣": {
                        url="Nantou_County.htm";
                        break;
                    }
                    case "雲林縣": {
                        url="Chiayi_City.htm";
                        break;
                    }
                    case "嘉義市": {
                        url="Chiayi_City.htm";
                        break;
                    }
                    case "嘉義縣": {
                        url="Chiayi_County.htm";
                        break;
                    }
                    case "屏東縣": {
                        url="Pingtung_County.htm";
                        break;
                    }
                    case "宜蘭縣": {
                        url="Yilan_County.htm";
                        break;
                    }
                    case "花蓮縣": {
                        url="Hualien_County.htm";
                        break;
                    }
                    case "台東縣": {
                        url="Taitung_County.htm";
                        break;
                    }
                    case "澎湖縣": {
                        url="Penghu_County.htm";
                        break;
                    }
                    default:
                        text="";

                }
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/"+url);
                CloseableHttpResponse response = httpClient.execute(httpget);
                HttpEntity httpEntity = response.getEntity();
                strResult = EntityUtils.toString(httpEntity, "utf-8");
                if(text.equals("")){
                    strResult = "義大利?維大力? \nSorry 我不知道" + text + "是哪裡...";
                    this.replyText(replyToken, strResult);
                }else{
                    String dateTime = "";
                    String temperature = "";
                    String comfort = "";
                    String weatherConditions = "";
                    String rainfallRate = "";
                    strResult = strResult.substring(
                            strResult.indexOf("<h3 class=\"CenterTitle\">今明預報<span class=\"Issued\">"), strResult.length());
                    strResult = strResult.substring(0,strResult.indexOf("</tr><tr>"));
                    Pattern pattern = Pattern.compile("<th scope=\"row\">.*?</th>");
                    Matcher matcher = pattern.matcher(strResult);
                    while(matcher.find()){
                        dateTime = matcher.group().replaceAll("<[^>]*>", "");
                    }
                    pattern = Pattern.compile("<td>.*?~.*?</td>");
                    matcher = pattern.matcher(strResult);
                    while(matcher.find()){
                        temperature = matcher.group().replaceAll("<[^>]*>","");
                    }
                    pattern = Pattern.compile("title=\".*?\"");
                    matcher = pattern.matcher(strResult);
                    while(matcher.find()){
                        weatherConditions = matcher.group().replace("title=\"", "").replace("\"", "").replaceAll("[\\s]{0,}","");
                    }
                    pattern = Pattern.compile("<img.*?</td>[\\s]{0,}<td>.*?</td>");
                    matcher = pattern.matcher(strResult);
                    while(matcher.find()){
                        comfort = matcher.group().replaceAll("<[^>]*>", "").replaceAll("[\\s]{0,}","");
                    }
                    pattern = Pattern.compile("<td>[\\d]{0,3} %</td>");
                    matcher = pattern.matcher(strResult);
                    while(matcher.find()){
                        rainfallRate = matcher.group().replaceAll("<[^>]*>", "");
                    }
                    strResult = text+"氣溫 : "+temperature+"\n"+dateTime+"\n天氣狀況 : "+weatherConditions+"\n舒適度 : "+comfort+"\n降雨率 : "+rainfallRate;
                    this.replyText(replyToken, strResult);
                }

            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void stock(String text, String replyToken) {
        try {
            text = text.replace("@","").replace("?","");
            String strResult;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url="http://mis.twse.com.tw/stock/index.jsp";
            log.info(url);
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpget.setHeader("Accept-Encoding","gzip, deflate, sdch");
            httpget.setHeader("Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4");
            httpget.setHeader("Cache-Control","max-age=0");
            httpget.setHeader("Connection","keep-alive");
            httpget.setHeader("Host","mis.twse.com.tw");
            httpget.setHeader("Upgrade-Insecure-Requests", "1");
            httpget.setHeader("User-Agent",
                              "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            CloseableHttpResponse response = httpClient.execute(httpget);
            log.info(String.valueOf(response.getStatusLine().getStatusCode()));
            url="http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_"+text+".tw&_="+
                Instant.now().toEpochMilli();
            log.info(url);
            httpget = new HttpGet(url);
            response = httpClient.execute(httpget);
            log.info(String.valueOf(response.getStatusLine().getStatusCode()));
            HttpEntity httpEntity = response.getEntity();
            strResult = "";

            Gson gson = new GsonBuilder().create();
            StockData stockData = gson.fromJson(EntityUtils.toString(httpEntity, "utf-8"), StockData.class);
            for(MsgArray msgArray:stockData.getMsgArray()){
                DecimalFormat df=new DecimalFormat("#.##");
                strResult = msgArray.getC()+" "+msgArray.getN()+"\n現價 : "+msgArray.getZ()+"\n開盤 : "+msgArray.getO()+"\n昨收 : "+msgArray.getY()+"\n更新 : "+msgArray.getT();
            }
            this.replyText(replyToken, strResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}