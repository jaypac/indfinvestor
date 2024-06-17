package com.indfinvestor.app.navprocessor.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "MF_RETURN_STATS")
public class MfReturnStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SCHEME_NAME", nullable = false)
    private String schemeName;

    @Column(name = "SCHEME_CODE", nullable = false)
    private String schemeCode;

    @Column(name = "CATEGORY", nullable = false)
    private String category;

    @Column(name = "SUB_CATEGORY", nullable = false)
    private String subCategory;

    @Column(name = "YEAR_NOS", nullable = false)
    private Long year;

    @Column(name = "STD_DEV", precision = 10, scale = 2)
    private BigDecimal standardDeviation;

    @Column(name = "MEAN", precision = 10, scale = 2)
    private BigDecimal mean;

    @Column(name = "PERCENTILE_90", precision = 10, scale = 2)
    private BigDecimal percentile90;

    @Column(name = "PERCENTILE_95", precision = 10, scale = 2)
    private BigDecimal percentile95;

    @Column(name = "NEGATIVE_COUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal negative;

    @Column(name = "COUNT_5", nullable = false, precision = 10, scale = 2)
    private BigDecimal count5;

    @Column(name = "COUNT_10", nullable = false, precision = 10, scale = 2)
    private BigDecimal count10;

    @Column(name = "COUNT_15", nullable = false, precision = 10, scale = 2)
    private BigDecimal count15;

    @Column(name = "COUNT_20", nullable = false, precision = 10, scale = 2)
    private BigDecimal count20;

    @Column(name = "COUNT_25", nullable = false, precision = 10, scale = 2)
    private BigDecimal count25Plus;

    @Column(name = "TOTAL_COUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(BigDecimal standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public BigDecimal getMean() {
        return mean;
    }

    public void setMean(BigDecimal mean) {
        this.mean = mean;
    }

    public BigDecimal getPercentile90() {
        return percentile90;
    }

    public void setPercentile90(BigDecimal percentile90) {
        this.percentile90 = percentile90;
    }

    public BigDecimal getPercentile95() {
        return percentile95;
    }

    public void setPercentile95(BigDecimal percentile95) {
        this.percentile95 = percentile95;
    }

    public BigDecimal getNegative() {
        return negative;
    }

    public void setNegative(BigDecimal negative) {
        this.negative = negative;
    }

    public BigDecimal getCount5() {
        return count5;
    }

    public void setCount5(BigDecimal count5) {
        this.count5 = count5;
    }

    public BigDecimal getCount10() {
        return count10;
    }

    public void setCount10(BigDecimal count10) {
        this.count10 = count10;
    }

    public BigDecimal getCount15() {
        return count15;
    }

    public void setCount15(BigDecimal count15) {
        this.count15 = count15;
    }

    public BigDecimal getCount20() {
        return count20;
    }

    public void setCount20(BigDecimal count20) {
        this.count20 = count20;
    }

    public BigDecimal getCount25Plus() {
        return count25Plus;
    }

    public void setCount25Plus(BigDecimal count25Plus) {
        this.count25Plus = count25Plus;
    }

    public BigDecimal getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigDecimal totalCount) {
        this.totalCount = totalCount;
    }
}

