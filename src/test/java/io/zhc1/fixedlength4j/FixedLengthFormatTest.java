package io.zhc1.fixedlength4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.zhc1.fixedlength4j.fixture.Employee;

class FixedLengthFormatTest {
    FixedLengthFormat<Employee> sut = new FixedLengthFormat<>(Employee.class);

    @Test
    @DisplayName("Serializes single Employee object to fixed-length format")
    void test_serialize_employee() {
        // given
        Employee john = new Employee();
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setAge(30);
        john.setBirthday(LocalDate.of(1993, 1, 1));

        // when
        ByteBuffer actual = sut.serialize(john);

        // then
        assertThat(new String(actual.array())).isEqualTo("John      Doe       0301993-01-01");
    }

    @Test
    @DisplayName("Serializes list of Employee objects to fixed-length format")
    void test_serialize_employees() {
        // given
        Employee john = new Employee();
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setAge(30);
        john.setBirthday(LocalDate.of(1993, 1, 1));

        List<Employee> employees = Collections.singletonList(john);

        // when
        ByteBuffer actual = sut.serialize(employees);

        // then
        assertThat(new String(actual.array())).isEqualTo("John      Doe       0301993-01-01");
    }

    @Test
    @DisplayName("Deserializes fixed-length format to list of Employee objects")
    void test_deserialize_employees() {
        // given
        ByteBuffer buffer =
                ByteBuffer.wrap("John      Doe       0301993-01-01Michael   Andrew    0331990-01-01".getBytes());

        // when
        List<Employee> actual = sut.deserialize(buffer);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getFirstName()).isEqualTo("John");
        assertThat(actual.get(0).getLastName()).isEqualTo("Doe");
        assertThat(actual.get(0).getAge()).isEqualTo(30);
        assertThat(actual.get(0).getBirthday()).isEqualTo(LocalDate.of(1993, 1, 1));

        assertThat(actual.get(1).getFirstName()).isEqualTo("Michael");
        assertThat(actual.get(1).getLastName()).isEqualTo("Andrew");
        assertThat(actual.get(1).getAge()).isEqualTo(33);
        assertThat(actual.get(1).getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }
}
