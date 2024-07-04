package spring.apitest.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.apitest.Repository.H2StockItemRepository;
import spring.apitest.Repository.MySqlStockNameRepository;
import spring.apitest.domain.item.Item;
import spring.apitest.domain.item.ItemRepository;
import spring.apitest.domain.item.StockName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenApiService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private final ItemRepository itemRepository;
    private final H2StockItemRepository h2StockItemRepository;
    private final MySqlStockNameRepository mySqlStockNameRepository;

    @Scheduled(cron = "0 0 8,13,20 * * *") // 매일 8시, 13시, 20시에 실행
    @Transactional
    public void getApiSchedule() throws IOException {

        LocalDateTime now = LocalDateTime.now();;
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int value = dayOfWeek.getValue();
        int hour = now.getHour();
        log.info("{}, {}, {}", now, value, hour);

        switch (value){
            case 1, 2 : now=now.minusDays(3);
                break;
            case 3, 4, 5, 6: now=now.minusDays(1);
                break;
            case 7 : now=now.minusDays(2);
                break;


        }


        if(0<=hour && hour <=11){
            now=now.minusDays(1);
        }


        log.info("{}", now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String nowDate = formatter.format(now);


        log.info(nowDate);

        String apiUrl = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";
        String serviceKey = "WYIUcexBXmPnj68GHRo%2FviP5RiWfySCWYYgzSx1QePp6MvhonxE6Yb8UhGhBBsKZf%2BcFF7esB1IQRtcAdGHyDQ%3D%3D";
        String basDt = nowDate;
        String resultType = "json";
        String numOfRows = "2810";


        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("basDt","UTF-8") + "=" + URLEncoder.encode(basDt, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));

        log.info("url={}", urlBuilder);
        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");


        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result= sb.toString();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Map<String, Object> map = objectMapper.readValue(result, Map.class);
        Map<String, Object> responseMap = (Map<String, Object>) map.get("response");
        Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
        Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");

      /*  Object bodyMap1 = bodyMap;
        String s1 = objectMapper.writeValueAsString(bodyMap1);
        StockValue stockValue1 = objectMapper.readValue(s1, StockValue.class);*/


        Object o = itemsMap.get("item");
        String s = objectMapper.writeValueAsString(o);
        Item[] items = objectMapper.readValue(s, Item[].class);
        StockName[] stockNames = objectMapper.readValue(s, StockName[].class);
        for (StockName stockName : stockNames) {
            mySqlStockNameRepository.save(stockName);
        }

        for (Item item : items) {
            itemRepository.save(item);
        }

        List<Item> itemList1 = itemRepository.findAll();


        for (Item item1 : itemList1) {
            log.info("ID = {}, 기준일자 {}, 종목명 {} , 시장구분 {}, 종가 {}, 시가 {}", item1.getId(), item1.getBasDt(), item1.getItmsNm(), item1.getMrktCtg(), item1.getClpr(), item1.getMkp());

        }


        h2StockItemRepository.save(itemList1);
        itemRepository.clear();

    }
}
