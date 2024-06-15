package com.indfinvestor.app.indexprocessor.service;

import com.google.common.math.Quantiles;
import com.google.common.math.StatsAccumulator;
import com.indfinvestor.app.indexprocessor.entity.IndexReturnStats;
import com.indfinvestor.app.indexprocessor.entity.IndexRollingReturns;
import com.indfinvestor.app.indexprocessor.model.IndexData;
import com.indfinvestor.app.indexprocessor.repository.IndexReturnStatsRepository;
import com.indfinvestor.app.indexprocessor.repository.IndexRollingReturnsRepository;
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
public class RollingReturnProcessor {

    private static Logger LOG = LoggerFactory.getLogger(RollingReturnProcessor.class);

    private final IndexRollingReturnsRepository indexRollingReturnsRepository;
    private final IndexReturnStatsRepository indexReturnStatsRepository;

    public RollingReturnProcessor(IndexRollingReturnsRepository indexRollingReturnsRepository, IndexReturnStatsRepository indexReturnStatsRepository) {
        this.indexRollingReturnsRepository = indexRollingReturnsRepository;
        this.indexReturnStatsRepository = indexReturnStatsRepository;
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
     * @param indexDataRecords List of IndexData records containing the index name, date, and closing price
     */
    public void calculateRollingReturns(List<IndexData> indexDataRecords) {

        var navDateMap = indexDataRecords.stream().collect(Collectors.toMap(IndexData::date, IndexData::close));

        var pctValues1year = new ArrayList<Double>();
        var pctValues3year = new ArrayList<Double>();
        var pctValues5year = new ArrayList<Double>();
        var pctValues10year = new ArrayList<Double>();


        String indexName = indexDataRecords.stream().findFirst().get().name();
        LOG.info("Processing Index Data {}", indexName);
        List<IndexRollingReturns> indexRollingReturnsList = new ArrayList<>();
        indexDataRecords.forEach(indexData -> {
            //  LOG.info("indexData {}", indexData);
            var price = new BigDecimal(indexData.close());

            IndexRollingReturns indexRollingReturns = new IndexRollingReturns();
            indexRollingReturns.setName(indexData.name());
            indexRollingReturns.setDate(indexData.date());

            if (price.compareTo(BigDecimal.ZERO) == 0) {
                indexRollingReturns.setOneYearReturn(BigDecimal.ZERO);
                pctValues1year.add(BigDecimal.ZERO.doubleValue());

                indexRollingReturns.setThreeYearReturn(BigDecimal.ZERO);
                pctValues3year.add(BigDecimal.ZERO.doubleValue());

                indexRollingReturns.setFiveYearReturn(BigDecimal.ZERO);
                pctValues5year.add(BigDecimal.ZERO.doubleValue());

                indexRollingReturns.setTenYearReturn(BigDecimal.ZERO);
                pctValues10year.add(BigDecimal.ZERO.doubleValue());
            } else {
                var navDate = indexData.date();

                var oneYearDate = localDate(navDate, navDateMap, 1L);
                var threeYearDate = localDate(navDate, navDateMap, 3L);
                var fiveYearDate = localDate(navDate, navDateMap, 5L);
                var tenYearDate = localDate(navDate, navDateMap, 10L);

                if (navDateMap.containsKey(oneYearDate)) {
                    var oldPrice = new BigDecimal(navDateMap.get(oneYearDate));
                    if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                        indexRollingReturns.setOneYearReturn(BigDecimal.ZERO);
                        pctValues1year.add(BigDecimal.ZERO.doubleValue());
                    } else {
                        // LOG.info("1 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        var pct = ((price.doubleValue() - oldPrice.doubleValue()) / price.doubleValue()) * 100;
                        indexRollingReturns.setOneYearReturn(new BigDecimal(pct));
                        pctValues1year.add(pct);
                    }
                }

                if (navDateMap.containsKey(threeYearDate)) {
                    var oldPrice = new BigDecimal(navDateMap.get(threeYearDate));
                    if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                        indexRollingReturns.setThreeYearReturn(BigDecimal.ZERO);
                        pctValues3year.add(BigDecimal.ZERO.doubleValue());
                    } else {
                        // LOG.info("3 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        var cagr = calculateCagr(price, oldPrice, 3);
                        indexRollingReturns.setThreeYearReturn(cagr);
                        pctValues3year.add(cagr.doubleValue());
                    }
                }

                if (navDateMap.containsKey(fiveYearDate)) {
                    var oldPrice = new BigDecimal(navDateMap.get(fiveYearDate));
                    if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {

                        indexRollingReturns.setFiveYearReturn(BigDecimal.ZERO);
                        pctValues5year.add(BigDecimal.ZERO.doubleValue());
                    } else {
                        //LOG.info("5 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        var cagr = calculateCagr(price, oldPrice, 5);
                        indexRollingReturns.setFiveYearReturn(cagr);
                        pctValues5year.add(cagr.doubleValue());
                    }
                }

                if (navDateMap.containsKey(tenYearDate)) {
                    var oldPrice = new BigDecimal(navDateMap.get(tenYearDate));
                    if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
                        indexRollingReturns.setTenYearReturn(BigDecimal.ZERO);
                        pctValues10year.add(BigDecimal.ZERO.doubleValue());
                    } else {
                        //LOG.info("10 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        var cagr = calculateCagr(price, oldPrice, 10);
                        indexRollingReturns.setTenYearReturn(cagr);
                        pctValues10year.add(cagr.doubleValue());
                    }
                }
            }

            indexRollingReturnsList.add(indexRollingReturns);

        });

        //indexRollingReturnsRepository.saveAll(indexRollingReturnsList);

        var yearMap = Map.of(1, pctValues1year, 3, pctValues3year, 5, pctValues5year, 10, pctValues10year);
        for (Map.Entry<Integer, ArrayList<Double>> entry : yearMap.entrySet()) {

            var year = entry.getKey();
            var pctValues = entry.getValue();

            var accumYear = new StatsAccumulator();

            if (!pctValues.isEmpty()) {

                accumYear.addAll(pctValues);
                var indexReturnStats = new IndexReturnStats();
                indexReturnStats.setName(indexName);
                indexReturnStats.setYear(Long.valueOf(year));
                indexReturnStats.setStandardDeviation(BigDecimal.valueOf(accumYear.populationStandardDeviation()));
                indexReturnStats.setMean(BigDecimal.valueOf(accumYear.mean()));
                indexReturnStats.setPercentile90(BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues)));
                indexReturnStats.setPercentile95(BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues)));
                setFrequencyDistribution(indexReturnStats, pctValues);
                indexReturnStatsRepository.save(indexReturnStats);
            }
        }
    }

    private void setFrequencyDistribution(IndexReturnStats indexReturnStats, ArrayList<Double> pctValues) {

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
