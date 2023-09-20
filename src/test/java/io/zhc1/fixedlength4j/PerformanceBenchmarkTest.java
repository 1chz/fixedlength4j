package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

@DisplayName("Performance Benchmark Tests")
class PerformanceBenchmarkTest {

    @Test
    @DisplayName("Serialize and deserialize 10,000 records within acceptable time")
    void serializeAndDeserializeLargeDataset() {
        FixedLengthFormat<BenchmarkRecord> format = new FixedLengthFormat<>(BenchmarkRecord.class);

        List<BenchmarkRecord> records = new ArrayList<>(10_000);
        for (int i = 0; i < 10_000; i++) {
            BenchmarkRecord record = new BenchmarkRecord();
            record.setId(String.format("%010d", i));
            record.setName("TestName" + (i % 100));
            record.setAmount((i + 1) * 1000L);
            record.setActive(i % 2 == 0);
            records.add(record);
        }

        long startSerialize = System.nanoTime();
        ByteBuffer serialized = format.serialize(records);
        long serializeTime = System.nanoTime() - startSerialize;

        serialized.rewind();

        long startDeserialize = System.nanoTime();
        List<BenchmarkRecord> deserialized = format.deserialize(serialized);
        long deserializeTime = System.nanoTime() - startDeserialize;

        assertThat(deserialized).hasSize(10_000);
        assertThat(deserialized.get(0).getId()).isEqualTo("0000000000");
        assertThat(deserialized.get(9999).getId()).isEqualTo("0000009999");

        System.out.println("Serialize 10,000 records: " + (serializeTime / 1_000_000) + " ms");
        System.out.println("Deserialize 10,000 records: " + (deserializeTime / 1_000_000) + " ms");

        assertThat(serializeTime).isLessThan(5_000_000_000L);
        assertThat(deserializeTime).isLessThan(5_000_000_000L);
    }

    @Test
    @DisplayName("Verify FixedLengthFormat reuse is efficient due to cached metadata")
    void verifyMetadataCaching() {
        FixedLengthFormat<BenchmarkRecord> format = new FixedLengthFormat<>(BenchmarkRecord.class);

        BenchmarkRecord record = new BenchmarkRecord();
        record.setId("0000000001");
        record.setName("TestName");
        record.setAmount(1000L);
        record.setActive(true);

        format.serialize(record);

        long totalTime = 0;
        int iterations = 1000;

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            ByteBuffer buffer = format.serialize(record);
            buffer.rewind();
            format.deserialize(buffer);
            totalTime += System.nanoTime() - start;
        }

        double avgMicros = ((double) totalTime / iterations) / 1000.0;
        System.out.println("Average time per serialize+deserialize: " + avgMicros + " microseconds");

        assertThat(avgMicros).isLessThan(1000);
    }

    public static class BenchmarkRecord {
        @Fixed(order = 1, bytes = 10)
        private String id;

        @Fixed(order = 2, bytes = 20)
        private String name;

        @Fixed(order = 3, bytes = 15, align = Align.RIGHT, pad = Pad.ZERO)
        private Long amount;

        @Fixed(order = 4, bytes = 1)
        private Boolean active;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
