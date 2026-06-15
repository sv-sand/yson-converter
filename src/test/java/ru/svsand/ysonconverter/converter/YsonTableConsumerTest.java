package ru.svsand.ysonconverter.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class YsonTableConsumerTest {

    private YsonTableConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new YsonTableConsumer();
    }

    @Test
    void onBeginMap_initializesEmptyMap() {
        consumer.onBeginMap();

        assertThat(consumer.getMap()).isNotNull().isEmpty();
    }

    @Test
    void onEndMap_clearsMap() {
        consumer.onBeginMap();

        consumer.onEndMap();

        assertThat(consumer.getMap()).isNull();
    }

    @Test
    void onInteger_storesValueInOpenMap() {
        // Arrange
        consumer.onBeginMap();
        consumer.onKeyedItem("count");

        // Act
        consumer.onInteger(42L);

        // Assert
        assertThat(consumer.getMap()).containsEntry("count", 42L);
    }

    @Test
    void onInteger_ignoredWhenMapNotOpen() {
        // Act
        consumer.onInteger(42L);

        // Assert
        assertThat(consumer.getMap()).isNull();
    }

    @Test
    void onUnsignedInteger_storesValueInOpenMap() {
        // Arrange
        consumer.onBeginMap();
        consumer.onKeyedItem("uid");

        // Act
        consumer.onUnsignedInteger(100L);

        // Assert
        assertThat(consumer.getMap()).containsEntry("uid", 100L);
    }

    @Test
    void onUnsignedInteger_ignoredWhenMapNotOpen() {
        // Act
        consumer.onUnsignedInteger(100L);

        // Assert
        assertThat(consumer.getMap()).isNull();
    }

    @Test
    void onBoolean_storesValueInOpenMap() {
        // Arrange
        consumer.onBeginMap();
        consumer.onKeyedItem("active");

        // Act
        consumer.onBoolean(true);

        // Assert
        assertThat(consumer.getMap()).containsEntry("active", true);
    }

    @Test
    void onBoolean_ignoredWhenMapNotOpen() {
        // Act
        consumer.onBoolean(true);

        // Assert
        assertThat(consumer.getMap()).isNull();
    }

    @Test
    void onDouble_storesValueInOpenMap() {
        // Arrange
        consumer.onBeginMap();
        consumer.onKeyedItem("ratio");

        // Act
        consumer.onDouble(3.14);

        // Assert
        assertThat(consumer.getMap()).containsEntry("ratio", 3.14);
    }

    @Test
    void onDouble_ignoredWhenMapNotOpen() {
        // Act
        consumer.onDouble(3.14);

        // Assert
        assertThat(consumer.getMap()).isNull();
    }

    @Test
    void onString_storesValueInOpenMap() {
        // Arrange
        consumer.onBeginMap();
        consumer.onKeyedItem("name");

        // Act
        consumer.onString("hello");

        // Assert
        assertThat(consumer.getMap()).containsEntry("name", "hello");
    }

    @Test
    void onString_ignoredWhenMapNotOpen() {
        // Act
        consumer.onString("hello");

        // Assert
        assertThat(consumer.getMap()).isNull();
    }

    @Test
    void onString_byteArray_decodesUtf8AndStoresValue() {
        // Arrange
        consumer.onBeginMap();
        consumer.onKeyedItem("lang");
        byte[] bytes = "java".getBytes(StandardCharsets.UTF_8);

        // Act
        consumer.onString(bytes, 0, bytes.length);

        // Assert
        assertThat(consumer.getMap()).containsEntry("lang", "java");
    }

    @Test
    void onKeyedItem_byteArray_decodesUtf8AndSetsCurrentKey() {
        // Arrange
        consumer.onBeginMap();
        byte[] keyBytes = "field".getBytes(StandardCharsets.UTF_8);

        // Act
        consumer.onKeyedItem(keyBytes, 0, keyBytes.length);
        consumer.onInteger(99L);

        // Assert
        assertThat(consumer.getMap()).containsEntry("field", 99L);
    }

    @Test
    void multipleFields_allStoredInMap() {
        // Arrange
        consumer.onBeginMap();

        // Act
        consumer.onKeyedItem("a");
        consumer.onInteger(1L);
        consumer.onKeyedItem("b");
        consumer.onString("text");
        consumer.onKeyedItem("c");
        consumer.onBoolean(false);

        // Assert
        assertThat(consumer.getMap())
                .containsEntry("a", 1L)
                .containsEntry("b", "text")
                .containsEntry("c", false);
    }
}
