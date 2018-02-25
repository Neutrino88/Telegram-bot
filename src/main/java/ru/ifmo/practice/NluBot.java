package ru.ifmo.practice;

import java.io.File;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class NluBot {
    private static String resourcesPath;
    private Chat chatSession;

    NluBot(){
        if (resourcesPath == null)
            resourcesPath = getResourcesPath();
        MagicBooleans.trace_mode = false;

        Bot bot = new Bot("botName", resourcesPath);
        bot.brain.nodeStats();
        chatSession = new Chat(bot);

        log.info("NluBot successfully created");
    }

    public String getAnswer(String request) {
        if ((request == null) || (request.length() < 1))
            request = MagicStrings.null_input;

        String response = chatSession.multisentenceRespond(request);

        while (response.contains("&lt;"))
            response = response.replace("&lt;", "<");
        while (response.contains("&gt;"))
            response = response.replace("&gt;", ">");

        return response;
    }

    private static String getResourcesPath() {
        String path = new File(".").getAbsolutePath();
        path = path.substring(0, path.length() - 2);

        return path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
    }
}
