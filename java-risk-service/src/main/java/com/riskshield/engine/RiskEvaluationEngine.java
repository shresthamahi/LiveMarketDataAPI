package com.riskshield.engine;

import com.riskshield.event.RiskBreachEvent;
import com.riskshield.model.MarketQuote;

import java.util.Optional;

public class RiskEvaluationEngine {

    private static final double POSITION_NOTIONAL_GBP=100_000.00;
    private static final double MAX_DAILY_VAR_LIMIT_USD = 2_500.00;
    private static final double INTRADAY_STRESS_VOLALITY_THRESHOLD= 4.00;

    public Optional<RiskBreachEvent> evaluate(MarketQuote quote)
    {
        if(quote==null || quote.previousClose() <=0)
        {
            return Optional.empty();
        }

        //percent change from previous close
        double percentChange= (quote.currentPrice()-quote.previousClose())/quote.previousClose()*100;
        //calculate percent and loss on our asset
        double postionPnL= POSITION_NOTIONAL_GBP * (percentChange/100);
        //calculating intraday high-low volality
        double intradaySwingpercent= (quote.highPrice()- quote.lowPrice()/quote.previousClose())*100;


        /*
        * Checking if there is breach in Var
        * */
        if(postionPnL < -MAX_DAILY_VAR_LIMIT_USD)
        {
            double estimatedBreachAmount =Math.abs(postionPnL);
            String reason= "Var Limit Breached: Unrealized loss of "+estimatedBreachAmount+ " exceeds breach daily limit of "+ MAX_DAILY_VAR_LIMIT_USD+ " equal to "+percentChange+" % drop";

            return Optional.of(RiskBreachEvent.createEvent(quote,percentChange,estimatedBreachAmount, reason));
        }
        /*
             Measuring how sharply asset is moving relative to its previous close; Intraday sensitivity
         */
        if(intradaySwingpercent>INTRADAY_STRESS_VOLALITY_THRESHOLD)
        {
            double stressedExposure= POSITION_NOTIONAL_GBP*(intradaySwingpercent/100); //estimated var impact
            String reason= " Stress volatility Alert: Intraday swing of "+intradaySwingpercent+" crossed stress threshold of:"+INTRADAY_STRESS_VOLALITY_THRESHOLD;
            return Optional.of(RiskBreachEvent.createEvent(quote,percentChange,stressedExposure,reason));
        }

        return Optional.empty();
    }
}
