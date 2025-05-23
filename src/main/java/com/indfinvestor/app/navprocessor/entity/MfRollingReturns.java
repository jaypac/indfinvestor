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

    @Column(name = "YEAR_NOS", nullable = false)
    private Long year;

    @Column(name = "CAGR_RETURN", precision = 10, scale = 2)
    private BigDecimal cagrReturn;

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

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public BigDecimal getCagrReturn() {
        return cagrReturn;
    }

    public void setCagrReturn(BigDecimal cagrReturn) {
        this.cagrReturn = cagrReturn;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
