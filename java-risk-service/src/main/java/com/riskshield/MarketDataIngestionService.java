package com.riskshield;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class MarketDataIngestionService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    private final String apiKey="dbigkipr01qie36ocpg0dbigkipr01qie36ocpgg";
    private final String topicName="risk-breach-events";



    public MarketDataIngestionService(KafkaTemplate<String, String> kafkaTemplate, RestTemplate restTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 10000)
    public void fetchAndStreamMarketData(){
        String url="https://finnhub.io"+apiKey;

        try {
            Map<String,Object> response =restTemplate.getForObject(url,Map.class);
            if(response !=null && response.containsKey("c")){
                double currentPrice=Double.parseDouble(response.get("c").toString());
                double previousClose= Double.parseDouble(response.get("pc").toString());

                double percentChange=((currentPrice-previousClose)/previousClose)*100;

                String jsonPayload = String.format("{\"ticker\":\"AAPL\",\"currentPrice\":%.2f,\"percentChange\":%.2f}",currentPrice,percentChange);

                System.out.println("Java Core Ingestion: Streaming event to Redpanda --->"+jsonPayload);
                kafkaTemplate.send(topicName,jsonPayload);

            }

        }catch(Exception e)
        {
            System.out.println("Failed to pull market data!!"+e.getMessage());
        }
    }

}
