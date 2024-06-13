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

    private Logger LOG = LoggerFactory.getLogger(RollingReturnProcessor.class);

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

        var accum1Year = new StatsAccumulator();
        var accum3Year = new StatsAccumulator();
        var accum5Year = new StatsAccumulator();
        var accum10Year = new StatsAccumulator();

        var pctValues1year = new ArrayList<Double>();
        var pctValues3year = new ArrayList<Double>();
        var pctValues5year = new ArrayList<Double>();
        var pctValues10year = new ArrayList<Double>();


        indexDataRecords.forEach(indexData -> {
            LOG.info("indexData {}", indexData);
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
                        LOG.info("1 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        //println("1 ${oneYearDate} oldPrice: ${navDateMap.get(oneYearDate)} currentPrice: ${price.toDouble()}")
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
                        LOG.info("3 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        //println("3 ${threeYearDate} oldPrice: ${navDateMap.get(threeYearDate)} currentPrice: ${price.toDouble()}")
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
                        LOG.info("5 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        // println("5 ${fiveYearDate} oldPrice: ${navDateMap.get(fiveYearDate)} currentPrice: ${price.toDouble()}")
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
                        LOG.info("10 {} oldPrice: {} currentPrice: {}", oneYearDate, oldPrice, price.doubleValue());
                        //println("10 ${tenYearDate} oldPrice: ${navDateMap.get(tenYearDate)} currentPrice: ${price.toDouble()}")
                        var cagr = calculateCagr(price, oldPrice, 10);
                        indexRollingReturns.setTenYearReturn(cagr);
                        pctValues10year.add(cagr.doubleValue());
                    }
                }
            }

            indexRollingReturnsRepository.save(indexRollingReturns);

        });


        LOG.info("pctValues1year {}", pctValues1year);
        LOG.info("pctValues3year {}", pctValues3year);
        LOG.info("pctValues1year {}", pctValues5year);
        LOG.info("pctValues1year {}", pctValues10year);

        accum1Year.addAll(pctValues1year);
        accum3Year.addAll(pctValues3year);
        accum5Year.addAll(pctValues5year);
        accum10Year.addAll(pctValues10year);


        var indexReturnStats = new IndexReturnStats();
        indexReturnStats.setName("NIFTY 50");

        indexReturnStats.setOneYearStd(BigDecimal.valueOf(accum1Year.populationStandardDeviation()));
        indexReturnStats.setThreeYearStd(BigDecimal.valueOf(accum3Year.populationStandardDeviation()));
        indexReturnStats.setFiveYearStd(BigDecimal.valueOf(accum5Year.populationStandardDeviation()));
        indexReturnStats.setTenYearStd(BigDecimal.valueOf(accum10Year.populationStandardDeviation()));

        indexReturnStats.setOneYearMean(BigDecimal.valueOf(accum1Year.mean()));
        indexReturnStats.setThreeYearMean(BigDecimal.valueOf(accum3Year.mean()));
        indexReturnStats.setFiveYearMean(BigDecimal.valueOf(accum5Year.mean()));
        indexReturnStats.setTenYearMean(BigDecimal.valueOf(accum10Year.mean()));

        indexReturnStats.setOneYear90(BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues1year)));
        indexReturnStats.setThreeYear90(BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues3year)));
        indexReturnStats.setFiveYear90(BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues5year)));
        indexReturnStats.setTenYear90(BigDecimal.valueOf(Quantiles.percentiles().index(90).compute(pctValues10year)));

        indexReturnStats.setOneYear95(BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues1year)));
        indexReturnStats.setThreeYear95(BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues3year)));
        indexReturnStats.setFiveYear95(BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues5year)));
        indexReturnStats.setTenYear95(BigDecimal.valueOf(Quantiles.percentiles().index(95).compute(pctValues10year)));

        indexReturnStatsRepository.save(indexReturnStats);

    }
}
