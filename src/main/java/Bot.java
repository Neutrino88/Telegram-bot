import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    private String botToken;
    private HashSet<String> users = new HashSet<String>();
    private ArrayList<Message> messages = new ArrayList<Message>();

    public Bot(String token){
        botToken = token;
    }

    public static void main(String[] args) {
        String botToken;

        // Read Bot's token
        try
        {
            File tokenFile = new File("src/main/resources/bot_token.txt");
            Scanner scanner = new Scanner(tokenFile);
            botToken = scanner.nextLine();
        }
        catch(Exception e){
            System.out.println("Can't read bot's token!");
            e.printStackTrace();
            return;
        }
        // Init bot
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot(botToken));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "olegs_simple_bot";
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
        // save message
        Message message = update.getMessage();
        messages.add(message);

        printToConsole(message);

        // save username
        // if user is new
        if (users.add(message.getChat().getUserName())) {
            String first_name = message.getChat().getFirstName();
            sendMessage(message, first_name + ", I haven't time :(\nCan you write to me later?");
        }
        else {
            String[] angryStickerTokens = {
                    "CAADBAADGgIAAuJy2QABeEernu20KnMC",
                    "CAADAgADxgEAAu5izQdODlEtrOY2_gI",
                    "CAADAgADBQUAAmvEygqACJYqPM8VfwI",
                    "CAADAgAD2QEAAu5izQcssIrlHhk7xgI",
                    "CAADAgADFwADwyiDDZxXAAGHyl5myAI",
                    "CAADAgADPgMAAsSraAtfLLQnD0YAAcIC"
            };

            sendSticker(message, angryStickerTokens[new Random().nextInt(angryStickerTokens.length)]);
        }
    }

    private void sendMessage(Message message, String text) {
        SendMessage sMessage = new SendMessage();
        sMessage.setChatId(message.getChatId());
        sMessage.setText(text);
        //sMessage.enableMarkdown(true);
        //sendMessage.setReplyToMessageId(message.getMessageId());

        try {
            sendMessage(sMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSticker(Message message, String stickerToken){
        SendSticker sticker = new SendSticker();
        sticker.setChatId(message.getChatId());
        sticker.setSticker(stickerToken);

        try {
            sendSticker(sticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void printToConsole(Message message){
        System.out.println("login:" + message.getChat().getUserName());

        if (message.getText() != null) {
            System.out.println("text: " + message.getText() + "\n");
        }

        if (message.getSticker() != null) {
            System.out.println("sticker: " + message.getSticker().getFileId() + "\n");
        }
    }
}