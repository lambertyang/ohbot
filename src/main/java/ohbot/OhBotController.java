package ohbot;

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
import java.util.Collections;
import java.util.List;

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
                HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_" + city + ".txt");
                CloseableHttpResponse response = httpClient.execute(httpget);
                HttpEntity httpEntity = response.getEntity();
                strResult = EntityUtils.toString(httpEntity, "utf-8");
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
        if (text.endsWith("天氣?")) {
            text = text.replace("天氣", "").replace("?", "").replace("臺", "台").trim();
            if (text.length() == 3) {


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
                    case "臺中市": {
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
                    case "臺東縣": {
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
        }
        this.replyText(replyToken, text);
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
}