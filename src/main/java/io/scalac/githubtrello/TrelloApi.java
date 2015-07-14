package io.scalac.githubtrello;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.scalac.githubtrello.model.TrelloCard;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TrelloApi {
    private final static String TRELLO_CLIENT_KEY = System.getProperty("trello.client_key");
    private final static String TRELLO_TOKEN = System.getProperty("trello.token");
    private final static String TRELLO_BOARD_ID = System.getProperty("trello.board_id");
    private final static String TRELLO_AUTH_QUERY = "key=" + TRELLO_CLIENT_KEY + "&token=" + TRELLO_TOKEN;

    public TrelloCard[] getCards() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url = new URL("https://api.trello.com/1/boards/" + TRELLO_BOARD_ID + "/cards/open?" + TRELLO_AUTH_QUERY);
            return mapper.readValue(url, TrelloCard[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void postCommentToCard(String cardShortLink, String comment) {
        byte[] postData = ("text=" + comment).getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            URL url = new URL("https://api.trello.com/1/cards/" + cardShortLink + "/actions/comments?" + TRELLO_AUTH_QUERY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            connection.setUseCaches(false);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postData);

            Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            for (int c = in.read(); c != -1; c = in.read())
                System.out.print((char) c);

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
