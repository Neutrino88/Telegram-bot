package ru.ifmo.practice;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.val;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.experimental.FieldDefaults;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;

import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;

@Log4j2
@FieldDefaults(level= PRIVATE)
public class ChatBot extends TelegramLongPollingBot {
    private final static String FILE_SETTINGS = "/settings.properties";

    @Getter
    String botToken;
    @Getter
    String botUsername;

    HashMap<String, NluBot> bots = new HashMap<>();

    @SneakyThrows
    public ChatBot(String pathSettings){
        val props = new Properties();

        try (InputStream inStream = ChatBot.class.getResourceAsStream(pathSettings)) {
            props.load(inStream);

            botToken = props.getProperty("token", "<no-token>");
            botUsername = props.getProperty("username", "<no-username>");

            log.info(() -> pathSettings + " file was successfully read ");
        }
        catch (Exception e){
            log.fatal(() -> "Can't load settings from " + pathSettings, e);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        // Init bot
        ApiContextInitializer.init();
        new TelegramBotsApi()
                .registerBot(new ChatBot(FILE_SETTINGS));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null)
            getMessage(update);
    }

    private void getMessage(Update update){
        Message message = update.getMessage();
        String userLogin = message.getChat().getUserName() ;
        String requestMsg = (message.getText() == null) ? "" : message.getText();

        if (! bots.containsKey(userLogin))
            bots.put(userLogin, new NluBot());

        log.info(() -> userLogin + ": " + requestMsg);
        String responseMsg = bots.get(userLogin).getAnswer(requestMsg);
        sendMessage(message, responseMsg);
        log.info(() -> "Bot's answer: " + responseMsg);
    }

    @SneakyThrows
    private void sendMessage(Message message, String text) {
        //noinspection deprecation
        sendMessage(new SendMessage()
                .setChatId(message.getChatId())
                .setText(text));
    }

    @SneakyThrows
    private void sendSticker(Message message, String stickerToken){
        sendSticker(new SendSticker()
                .setChatId(message.getChatId())
                .setSticker(stickerToken));
    }
}