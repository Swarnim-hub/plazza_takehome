package com.plazza.ridehailing.service.matching;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class DriverMatchingStrategyRegistry {

    private final Map<MatchingStrategyType, DriverMatchingStrategy> strategies = new EnumMap<>(MatchingStrategyType.class);
    private final AtomicReference<MatchingStrategyType> defaultStrategyType = new AtomicReference<>(MatchingStrategyType.NEAREST);

    public DriverMatchingStrategyRegistry(List<DriverMatchingStrategy> strategyList) {
        for (DriverMatchingStrategy strategy : strategyList) {
            strategies.put(strategy.getType(), strategy);
        }
    }

    public DriverMatchingStrategy getStrategy(MatchingStrategyType type) {
        MatchingStrategyType targetType = (type != null) ? type : defaultStrategyType.get();
        return strategies.getOrDefault(targetType, strategies.get(MatchingStrategyType.NEAREST));
    }

    public void setDefaultStrategy(MatchingStrategyType type) {
        if (type != null && strategies.containsKey(type)) {
            defaultStrategyType.set(type);
        }
    }

    public MatchingStrategyType getDefaultStrategyType() {
        return defaultStrategyType.get();
    }
}
