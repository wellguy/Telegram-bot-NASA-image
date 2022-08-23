package ru.egorov.nasa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class NasaStart {

    private static ObjectMapper mapper = new ObjectMapper();
    private static String imageUrl;
    private static String caption;


    private static final String API = "api сайта NASA";

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void start() throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        NasaPost post = requestNASA(httpClient);

        System.out.println(post);

        imageUrl = post.getUrl();
        caption = post.getExplanation();

        //закрываем клиент.
        httpClient.close();

    }

    private static NasaPost requestNASA(CloseableHttpClient httpClient) {
        //объект запроса
        HttpGet requestBody = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=" + API);
        //удаленный сервис
        try (CloseableHttpResponse responseBody = httpClient.execute(requestBody)) {
            //Печать ответа
            String body = new String(responseBody.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            //System.out.println(body);
            //парсим из тела ответа объекта и печатаем
            return mapper.readValue(body, NasaPost.class);
        } catch (IOException e) {
            System.out.println("Не удалось установить соединение");
            e.printStackTrace();
        }
        return null;
    }

}



