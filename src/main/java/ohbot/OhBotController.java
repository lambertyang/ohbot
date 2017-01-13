package ohbot;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
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
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {
        handleTextContent(event.getReplyToken(), event, event.getMessage());
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) throws IOException {
        String text = content.getText();
        if (text.contains("�Ѯ�") && text.contains("?")) {
            text = text.replace("�Ѯ�","").replace("?","").replace("�O","�x").trim();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String strResult = "";
            switch (text) {
                case "�x�_��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_63.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�s�_��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_65.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "��饫": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_68.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�O����": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_66.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "������": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_64.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�򶩥�": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10017.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�s�˥�": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10018.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�s�˿�": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10004.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�]�߿�": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10005.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "���ƿ�": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10007.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�n�뿤": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10008.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "���L��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10009.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�Ÿq��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10020.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�Ÿq��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10010.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�̪F��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10013.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�y����": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10002.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�Ὤ��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10015.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "�O�F��": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10014.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                case "���": {
                    HttpGet httpget = new HttpGet("http://www.cwb.gov.tw/V7/forecast/taiwan/Data/W50_10016.txt");
                    CloseableHttpResponse response = httpClient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity, "utf-8");
                    break;
                }
                default:
                    strResult="�q�j�Q?���j�O?";
            }
            strResult = strResult.replace("<BR><BR>", "\n");
            strResult = strResult.replaceAll("<[^<>]*?>", "");
            this.replyText(replyToken, strResult);
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "�K�K";
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