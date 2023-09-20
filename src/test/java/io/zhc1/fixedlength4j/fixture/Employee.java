package io.zhc1.fixedlength4j.fixture;

import java.time.LocalDate;

import io.zhc1.fixedlength4j.annotation.Align;
import io.zhc1.fixedlength4j.annotation.Fixed;
import io.zhc1.fixedlength4j.annotation.Pad;

public class Employee {
    @Fixed(bytes = 10, order = 1, pad = Pad.SPACE, align = Align.LEFT)
    private String firstName;

    @Fixed(bytes = 10, order = 2, pad = Pad.SPACE, align = Align.LEFT)
    private String lastName;

    @Fixed(bytes = 3, order = 3, pad = Pad.ZERO, align = Align.RIGHT)
    private int age;

    @Fixed(bytes = 10, order = 4, pattern = "yyyy-MM-dd", pad = Pad.SPACE, align = Align.LEFT)
    private LocalDate birthday;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
