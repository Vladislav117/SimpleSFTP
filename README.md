# SimpleSFTP

SimpleSFTP - максимально упрощённая java-библиотека для передачи файлов через SFTP-протокол.

## Добавление зависимости

### Gradle

```groovy
dependencies {
    implementation 'com.github.mwiede:jsch:0.2.19'
    implementation ('com.github.Vladislav117:SimpleSFTP:1.1') {
        exclude group: "com.github.mwiede", module: "jsch"
    }
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Vladislav117</groupId>
    <artifactId>SimpleSFTP</artifactId>
    <version>1.1</version>
    <exclusions>
        <exclusion>
            <groupId>com.jcraft</groupId>
            <artifactId>jsch</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Использование

Пример загрузки файла на сервер:
```java
// Подключение к серверу.
SimpleSFTP sftp = SimpleSFTP.connect("127.0.0.1", "7777", "user", "password");

// Если не удалось подключиться, метод connect вернёт null.
if (sftp == null) return;

// Передача файла "image1.png" в директорию сервера "/images".
boolean status1 = sftp.transferFile(new File("image1.png"), "/images");

// Если не удалось отправить файл, то статус будет равен false.
if (!status1) return;

// Передача файла "image2.png" в директорию сервера "/images".
boolean status2 = sftp.transferFile("image2.png", "/images");

// Если не удалось отправить файл, то статус будет равен false.
if (!status2) return;

// Отключение от сервера.
sftp.disconnect();
```

Если вы хотите отключить вывод исключений или изменить метод вывода, воспользуйтесь этим:

```java
// Отключение вывода исключений.
SimpleSFTP.setDisplayExceptions(false);

// Изменение метода вывода исключений.
SimpleSFTP.setExceptionDisplay(exception -> exception.printStackTrace());
```
