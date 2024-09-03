# SimpleSFTP

SimpleSFTP - максимально упрощённая java-библиотека для передачи файлов через SFTP-протокол.

## Добавление зависимости

### Gradle

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Vladislav117:SimpleSFTP:1.0'
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
    <version>1.0</version>
</dependency>
```

## Использование

Пример загрузки файла на сервер:
```java
// Создание объекта для подключения
SimpleSFTP sftp = new SimpleSFTP("your-sftp-server.com", "7777", "user", "password");

// Если будет вызвано исключение - оно будет выведено в консоль
sftp.setDisplayExceptions(true);

// Подключение. Полученный статус покажет, что пошло не так
SimpleSFTP.Status status = sftp.connect();

// Если статус неуспешный, прерываем выполнение
if (status != SimpleSFTP.Status.SUCCESS) return;

// Передача файла "image.png" в директорию сервера "/images".
status = sftp.transferFile(new File("image.png"), "/images")

// Если статус неуспешный, прерываем выполнение
if (status != SimpleSFTP.Status.SUCCESS) return;

// Передача файла "image2.png" в директорию сервера "/images".
status = sftp.transferFile("image2.png", "/images")

// Если статус неуспешный, прерываем выполнение
if (status != SimpleSFTP.Status.SUCCESS) return;

// Отключаем соединение
sftp.disconnect();
```
