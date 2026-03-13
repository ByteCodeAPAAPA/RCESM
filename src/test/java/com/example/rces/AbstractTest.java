package com.example.rces;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final ThreadLocal<Integer> stepCounter = ThreadLocal.withInitial(() -> 0);
    private final ThreadLocal<String> currentStep = new ThreadLocal<>();

    @BeforeEach
    void setUpTest() {
        stepCounter.set(0);
        currentStep.remove();
    }

    @AfterEach
    void tearDownTest(TestInfo testInfo) {
        if (stepCounter.get() > 0) {
            log.info("✅ Тест '{}' выполнен за {} шагов",
                    testInfo.getDisplayName(),
                    stepCounter.get()
            );
        }
    }

    protected void step(String stepDescription) {
        int stepNumber = stepCounter.get() + 1;
        stepCounter.set(stepNumber);
        currentStep.set(stepDescription);
        log.info("=== Шаг {}: {} ===", stepNumber, stepDescription);
    }

    protected void stepFailed(Throwable error) {
        log.error("❌ Ошибка на шаге {}: {}",
                stepCounter.get(),
                currentStep.get(),
                error
        );
    }

}
