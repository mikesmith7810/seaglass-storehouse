package com.mike.stagerhub;

import io.quarkus.runtime.annotations.QuarkusMain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StagerHubApplicationTests {

    @Test
    void applicationClassIsAnnotatedWithQuarkusMain() {
        assertThat(StagerHubApplication.class.isAnnotationPresent(QuarkusMain.class)).isTrue();
    }
}
