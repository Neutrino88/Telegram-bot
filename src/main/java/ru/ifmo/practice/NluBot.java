package ru.ifmo.practice;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import lombok.extern.log4j.Log4j2;
import lombok.experimental.FieldDefaults;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.MagicBooleans;

@Log4j2
@FieldDefaults(level = PRIVATE)
public class NluBot {
    private static String resourcesPath;
    Chat chatSession;

    NluBot(){
        if (resourcesPath == null) {
            String path = new File(".").getAbsolutePath();
            path = path.substring(0, path.length() - 2);

            resourcesPath = String.format("%s%ssrc%<smain%<sresources", path, File.separator);
        }

        MagicBooleans.trace_mode = false;

        Bot bot = new Bot("botName", resourcesPath);
        bot.brain.nodeStats();
        chatSession = new Chat(bot);

        log.info("NluBot was successfully created");
    }

    public String getAnswer(String request) {
        if (request == null || request.isEmpty())
            request = MagicStrings.null_input;

        return chatSession
                .multisentenceRespond(request)
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}
