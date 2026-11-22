package com.riskshield.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.riskshield.model.MarketQuote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class FinnhubClient {

    private  RestTemplate restTemplate;
    private  String apiKey;
    private String baseurl;

    public FinnhubClient(
            @Value("${finnhub.api.key}") String apiKey,
            @Value("${finnhub.api.base-url}") String baseurl) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.baseurl = baseurl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FinnhubQuoteResponse(
            @JsonProperty("c") double currentPrice,
            @JsonProperty("pc") double previousClose,
            @JsonProperty("h") double highPrice,
            @JsonProperty("l") double lowPrice
    )
    { }

    public Optional<MarketQuote> fetchQuote(String symbol){
        String url= String.format("%s/quote?symbol=%s&token=%s", baseurl,symbol,apiKey);
        try{
            FinnhubQuoteResponse response=restTemplate.getForObject(url, FinnhubQuoteResponse.class);

            if(response==null || response.currentPrice<=0){
                return Optional.empty();
            }

            return Optional.of(new MarketQuote(
                    symbol,
                    response.currentPrice(),
                    response.highPrice(),
                    response.previousClose(),
                    response.lowPrice()

            ));
        }
        catch(RestClientException e)
        {
            System.err.println("Error in fetching info symbol:"+symbol+" ::"+e.getMessage());
            return Optional.empty();
        }
    }



}
