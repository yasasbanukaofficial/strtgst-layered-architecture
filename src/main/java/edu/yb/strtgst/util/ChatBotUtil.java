package edu.yb.strtgst.util;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class ChatBotUtil {
    private static final String GOOGLE_API_KEY = "AIzaSyAboDpPm77ZEmlnGyyRK-Ta518yv6e9p9Q";

    public static String getResponse(String instructions) {
        System.setProperty("GOOGLE_API_KEY", GOOGLE_API_KEY);

        try {
            Client client = new Client.Builder().apiKey(GOOGLE_API_KEY).build();
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.0-flash",
                    instructions,
                    null
            );
            return response.text();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when generating the text through AI " + e.getMessage());
            return "Error:  " + e.getMessage();
        }
    }
}
