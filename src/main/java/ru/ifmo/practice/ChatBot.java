package ru.ifmo.practice;

import lombok.val;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;
import java.util.Properties;
import java.io.InputStream;

@Log4j2
public class ChatBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;
    private HashMap<String, NluBot> bots = new HashMap<>();

    @SneakyThrows
    public ChatBot(String pathSettings){
        val props = new Properties();

        try (InputStream inStream = ChatBot.class.getResourceAsStream(pathSettings)) {
            props.load(inStream);

            botToken = props.getProperty("token");
            botUsername = props.getProperty("username");
        }
        catch (Exception e){
            log.fatal("Can't load settings file!", e);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        // Init bot
        ApiContextInitializer.init();
        val telegramBotsApi = new TelegramBotsApi()
                .registerBot(new ChatBot("/settings.properties"));
    }

    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null){
            getMessage(update);
        }
    }

    private void getMessage(Update update){
        Message message = update.getMessage();
        String userLogin = message.getChat().getUserName() ;
        String requestMsg = (message.getText() == null) ? "" : message.getText();

        if (! bots.containsKey(userLogin)){
            val newBot = new NluBot();
            bots.put(userLogin, newBot);
        }

        log.info(userLogin + ": " + requestMsg);
        String responseMsg = bots.get(userLogin).getAnswer(requestMsg);
        sendMessage(message, responseMsg);
        log.info("Bot's answer: " + responseMsg);
    }

    @SneakyThrows
    private void sendMessage(Message message, String text) {
        val sMessage = new SendMessage()
            .setChatId(message.getChatId())
            .setText(text);
        //noinspection deprecation
        sendMessage(sMessage);
    }

    @SneakyThrows
    private void sendSticker(Message message, String stickerToken){
        val sticker = new SendSticker()
            .setChatId(message.getChatId())
            .setSticker(stickerToken);

        sendSticker(sticker);
    }
}