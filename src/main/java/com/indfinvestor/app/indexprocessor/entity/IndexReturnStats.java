package com.indfinvestor.app.indexprocessor.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "INDEX_RETURN_STATS")
public class IndexReturnStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "1_YEAR_STD", precision = 10, scale = 2)
    private BigDecimal oneYearStd;

    @Column(name = "1_YEAR_MEAN", precision = 10, scale = 2)
    private BigDecimal oneYearMean;

    @Column(name = "1_YEAR_90", precision = 10, scale = 2)
    private BigDecimal oneYear90;


    @Column(name = "1_YEAR_95", precision = 10, scale = 2)
    private BigDecimal oneYear95;

    @Column(name = "3_YEAR_STD", precision = 10, scale = 2)
    private BigDecimal threeYearStd;

    @Column(name = "3_YEAR_MEAN", precision = 10, scale = 2)
    private BigDecimal threeYearMean;

    @Column(name = "3_YEAR_90", precision = 10, scale = 2)
    private BigDecimal threeYear90;

    @Column(name = "3_YEAR_95", precision = 10, scale = 2)
    private BigDecimal threeYear95;

    @Column(name = "5_YEAR_STD", precision = 10, scale = 2)
    private BigDecimal fiveYearStd;

    @Column(name = "5_YEAR_MEAN", precision = 10, scale = 2)
    private BigDecimal fiveYearMean;

    @Column(name = "5_YEAR_90", precision = 10, scale = 2)
    private BigDecimal fiveYear90;
    @Column(name = "5_YEAR_95", precision = 10, scale = 2)
    private BigDecimal fiveYear95;

    @Column(name = "10_YEAR_STD", precision = 10, scale = 2)
    private BigDecimal tenYearStd;

    @Column(name = "10_YEAR_MEAN", precision = 10, scale = 2)
    private BigDecimal tenYearMean;

    @Column(name = "10_YEAR_90", precision = 10, scale = 2)
    private BigDecimal tenYear90;

    @Column(name = "10_YEAR_95", precision = 10, scale = 2)
    private BigDecimal tenYear95;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOneYearStd() {
        return oneYearStd;
    }

    public void setOneYearStd(BigDecimal oneYearStd) {
        this.oneYearStd = oneYearStd;
    }

    public BigDecimal getOneYearMean() {
        return oneYearMean;
    }

    public void setOneYearMean(BigDecimal oneYearMean) {
        this.oneYearMean = oneYearMean;
    }

    public BigDecimal getOneYear90() {
        return oneYear90;
    }

    public void setOneYear90(BigDecimal oneYear90) {
        this.oneYear90 = oneYear90;
    }

    public BigDecimal getOneYear95() {
        return oneYear95;
    }

    public void setOneYear95(BigDecimal oneYear95) {
        this.oneYear95 = oneYear95;
    }

    public BigDecimal getThreeYearStd() {
        return threeYearStd;
    }

    public void setThreeYearStd(BigDecimal threeYearStd) {
        this.threeYearStd = threeYearStd;
    }

    public BigDecimal getThreeYearMean() {
        return threeYearMean;
    }

    public void setThreeYearMean(BigDecimal threeYearMean) {
        this.threeYearMean = threeYearMean;
    }

    public BigDecimal getThreeYear90() {
        return threeYear90;
    }

    public void setThreeYear90(BigDecimal threeYear90) {
        this.threeYear90 = threeYear90;
    }

    public BigDecimal getThreeYear95() {
        return threeYear95;
    }

    public void setThreeYear95(BigDecimal threeYear95) {
        this.threeYear95 = threeYear95;
    }

    public BigDecimal getFiveYearStd() {
        return fiveYearStd;
    }

    public void setFiveYearStd(BigDecimal fiveYearStd) {
        this.fiveYearStd = fiveYearStd;
    }

    public BigDecimal getFiveYearMean() {
        return fiveYearMean;
    }

    public void setFiveYearMean(BigDecimal fiveYearMean) {
        this.fiveYearMean = fiveYearMean;
    }

    public BigDecimal getFiveYear90() {
        return fiveYear90;
    }

    public void setFiveYear90(BigDecimal fiveYear90) {
        this.fiveYear90 = fiveYear90;
    }

    public BigDecimal getFiveYear95() {
        return fiveYear95;
    }

    public void setFiveYear95(BigDecimal fiveYear95) {
        this.fiveYear95 = fiveYear95;
    }

    public BigDecimal getTenYearStd() {
        return tenYearStd;
    }

    public void setTenYearStd(BigDecimal tenYearStd) {
        this.tenYearStd = tenYearStd;
    }

    public BigDecimal getTenYearMean() {
        return tenYearMean;
    }

    public void setTenYearMean(BigDecimal tenYearMean) {
        this.tenYearMean = tenYearMean;
    }

    public BigDecimal getTenYear90() {
        return tenYear90;
    }

    public void setTenYear90(BigDecimal tenYear90) {
        this.tenYear90 = tenYear90;
    }

    public BigDecimal getTenYear95() {
        return tenYear95;
    }

    public void setTenYear95(BigDecimal tenYear95) {
        this.tenYear95 = tenYear95;
    }
}
