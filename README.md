# Fixed Length Format for Java

A fast, lightweight, zero-dependency NIO-based pure Java 8+ library for converting fixed-length format data to Java objects and vice versa.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Annotation Reference](#annotation-reference)
- [Advanced Usage](#advanced-usage)
  - [Null Handling](#null-handling)
  - [Character Encoding](#character-encoding)
  - [Character Width Conversion](#character-width-conversion)
  - [Custom Converters](#custom-converters)
- [Supported Types](#supported-types)

---

## Features

- **Zero dependencies** - Pure Java implementation
- **Java 8+ compatible** - Works with legacy systems
- **High performance** - NIO ByteBuffer-based processing with reflection caching
- **Flexible encoding** - UTF-8, EUC-KR, MS949 and more
- **Half/Full-width support** - Convert between half-width and full-width characters
- **Null safety** - Configurable null value handling
- **Custom converters** - Extensible type conversion system

## Prerequisites

- Java 8 or higher
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Installation

This library was developed to solve practical challenges in real-world projects. It is currently for personal use and has not been officially published to Maven Central. Therefore, manual installation is required:

### Build from Source

```bash
git clone https://github.com/zhc1/fixedlength4j.git
cd fixedlength4j
./gradlew build
```

### Add as Dependency

After building, add the JAR to your project:

#### Gradle

```kotlin
dependencies {
    implementation(files("path/to/fixedlength4j.jar"))
}
```

#### Maven

```xml
<dependency>
    <groupId>io.zhc1</groupId>
    <artifactId>fixedlength4j</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/fixedlength4j.jar</systemPath>
</dependency>
```

## Quick Start

Fixed-length format is commonly used in financial systems. For example, a 33-byte record:

```text
John      Doe       0301993-01-01
```

| Field     | Bytes | Value        |
|-----------|-------|--------------|
| firstName | 10    | "John      " |
| lastName  | 10    | "Doe       " |
| age       | 3     | "030"        |
| birthday  | 10    | "1993-01-01" |

### Define your model

```java
public class Employee {
    @Fixed(bytes = 10, order = 1)
    private String firstName;

    @Fixed(bytes = 10, order = 2)
    private String lastName;

    @Fixed(bytes = 3, order = 3, pad = Pad.ZERO, align = Align.RIGHT)
    private int age;

    @Fixed(bytes = 10, order = 4, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    // getters and setters required
}
```

### Deserialize (bytes to object)

```java
ByteBuffer buffer = ByteBuffer.wrap("John      Doe       0301993-01-01".getBytes());
List<Employee> employees = new FixedLengthFormat<>(Employee.class).deserialize(buffer);
```

### Serialize (object to bytes)

```java
Employee employee = new Employee("John", "Doe", 30, LocalDate.of(1993, 1, 1));
ByteBuffer buffer = new FixedLengthFormat<>(Employee.class).serialize(employee);

// Or serialize multiple objects
List<Employee> employees = Arrays.asList(emp1, emp2);
ByteBuffer buffer = new FixedLengthFormat<>(Employee.class).serialize(employees);
```

## Annotation Reference

### @Fixed

| Attribute        | Type           | Default   | Description                                      |
|------------------|----------------|-----------|--------------------------------------------------|
| `bytes`          | int            | required  | Field length in bytes                            |
| `order`          | int            | required  | Field order (ascending)                          |
| `pad`            | Pad            | SPACE     | Padding character (SPACE, ZERO)                  |
| `align`          | Align          | LEFT      | Data alignment (LEFT, RIGHT)                     |
| `pattern`        | String         | yyyyMMdd  | Date/time format pattern                         |
| `nullable`       | boolean        | false     | Allow null values                                |
| `nullValue`      | String         | ""        | String representation for null                   |
| `charset`        | String         | ""        | Field-specific charset (empty = use default)     |
| `width`          | CharacterWidth | PRESERVE  | Half/full-width conversion mode                  |
| `allowMixedWidth`| boolean        | true      | Allow mixed half/full-width characters           |

## Advanced Usage

### Null Handling

By default, null values throw an exception. Enable null support with `nullable` and `nullValue`:

```java
public class Person {
    @Fixed(bytes = 10, order = 1)
    private String name;

    @Fixed(bytes = 10, order = 2, nullable = true, nullValue = "N/A")
    private String nickname;  // Must use wrapper type for nullable fields
}
```

**Rules:**
- `nullable=true` requires a non-empty `nullValue`
- Primitive types (`int`, `long`, etc.) cannot be nullable - use wrapper types (`Integer`, `Long`)
- `nullValue` byte length must not exceed `bytes`

### Character Encoding

Specify encoding at format level or field level:

```java
// Default charset for all fields
FixedLengthFormat<Person> format = new FixedLengthFormat<>(Person.class, Charset.forName("EUC-KR"));

// Or per-field charset
public class Person {
    @Fixed(bytes = 20, order = 1, charset = "EUC-KR")
    private String koreanName;

    @Fixed(bytes = 10, order = 2)  // Uses default charset
    private String code;
}
```

### Character Width Conversion

Handle half-width (ASCII) and full-width (CJK) character conversion:

```java
public class BankRecord {
    // Convert full-width to half-width: "１２３" → "123"
    @Fixed(bytes = 10, order = 1, width = CharacterWidth.HALF)
    private String accountNumber;

    // Convert half-width to full-width: "ABC" → "ＡＢＣ"
    @Fixed(bytes = 30, order = 2, width = CharacterWidth.FULL)
    private String displayName;

    // Strict mode: only allow half-width, throw exception otherwise
    @Fixed(bytes = 10, order = 3, width = CharacterWidth.HALF_ONLY)
    private String bankCode;

    // Reject mixed width characters
    @Fixed(bytes = 20, order = 4, width = CharacterWidth.PRESERVE, allowMixedWidth = false)
    private String memo;
}
```

**CharacterWidth modes:**
- `PRESERVE` - Keep original width (default)
- `HALF` - Convert full-width to half-width
- `FULL` - Convert half-width to full-width
- `HALF_ONLY` - Only allow half-width, throw exception if full-width found
- `FULL_ONLY` - Only allow full-width, throw exception if half-width found

### Custom Converters

Create custom converters for unsupported types:

```java
public class MyTypeConverter extends AbstractConverter<MyType> {
    @Override
    public String convertToString(MyType value, Fixed annotation) {
        return value.toString();
    }

    @Override
    public MyType convertToObject(String value, Fixed annotation) {
        return MyType.parse(value.trim());
    }
}
```

Register your converter:

```java
public class MyConverterRegistrar extends ConverterRegistrar {
    @Override
    protected void addConverters(Map<Class<?>, Converter<?>> converters) {
        converters.put(MyType.class, new MyTypeConverter());
    }
}

// Use custom registrar
FixedLengthFormat<Record> format = new FixedLengthFormat<>(Record.class, new MyConverterRegistrar());
```

## Supported Types

Built-in converters support:

| Type          | Default Padding | Notes                           |
|---------------|-----------------|----------------------------------|
| String        | SPACE/LEFT      |                                  |
| boolean/Boolean | SPACE/LEFT    | Serialized as "1"/"0"           |
| int/Integer   | SPACE/LEFT      |                                  |
| long/Long     | SPACE/LEFT      |                                  |
| float/Float   | SPACE/LEFT      |                                  |
| double/Double | SPACE/LEFT      |                                  |
| BigInteger    | SPACE/LEFT      |                                  |
| BigDecimal    | SPACE/LEFT      |                                  |
| LocalDate     | SPACE/LEFT      | Uses `pattern` attribute        |
| LocalDateTime | SPACE/LEFT      | Uses `pattern` attribute        |

## License

MIT License
