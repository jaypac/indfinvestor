package com.indfinvestor.app.navprocessor.service;

import com.google.common.math.Quantiles;
import com.google.common.math.StatsAccumulator;
import com.indfinvestor.app.navprocessor.entity.MfReturnStats;
import com.indfinvestor.app.navprocessor.entity.MfRollingReturns;
import com.indfinvestor.app.navprocessor.model.MfNavRecord;
import com.indfinvestor.app.navprocessor.repository.MfReturnStatsRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MfRollingReturnProcessor {

    private static Logger LOG = LoggerFactory.getLogger(MfRollingReturnProcessor.class);

    private final MfReturnStatsRepository mfReturnStatsRepository;

    public MfRollingReturnProcessor(MfReturnStatsRepository mfReturnStatsRepository) {
        this.mfReturnStatsRepository = mfReturnStatsRepository;
    }

    private LocalDate localDate(LocalDate navDate, Map<LocalDate, String> navDateMap, Long minusYears) {
        var oldDate = navDate.minusYears(minusYears);

        var isNavPresentForDate = navDateMap.containsKey(oldDate);
        var count = 7;
        while (!isNavPresentForDate && count-- > 0) {
            oldDate = oldDate.minusDays(1L);
            isNavPresentForDate = navDateMap.containsKey(oldDate);
        }

        return oldDate;
    }

    private BigDecimal calculateCagr(BigDecimal futurePrice, BigDecimal oldPrice, int numberOfYears) {

        //CAGR = (FV / PV) ^ (1 / n) – 1
        MathContext mc = new MathContext(2, RoundingMode.HALF_UP);

        var division = futurePrice.divide(oldPrice, mc);
        var exponent = BigDecimal.ONE.divide(new BigDecimal(numberOfYears), mc);
        var pow = Math.pow(division.doubleValue(), exponent.doubleValue());
        var cagr = BigDecimal.valueOf(pow).subtract(BigDecimal.ONE);
        return cagr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Calculates the rolling returns for a list of IndexData records.
     *
     * @param historicalIndexData List of IndexData records containing the index name, date, and closing price
     */
    public void calculateRollingReturns(Map<String, List<MfNavRecord>> historicalIndexData) {

        for (var navHistory : historicalIndexData.values()) {
            var schemeName = navHistory.stream().findFirst().get().getSchemeName();
            var schemeCode = navHistory.stream().findFirst().get().getSchemeCode();
            var category = navHistory.stream().findFirst().get().getCategory();
            var subCategory = navHistory.stream().findFirst().get().getSubCategory();

            LOG.info("Calculating Rolling Returns for {}", schemeName);

            var navDateMap = navHistory.stream().collect(Collectors.toMap(MfNavRecord::getDate, MfNavRecord::getNav));

            var pctValues1year = new ArrayList<Double>();
            var pctValues3year = new ArrayList<Double>();
            var pctValues5year = new ArrayList<Double>();
            var pctValues10year = new ArrayList<Double>();

            List<MfRollingReturns> mfRollingReturnsList = new ArrayList<>();
            navHistory.forEach(record -> {

                if (NumberUtils.isParsable(record.getNav())) {

                    var mfRollingReturns = new MfRollingReturns();
                    mfRollingReturns.setDate(record.getDate());
                    mfRollingReturns.setSchemeCode(record.getSchemeCode());
                    var price = new BigDecimal(record.getNav());

                    if (price.compareTo(BigDecimal.ZERO) == 0) {
                        mfRollingReturns.setOneYearReturn(BigDecimal.ZERO);
                        pctValues1year.add(BigDecimal.ZERO.doubleValue());

                        mfRollingReturns.setThreeYearReturn(BigDecimal.ZERO);
                        pctValues3year.add(BigDecimal.ZERO.doubleValue());

                        mfRollingReturns.setFiveYearReturn(BigDecimal.ZERO);
                        pctValues5year.add(BigDecimal.ZERO.doubleValue());

                        mfRollingReturns.setTenYearReturn(BigDecimal.ZERO);
                        pctValues10year.add(BigDecimal.ZERO.doubleValue());
                    } else {
                        var navDate = record.getDate();

                        var oneYearDate = localDate(navDate, navDateMap, 1L);
                        var threeYearDate = localDate(navDate, navDateMap, 3L);
                        var fiveYearDate = localDate(navDate, navDateMap, 5L);
                        var tenYearDate = localDate(navDate, navDateMap, 10L);


                        if (navDateMap.containsKey(oneYearDate)) {
                            var oldPrice = new BigDecimal(navDateMap.get(oneYearDate));
                            if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                                mfRollingReturns.setOneYearReturn(BigDecimal.ZERO);
                                pctValues1year.add(BigDecimal.ZERO.doubleValue());
                            } else {
                                // LOG.info("1 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                                var pct = ((price.doubleValue() - oldPrice.doubleValue()) / price.doubleValue()) * 100;
                                mfRollingReturns.setOneYearReturn(new BigDecimal(pct));
                                pctValues1year.add(pct);
                            }
                        }

                        if (navDateMap.containsKey(threeYearDate)) {
                            var oldPrice = new BigDecimal(navDateMap.get(threeYearDate));
                            if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                                mfRollingReturns.setThreeYearReturn(BigDecimal.ZERO);
                                pctValues3year.add(BigDecimal.ZERO.doubleValue());
                            } else {
                                // LOG.info("3 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                                var cagr = calculateCagr(price, oldPrice, 3);
                                mfRollingReturns.setThreeYearReturn(cagr);
                                pctValues3year.add(cagr.doubleValue());
                            }
                        }

                        if (navDateMap.containsKey(fiveYearDate)) {
                            var oldPrice = new BigDecimal(navDateMap.get(fiveYearDate));
                            if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {

                                mfRollingReturns.setFiveYearReturn(BigDecimal.ZERO);
                                pctValues5year.add(BigDecimal.ZERO.doubleValue());
                            } else {
                                //LOG.info("5 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                                var cagr = calculateCagr(price, oldPrice, 5);
                                mfRollingReturns.setFiveYearReturn(cagr);
                                pctValues5year.add(cagr.doubleValue());
                            }
                        }

                        if (navDateMap.containsKey(tenYearDate)) {
                            var oldPrice = new BigDecimal(navDateMap.get(tenYearDate));
                            if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                                mfRollingReturns.setTenYearReturn(BigDecimal.ZERO);
                                pctValues10year.add(BigDecimal.ZERO.doubleValue());
                            } else {
                                //LOG.info("10 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                                var cagr = calculateCagr(price, oldPrice, 10);
                                mfRollingReturns.setTenYearReturn(cagr);
                                pctValues10year.add(cagr.doubleValue());
                            }
                        }
                    }

                    mfRollingReturnsList.add(mfRollingReturns);
                }
            });

            //mfRollingReturnsRepository.save(mfRollingReturns)
            LOG.info("Calculating Stats for {}", schemeName);

            var yearMap = Map.of(1, pctValues1year, 3, pctValues3year, 5, pctValues5year, 10, pctValues10year);
            for (Map.Entry<Integer, ArrayList<Double>> entry : yearMap.entrySet()) {

                var year = entry.getKey();
                var pctValues = entry.getValue();

                var accumYear = new StatsAccumulator();

                if (!pctValues.isEmpty()) {

                    accumYear.addAll(pctValues);
                    var mfReturnStats = new MfReturnStats();
                    mfReturnStats.setSchemeCode(schemeCode);
                    mfReturnStats.setSchemeName(schemeName);
                    mfReturnStats.setCategory(category);
                    mfReturnStats.setSubCategory(subCategory);
                    mfReturnStats.setYear(Long.valueOf(year));
                    mfReturnStats.setStandardDeviation(BigDecimal.valueOf(accumYear.populationStandardDeviation()));
                    mfReturnStats.setMean(BigDecimal.valueOf(accumYear.mean()));
                    mfReturnStats.setPercentile90(BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues)));
                    mfReturnStats.setPercentile95(BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues)));
                    setFrequencyDistribution(mfReturnStats, pctValues);
                    mfReturnStatsRepository.save(mfReturnStats);
                }
            }
        }


    }

    private void setFrequencyDistribution(MfReturnStats indexReturnStats, ArrayList<Double> pctValues) {

        int negative = 0;
        int count5 = 0;
        int count10 = 0;
        int count15 = 0;
        int count20 = 0;
        int count25 = 0;


        for (Double value : pctValues) {

            if (value < 0) {
                negative++;
            } else if (value <= 5) {
                count5++;
            } else if (value <= 10) {
                count10++;
            } else if (value <= 15) {
                count15++;
            } else if (value <= 20) {
                count20++;
            } else {
                count25++;
            }
        }

        int total = negative + count5 + count10 + count15 + count20 + count25;

        if (total > 0) {
            indexReturnStats.setNegative(getValue(negative, total));
            indexReturnStats.setCount5(getValue(count5, total));
            indexReturnStats.setCount10(getValue(count10, total));
            indexReturnStats.setCount15(getValue(count15, total));
            indexReturnStats.setCount20(getValue(count20, total));
            indexReturnStats.setCount25Plus(getValue(count25, total));
            indexReturnStats.setTotalCount(BigDecimal.valueOf(total));
        }
    }

    private BigDecimal getValue(int count, int total) {
        BigDecimal countOfEntries = BigDecimal.valueOf(count);
        BigDecimal totalCount = BigDecimal.valueOf(total);
        BigDecimal percentage = countOfEntries
                .divide(totalCount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return percentage.setScale(2, RoundingMode.HALF_UP);
    }
}
