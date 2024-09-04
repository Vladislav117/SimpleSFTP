package ru.vladislav117.simplesftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

/**
 * Объект связи с севером по SFTP.
 */
public class SimpleSFTP {
    static boolean displayExceptions = true;
    static Consumer<Exception> exceptionDisplay = Throwable::printStackTrace;

    /**
     * Получение статуса отображения исключений. По умолчанию отображение включено.
     *
     * @return Статус отображения исключений.
     */
    public static boolean isDisplayExceptions() {
        return displayExceptions;
    }

    /**
     * Установка статуса отображения исключений. По умолчанию отображение включено.
     *
     * @param displayExceptions Статус отображения исключений.
     */
    public static void setDisplayExceptions(boolean displayExceptions) {
        SimpleSFTP.displayExceptions = displayExceptions;
    }

    /**
     * Установка метода отображения исключений. По умолчанию это printStackTrace().
     *
     * @param exceptionDisplay Метод отображения исключений.
     */
    public static void setExceptionDisplay(Consumer<Exception> exceptionDisplay) {
        SimpleSFTP.exceptionDisplay = exceptionDisplay;
    }

    /**
     * Отображение исключения. Если отображение отключено, то ничего не произойдёт.
     *
     * @param exception Исключение.
     */
    static void displayException(Exception exception) {
        if (displayExceptions) exceptionDisplay.accept(exception);
    }

    protected JSch jsch;
    protected Session session;
    protected ChannelSftp channel;

    /**
     * Создание объекта связи по SFTP.
     *
     * @param jsch    JSch-объект
     * @param session Сессия
     * @param channel Канал
     */
    protected SimpleSFTP(JSch jsch, Session session, ChannelSftp channel) {
        this.jsch = jsch;
        this.session = session;
        this.channel = channel;
    }

    /**
     * Создание связи с сервером.
     *
     * @param address  Адрес сервера
     * @param port     SFTP-порт
     * @param user     Пользователь
     * @param password Пароль
     * @return Созданный объект связи с сервером или null, если произошла какая-либо ошибка.
     */
    public static @Nullable SimpleSFTP connect(String address, String port, String user, String password) {
        int portNumber;
        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException exception) {
            displayException(exception);
            return null;
        }

        JSch jsch;
        Session session;
        ChannelSftp channel;
        try {
            jsch = new JSch();
            session = jsch.getSession(user, address, portNumber);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (Exception exception) {
            displayException(exception);
            return null;
        }

        return new SimpleSFTP(jsch, session, channel);
    }

    /**
     * Передача файла на сервер.
     *
     * @param localFile       Локальный файл
     * @param remoteDirectory Директория на сервере
     * @return Статус передачи файла: true - если файл был передан, false - если произошла ошибка.
     */
    public boolean transferFile(File localFile, String remoteDirectory) {
        try (FileInputStream fileInputStream = new FileInputStream(localFile)) {
            channel.cd(remoteDirectory);
            channel.put(fileInputStream, localFile.getName());
        } catch (Exception exception) {
            displayException(exception);
            return false;
        }
        return true;
    }

    /**
     * Передача файла на сервер.
     *
     * @param localFilePath   Путь к локальному файлу
     * @param remoteDirectory Директория на сервере
     * @return Статус передачи файла: true - если файл был передан, false - если произошла ошибка.
     */
    public boolean transferFile(String localFilePath, String remoteDirectory) {
        return transferFile(new File(localFilePath), remoteDirectory);
    }

    /**
     * Отключение соединения с сервером.
     */
    public void disconnect() {
        channel.disconnect();
        session.disconnect();
    }
}
