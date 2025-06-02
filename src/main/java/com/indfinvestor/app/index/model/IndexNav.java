package com.indfinvestor.app.index.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "INDEX_NAV")
public class IndexNav {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "NAV_DATE", nullable = false)
    private LocalDate navDate;

    @NaturalId
    @Column(name = "NET_ASSET_VALUE", nullable = false)
    private BigDecimal netAssetValue;

    @ManyToOne
    @JoinColumn(name = "INDEX_ID", nullable = false)
    private IndexDetails indexDetails;
}
