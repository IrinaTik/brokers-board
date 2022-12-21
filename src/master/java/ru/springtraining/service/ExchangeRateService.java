package ru.springtraining.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.springtraining.mappers.CBCurrencyToCurrencyMapper;
import ru.springtraining.response.ExchangeRateResponse;
import ru.springtraining.response.CBCurrency;
import ru.springtraining.entity.Currency;
import ru.springtraining.entity.ExchangeRate;
import ru.springtraining.entity.ExchangeRateHistory;
import ru.springtraining.repository.ExchangeRateRepository;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExchangeRateService {

    private final ExchangeRateRepository rateRepository;
    private final CurrencyService currencyService;
    private final CBCurrencyToCurrencyMapper cbCurrencyToCurrencyMapper;

    // сайт вида https://www.cbr-xml-daily.ru//archive//yyyy//MM//dd//daily_json.js
    private static String CBSiteArchive = "https://www.cbr-xml-daily.ru//archive//%s//%s//%s//daily_json.js";

    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static LocalDate FIRST_CB_DATE = LocalDate.of(1992, Month.JULY, 1);
    private final static String RUB_CODE = "RUB";
    private final static Integer DEFAULT_NOMINAL = 1;
    private final static Integer DECIMAL_ROUNDUP = 10_000;

    public List<ExchangeRate> getAll() {
        return rateRepository.findAll();
    }

    public ExchangeRate getRateById(Integer id) {
        return rateRepository.findById(id).orElse(null);
    }

    public ExchangeRate add(ExchangeRate rate) {
        return rateRepository.saveAndFlush(rate);
    }

    public ExchangeRate update(ExchangeRate rate) {
        return rateRepository.saveAndFlush(rate);
    }

    public ExchangeRateResponse getByCurrenciesAndDate
            (String firstCurrencyCode, String secondCurrencyCode, LocalDate date) throws Exception {
        Map<String, CBCurrency> cbCurrencyMap = new HashMap<>();
        Currency firstCurrency = getCurrency(cbCurrencyMap, date, firstCurrencyCode);
        Currency secondCurrency = getCurrency(cbCurrencyMap, date, secondCurrencyCode);
        if ((firstCurrency == null) || (secondCurrency == null)) {
            return null;
        }
        return sendExchangeRateResponse(cbCurrencyMap, date, firstCurrency, secondCurrency);
    }

    private ExchangeRateResponse sendExchangeRateResponse(Map<String, CBCurrency> cbCurrencyMap, LocalDate date,
                                                          Currency firstCurrency, Currency secondCurrency) throws Exception {
        ExchangeRate rate = rateRepository.getByCurrencies(firstCurrency, secondCurrency);
        // если курса нет в БД
        if (rate == null) {
            rate = createAndSaveExchangeRate(cbCurrencyMap, date, firstCurrency, secondCurrency);
            return createExchangeRateResponse(rate, rate.getHistory().get(0));
        } else {
            // если и валюты, и их курс были в БД, то ищем в истории запись за указанную дату
            List<ExchangeRateHistory> history = rate.getHistory();
            for (ExchangeRateHistory entry : history) {
                if (entry.getDate().isEqual(date)) {
                    return createExchangeRateResponse(rate, entry);
                }
            }
            // записи за указанную дату не оказалось, создаем ее и записываем в БД
            ExchangeRateHistory newHistoryEntry =
                    createHistoryAndUpdateExchangeRate(cbCurrencyMap, date, firstCurrency, secondCurrency, rate);
            return createExchangeRateResponse(rate, newHistoryEntry);
        }
    }

    private ExchangeRateHistory createHistoryAndUpdateExchangeRate(Map<String, CBCurrency> cbCurrencyMap, LocalDate date,
                                              Currency firstCurrency, Currency secondCurrency, ExchangeRate rate) throws Exception {
        cbCurrencyMap = getCBInfo(cbCurrencyMap, date);
        ExchangeRateHistory newHistoryEntry =
                createExchangeRateHistoryFromCBInfo(cbCurrencyMap, date, firstCurrency.getCode(), secondCurrency.getCode());
        newHistoryEntry.setRate(rate);
        rate.getHistory().add(newHistoryEntry);
        update(rate);
        return newHistoryEntry;
    }

    private Currency getCurrency(Map<String, CBCurrency> cbCurrencyMap, LocalDate date, String currencyCode) throws Exception {
        Currency currency = getCurrencyFromBD(currencyCode);
        if (currency == null) {
            CBCurrency bankCurrency = getBankCurrency(cbCurrencyMap, date, currencyCode);
            // если информации о валюте нет на сайте ЦБ, то ввод кодов был неверным
            if (bankCurrency == null) {
                return null;
            }
            currency = createCurrencyFromCBCurrency(bankCurrency);
            return saveCurrencyToBD(currency);
        }
        return currency;
    }

    private Map<String, CBCurrency> getCBInfo(Map<String, CBCurrency> cbCurrencyMap, LocalDate date) throws Exception {
        if (cbCurrencyMap.isEmpty()) {
            return getCBInfo(date);
        }
        return cbCurrencyMap;
    }

    private Currency getCurrencyFromBD(String currencyCode) {
        return currencyService.findByCode(currencyCode);
    }

    private Currency saveCurrencyToBD(Currency currency) {
        return currencyService.add(currency);
    }

    private Currency createCurrencyFromCBCurrency(CBCurrency bankCurrency) {
        Currency currency = cbCurrencyToCurrencyMapper.cbCurrencyToCurrency(bankCurrency);
        currency.setOrg("Other");
        return currency;
    }

    private CBCurrency getBankCurrency(Map<String, CBCurrency> cbCurrencyMap, LocalDate date, String currencyCode) throws Exception {
        cbCurrencyMap = getCBInfo(cbCurrencyMap, date);
        return cbCurrencyMap.get(currencyCode.toLowerCase());
    }

    private ExchangeRate createAndSaveExchangeRate
            (Map<String, CBCurrency> cbCurrencyMap, LocalDate date, Currency firstCurrency, Currency secondCurrency) throws Exception {
        ExchangeRate rate = createExchangeRateFromCBInfo(cbCurrencyMap, date, firstCurrency, secondCurrency);
        return add(rate);
    }

    private Map<String, CBCurrency> getCBInfo(LocalDate date) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String month;
        if (date.getMonthValue() < 10) {
            month = "0" + date.getMonthValue();
        } else {
            month = "" + date.getMonthValue();
        }
        URL url = new URL(String.format(CBSiteArchive, date.getYear(), month, date.getDayOfMonth()));
        JsonNode node = objectMapper.readTree(url);
        JsonNode valuteNode = node.get("Valute");
        String valuteNodeString = valuteNode.toString().toLowerCase();
        return objectMapper.convertValue(objectMapper.readTree(valuteNodeString), new TypeReference<>() {
        });
    }

    private ExchangeRate createExchangeRateFromCBInfo
            (Map<String, CBCurrency> cbCurrencyMap, LocalDate date, Currency firstCurrency, Currency secondCurrency) throws Exception {
        ExchangeRate rate = new ExchangeRate();
        rate.setFirstCurrency(firstCurrency);
        rate.setSecondCurrency(secondCurrency);
        rate.setFirstCurrencyValue(DEFAULT_NOMINAL);
        rate.setHistory(new ArrayList<>());
        ExchangeRateHistory historyEntry =
                createExchangeRateHistoryFromCBInfo(cbCurrencyMap, date, firstCurrency.getCode(), secondCurrency.getCode());
        historyEntry.setRate(rate);
        rate.getHistory().add(historyEntry);
        return rate;
    }

    private ExchangeRateHistory createExchangeRateHistoryFromCBInfo
            (Map<String, CBCurrency> cbCurrencyMap, LocalDate date, String firstCurrencyCode, String secondCurrencyCode) throws Exception {
        ExchangeRateHistory historyEntry = new ExchangeRateHistory();
        historyEntry.setDate(date);
        // вычисление значений курса - для пар валют с рублем и без рубля (кросс-курс)
        if (isRuble(firstCurrencyCode)) {
            CBCurrency bankCurrency = getBankCurrency(cbCurrencyMap, date, secondCurrencyCode);
            Double rateValue = 1 / (bankCurrency.getValue() * bankCurrency.getNominal());
            historyEntry.setCurrencyValue(roundUpToFourDecimalPlaces(rateValue));
        } else {
            if (isRuble(secondCurrencyCode)) {
                historyEntry.setCurrencyValue(getBankCurrency(cbCurrencyMap, date, firstCurrencyCode).getValue());
            } else {
                // кросс-курс вычисляется по формуле (value1 * nominal2)/(value2 * nominal1)
                CBCurrency firstBankCurrency = getBankCurrency(cbCurrencyMap, date, firstCurrencyCode);
                CBCurrency secondBankCurrency = getBankCurrency(cbCurrencyMap, date, secondCurrencyCode);
                Double crossRateValue = (firstBankCurrency.getValue() * secondBankCurrency.getNominal()) /
                        (secondBankCurrency.getValue() * firstBankCurrency.getNominal());
                historyEntry.setCurrencyValue(roundUpToFourDecimalPlaces(crossRateValue));
            }
        }
        return historyEntry;
    }

    public LocalDate createDateFromRequest(String date) {
        try {
            return LocalDate.parse(date.trim(), DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ExchangeRateResponse createExchangeRateResponse(ExchangeRate rate, ExchangeRateHistory historyEntry) {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setFirstCurrency(rate.getFirstCurrency());
        response.setSecondCurrency(rate.getSecondCurrency());
        response.setFirstCurrencyValue(rate.getFirstCurrencyValue());
        response.setSecondCurrencyValue(historyEntry.getCurrencyValue());
        response.setDate(historyEntry.getDate());
        return response;
    }

    public boolean isDateValid(LocalDate date) {
        return date.isAfter(FIRST_CB_DATE) || date.isEqual(FIRST_CB_DATE);
    }

    private boolean isRuble(String currencyCode) {
        return currencyCode.equals(RUB_CODE);
    }

    private Double roundUpToFourDecimalPlaces(Double value) {
        return (double) Math.round(value * DECIMAL_ROUNDUP) / DECIMAL_ROUNDUP;
    }
}
