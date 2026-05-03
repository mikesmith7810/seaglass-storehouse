package com.mike.seaglassstorehouse;

import io.quarkus.runtime.annotations.QuarkusMain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeaglassStorehouseApplicationTests {

    @Test
    void applicationClassIsAnnotatedWithQuarkusMain() {
        assertThat(SeaglassStorehouseApplication.class.isAnnotationPresent(QuarkusMain.class)).isTrue();
    }
}
