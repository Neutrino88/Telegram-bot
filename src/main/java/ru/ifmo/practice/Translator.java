package ru.ifmo.practice;

import static lombok.AccessLevel.PRIVATE;
import static com.github.kevinsawicki.http.HttpRequest.get;

import lombok.Getter;
import org.json.JSONObject;
import lombok.extern.log4j.Log4j2;
import lombok.experimental.FieldDefaults;
import com.github.kevinsawicki.http.HttpRequest;

@Log4j2
@FieldDefaults(level = PRIVATE)
public class Translator {
    private static final String URL_YANDEX_DETECT_LANG = "https://translate.yandex.net/api/v1.5/tr.json/detect";
    private static final String URL_YANDEX_TRANSLATE = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    String yandexApiKey;
    @Getter
    String lastLanguageCode;

    Translator(String yandexApiKey){
        this.yandexApiKey = yandexApiKey;
        this.lastLanguageCode = "en";
    }

    public String getTranslation(String text, String languageCode) {
        // get-request to URL_YANDEX_TRANSLATE?key=...&text=...&lang=...
        String response = HttpRequest.get(
                URL_YANDEX_TRANSLATE, true,
                "key", yandexApiKey,
                "text", text,
                "lang", languageCode
        ).body();

        JSONObject jsonObj = new JSONObject(response);
        int responseCode = jsonObj.getInt("code");

        if (responseCode == 200) {
            response = jsonObj.getJSONArray("text").getString(0);
            log.info("Successful translating to {} - {}", languageCode, response);
            return response;
        } else {
            log.error("Failure translating to {} with code {} - {}", languageCode, responseCode,response);
            return text;
        }
    }

    public String getLanguageCode(String text) {
        // get-request to URL_YANDEX_DETECT_LANG?key=...&text=...
        String response = get(
                URL_YANDEX_DETECT_LANG, true,
                "key", yandexApiKey,
                "text", text
        ).body();

        JSONObject jsonObject = new JSONObject(response);
        String lang = jsonObject.getString("lang");

        if (jsonObject.getInt("code") != 200 || lang.isEmpty())
            log.warn("Unknown language in: {}", response);
        else {
            lastLanguageCode = lang;
            log.info("Language is {}", lang);
        }

        return lang;
    }
}
