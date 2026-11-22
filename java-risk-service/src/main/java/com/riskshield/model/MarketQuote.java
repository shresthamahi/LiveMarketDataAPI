package com.riskshield.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


public record MarketQuote(
        String symbol,
        double currentPrice,
         double previousClose,
        double highPrice,
         double lowPrice

) {


}
