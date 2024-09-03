package ru.vladislav117.simplesftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Простой объект управления SFTP.
 */
public class SimpleSFTP {
    /**
     * Статус выполнения операции.
     */
    public enum Status {
        /**
         * Статус успеха проведения операции.
         */
        SUCCESS,
        /**
         * Ошибка чтения порта. Обычно такое происходит, если введённая строка порта не является числом.
         */
        PORT_ERROR,
        /**
         * Ошибка подключения. Причины могут быть разными: нет подключения к интернету, неверный адрес сервера и т.п.
         */
        CONNECTION_ERROR,
        /**
         * Отсутствие локального файла.
         */
        LOCAL_FILE_NOT_FOUND,
        /**
         * Ошибка загрузки файла на сервер.
         */
        UPLOAD_ERROR
    }

    protected String address;
    protected String port;
    protected String user;
    protected String password;
    protected boolean connected = false;
    protected boolean displayExceptions = false;

    protected @Nullable JSch jsch = null;
    protected @Nullable Session session = null;
    protected @Nullable ChannelSftp channel = null;

    /**
     * Создание объекта управления SFTP.
     *
     * @param address  Адрес сервера
     * @param port     Порт сервера
     * @param user     Пользователь
     * @param password Пароль
     */
    public SimpleSFTP(String address, String port, String user, String password) {
        this.address = address;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /**
     * Получение адреса сервера.
     *
     * @return Адрес сервера.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Получение порта сервера.
     *
     * @return Порт сервера.
     */
    public String getPort() {
        return port;
    }

    /**
     * Получение пользователя.
     *
     * @return Пользователь.
     */
    public String getUser() {
        return user;
    }

    /**
     * Получение статуса соединения.
     *
     * @return Статус соединения.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Получение статуса отображения исключений.
     *
     * @return Статус отображения исключений.
     */
    public boolean isDisplayExceptions() {
        return displayExceptions;
    }

    /**
     * Установка статуса отображения исключений.
     *
     * @param displayExceptions Статус отображения
     * @return Этот же объект управления SFTP.
     */
    public SimpleSFTP setDisplayExceptions(boolean displayExceptions) {
        this.displayExceptions = displayExceptions;
        return this;
    }

    /**
     * Получение JSch.
     *
     * @return JSch или null, если подключения не было произведено.
     */
    public @Nullable JSch getJsch() {
        return jsch;
    }

    /**
     * Получение сессии.
     *
     * @return Сессия или null, если подключения не было произведено.
     */
    public @Nullable Session getSession() {
        return session;
    }

    /**
     * Получение канала.
     *
     * @return Канал или null, если подключения не было произведено.
     */
    public @Nullable ChannelSftp getChannel() {
        return channel;
    }

    /**
     * Подключение к серверу.
     *
     * @return Статус подключения.
     */
    public Status connect() {
        if (connected) return Status.SUCCESS;
        int port = 0;
        try {
            port = Integer.parseInt(this.port);
        } catch (NumberFormatException exception) {
            displayException(exception);
            return Status.PORT_ERROR;
        }
        try {
            jsch = new JSch();
            session = jsch.getSession(user, address, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (Exception exception) {
            displayException(exception);
            return Status.CONNECTION_ERROR;
        }
        connected = true;
        return Status.SUCCESS;
    }

    /**
     * Передача файла.
     *
     * @param localFile       Локальный файл
     * @param remoteDirectory Директория на сервере
     * @return Статус передачи файла.
     */
    public Status transferFile(File localFile, String remoteDirectory) {
        if (!connected) {
            Status status = connect();
            if (status != Status.SUCCESS) return status;
        }
        if (channel == null) return Status.CONNECTION_ERROR;
        try (FileInputStream fileInputStream = new FileInputStream(localFile)) {
            channel.cd(remoteDirectory);
            channel.put(fileInputStream, localFile.getName());
            System.out.println("File uploaded successfully - " + localFile);
        } catch (FileNotFoundException exception) {
            displayException(exception);
            return Status.LOCAL_FILE_NOT_FOUND;
        } catch (SftpException exception) {
            displayException(exception);
            return Status.UPLOAD_ERROR;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return Status.SUCCESS;
    }

    /**
     * Передача файла.
     *
     * @param localFilePath   Путь к локальному файлу
     * @param remoteDirectory Директория на сервере
     * @return Статус передачи файла.
     */
    public Status transferFile(String localFilePath, String remoteDirectory) {
        return transferFile(new File(localFilePath), remoteDirectory);
    }

    /**
     * Отключение.
     */
    public void disconnect() {
        if (!connected) return;
        if (channel != null) channel.disconnect();
        if (session != null) session.disconnect();
    }

    /**
     * Отображение исключения. Если отображение отключено, ничего не произойдёт.
     *
     * @param exception Исключение
     */
    protected void displayException(Exception exception) {
        if (displayExceptions) System.out.println(exception.toString());
    }
}
