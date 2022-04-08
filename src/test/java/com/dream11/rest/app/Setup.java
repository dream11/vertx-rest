package com.dream11.rest.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class Setup implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {
    public static final String AEROSPIKE_IMAGE = System.getProperty(Constants.AEROSPIKE_IMAGE_KEY, Constants.DEFAULT_AEROSPIKE_IMAGE);
    private static boolean started = false;
    private GenericContainer aerospikeContainer;

    @Override
    public void afterAll(ExtensionContext extensionContext) {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws IOException, InterruptedException {
        if (!started) {
            started = true;
            aerospikeContainer = new GenericContainer<>(AEROSPIKE_IMAGE);
            log.info("Starting aerospike container from image:{}", AEROSPIKE_IMAGE);
            aerospikeContainer
                    .withEnv(Constants.NAMESPACE, Constants.TEST_NAMESPACE)
                    .withExposedPorts(3000)
                    .withStartupTimeout(Duration.ofSeconds(1000))
                    .withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(10)))
                    .addFileSystemBind(Constants.INIT_DATA_PATH, Constants.INIT_DATA_PATH_IN_CONTAINER, BindMode.READ_ONLY);
            aerospikeContainer.start();
            System.setProperty(Constants.AEROSPIKE_HOST, aerospikeContainer.getHost());
            System.setProperty(Constants.AEROSPIKE_PORT, String.valueOf(aerospikeContainer.getFirstMappedPort()));
        }
    }

    @Override
    public void close() {
        aerospikeContainer.close();
    }
}
