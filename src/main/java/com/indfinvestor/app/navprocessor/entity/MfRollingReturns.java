package com.indfinvestor.app.navprocessor.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "MF_ROLLING_RETURNS")
public class MfRollingReturns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SCHEME_NAME", nullable = false)
    private String schemeName;

    @Column(name = "SCHEME_CODE", nullable = false)
    private String schemeCode;

    @Column(name = "1_YEAR_RETURN", precision = 10, scale = 2)
    private BigDecimal oneYearReturn;

    @Column(name = "3_YEAR_RETURN", precision = 10, scale = 2)
    private BigDecimal threeYearReturn;

    @Column(name = "5_YEAR_RETURN", precision = 10, scale = 2)
    private BigDecimal fiveYearReturn;

    @Column(name = "10_YEAR_RETURN", precision = 10, scale = 2)
    private BigDecimal tenYearReturn;

    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String name) {
        this.schemeName = name;
    }

    public BigDecimal getOneYearReturn() {
        return oneYearReturn;
    }

    public void setOneYearReturn(BigDecimal oneYearReturn) {
        this.oneYearReturn = oneYearReturn;
    }

    public BigDecimal getThreeYearReturn() {
        return threeYearReturn;
    }

    public void setThreeYearReturn(BigDecimal threeYearReturn) {
        this.threeYearReturn = threeYearReturn;
    }

    public BigDecimal getFiveYearReturn() {
        return fiveYearReturn;
    }

    public void setFiveYearReturn(BigDecimal fiveYearReturn) {
        this.fiveYearReturn = fiveYearReturn;
    }

    public BigDecimal getTenYearReturn() {
        return tenYearReturn;
    }

    public void setTenYearReturn(BigDecimal tenYearReturn) {
        this.tenYearReturn = tenYearReturn;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }
}
