package com.indfinvestor.app.indexprocessor.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INDEX_ROLLING_RETURNS")
public class IndexRollingReturns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
