package spring.apitest.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import spring.apitest.Repository.H2StockItemRepository;
import spring.apitest.Repository.MySqlStockNameRepository;
import spring.apitest.domain.item.Item;
import spring.apitest.domain.member.Member;
import spring.apitest.service.GetStockService;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class OpenApiController {

    private final H2StockItemRepository h2StockItemRepository;
    private final MySqlStockNameRepository mySqlStockNameRepository;
    private final GetStockService getStockService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/stocklist")
    public String getStockList(@SessionAttribute(name="loginMember", required = false) Member loginMember, Model model){

        LocalDateTime now = getStockService.getNowDate();


        log.info("{}", now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String nowDate = formatter.format(now);

        List<Item> itemList = h2StockItemRepository.findAll(nowDate);
        String count = mySqlStockNameRepository.findCount();

        model.addAttribute("itemList", itemList);
        model.addAttribute("count", count);
        model.addAttribute("loginMember", loginMember);
        return "stocklist";
    }

    @GetMapping("/stocklist/{srtnCd}")
    public String getStockItem(@PathVariable String srtnCd, Model model) throws IOException {

        Item item = getStockService.getStockItem(srtnCd);

        LocalDateTime now = getStockService.getNowDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime start = now.minusDays(4);
        String nowDate = now.format(formatter);
        String startDate = start.format(formatter);
        log.info("{}, {}", startDate, nowDate);

        // 주어진 srtnCd에 대한 데이터베이스 조회
        List<Item> byIdAndDate = h2StockItemRepository.findMkpAndDateByIdAndDate(srtnCd, startDate, nowDate);

        // 그래프에 필요한 데이터 구성
        List<String> dates1 = new ArrayList<>();
        List<Long> prices1 = new ArrayList<>();


        // 현재 날짜부터 시작일까지 반복하며 데이터를 구성합니다.
        LocalDateTime dateIterate = start;
        long previousPrice = 0L; // 이전 가격을 0으로 초기화합니다.

        while (!dateIterate.isAfter(now)) {
            String formattedDate = dateIterate.format(formatter);
            dates1.add(formattedDate);

            // 데이터베이스에서 해당 날짜에 대한 아이템을 찾습니다.
            boolean found = false;
            for (Item item1 : byIdAndDate) {
                String itemDate = item1.getBasDt();
                if (itemDate.equals(formattedDate)) {
                    prices1.add(item1.getMkp()); // 데이터가 있으면 가격을 추가합니다.
                    previousPrice = item1.getMkp(); // 이전 가격을 업데이트합니다.
                    found = true;
                    break;
                }
            }

            // 데이터가 없는 경우 이전 가격을 사용하거나, 처음부터 데이터가 없을 경우 0으로 처리합니다.
            if (!found) {
                prices1.add(previousPrice);
            }

            // 다음 날짜로 이동합니다.
            dateIterate = dateIterate.plusDays(1);
        }


        DayOfWeek dayOfWeek1 = start.getDayOfWeek();
        int value1 = dayOfWeek1.getValue();
        if(value1==6){
            LocalDateTime updateStart = start.minusDays(1);
            Item item1 = h2StockItemRepository.findItemByIdAndDate(srtnCd, updateStart.format(formatter));
            prices1.set(0, item1.getMkp());
            prices1.set(1, item1.getMkp());
        }else if(value1==7){
            LocalDateTime updateStart = start.minusDays(2);
            Item item1 = h2StockItemRepository.findItemByIdAndDate(srtnCd, updateStart.format(formatter));
            prices1.set(0, item1.getMkp());
        }

        log.info("{}", prices1);
        log.info("{}", dates1);

        //실시간 주가 크롤링
        String url = "https://finance.naver.com/item/main.nhn?code="+srtnCd; // 네이버 주식 페이지 URL
        Document doc = Jsoup.connect(url).get();

        // 필요한 데이터를 크롤링합니다.

        Element currentPriceElement = doc.selectFirst("p.no_today span");
        String currentPrice = currentPriceElement.text();

        Element exdayInfoElement = doc.selectFirst("p.no_exday");
        String exdayInfoText = exdayInfoElement.text();

        // 등락 구분 추출 (+, -)
        String fluctuation = getStockService.extractFluctuation(exdayInfoText);
        // 등락 값 추출
        String fluctuationValue = getStockService.extractFluctuationValue(exdayInfoText);
        // 등락 퍼센트 추출
        String fluctuationPercent = getStockService.extractFluctuationPercent(exdayInfoText) + "%";


        // 모델에 데이터 추가
        model.addAttribute("fluctuation", fluctuation);
        model.addAttribute("fluctuationValue",fluctuationValue);
        model.addAttribute("fluctuationPercent", fluctuationPercent);
        model.addAttribute("curPrice", currentPrice);
        model.addAttribute("dates", dates1);
        model.addAttribute("prices", prices1);
        model.addAttribute("item", item);

        return "stockitem";
    }

    @GetMapping("/stocklist/stockRank")
    public String getStockRank(){

        return "stockRank";
    }

}

