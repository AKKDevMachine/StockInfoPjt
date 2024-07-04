package spring.apitest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.apitest.Repository.H2StockItemRepository;
import spring.apitest.domain.item.Item;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetStockService {

    private final H2StockItemRepository h2StockItemRepository;

    public LocalDateTime getNowDate() {

        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int value = dayOfWeek.getValue();
        int hour = now.getHour();
        log.info("{}, {}, {}", now, value, hour);

        switch (value){
            case 1, 2: now=now.minusDays(3);
                break;
            case 3, 4, 5, 6: now=now.minusDays(1);
                break;
            case 7 : now=now.minusDays(2);
                break;


        }

        if(0<=hour && hour <=11){
            now=now.minusDays(1);
        }
        return now;
    }


    // 등락 구분 추출 메서드
    public String extractFluctuation(String exdayInfoText) {
        if (exdayInfoText.contains("상승")) {
            return "+";
        } else if (exdayInfoText.contains("하락")) {
            return "-";
        } else {
            return "";
        }
    }

    // 등락 값 추출 메서드
    public String extractFluctuationValue(String exdayInfoText) {
        // "전일대비 상승 2,900 2,900 l + 1.95 1.95 %"에서 등락 값 위치를 찾아 추출
        String[] tokens = exdayInfoText.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("상승") || tokens[i].equals("하락") || tokens[i].equals("보합")) {
                return tokens[i + 1]; // 등락 값은 기호 다음에 위치한 값입니다.
            }
        }
        return "";
    }

    // 등락 퍼센트 추출 메서드
    public String extractFluctuationPercent(String exdayInfoText) {
        // "전일대비 상승 2,900 2,900 l + 1.95 1.95 %"에서 등락 퍼센트 위치를 찾아 추출
        String[] tokens = exdayInfoText.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("%")) {
                return tokens[i - 1]; // 등락 퍼센트는 '%' 바로 앞에 위치한 값입니다.
            }
        }
        return "";
    }

    public Item getStockItem(String srtnCd) {
        LocalDateTime now = getNowDate();


        log.info("{}", now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String nowDate = formatter.format(now);

        Item item = h2StockItemRepository.findItemByIdAndDate(srtnCd, nowDate);
        return item;
    }

}
