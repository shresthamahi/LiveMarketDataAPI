package com.riskshield.event;

import com.riskshield.model.MarketQuote;

import java.time.Instant;
import java.util.UUID;

public record RiskBreachEvent(
        String eventId,
        String symbol,
        double currentprice,
        double preiousclose,

        double percentChange,
        double estimateVarImpact,
        String breachReason,
        Instant timestamp

) {

    public static RiskBreachEvent createEvent(
           MarketQuote quote,
           double percentChange,
           double estimateVarImpact,
           String breachReason    )
    {
        return new RiskBreachEvent(
                UUID.randomUUID().toString(),
                quote.symbol(),
                quote.currentPrice(),
                quote.previousClose(),
                percentChange,
                estimateVarImpact,
                breachReason,
                Instant.now()

        );
    }
}
