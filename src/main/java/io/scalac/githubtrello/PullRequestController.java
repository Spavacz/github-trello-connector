package io.scalac.githubtrello;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class PullRequestController {

    private final static String ROUTE_PULL_REQUEST = "/pr";
    private final static String TRELLO_CLIENT_KEY = System.getProperty("trello.client_key");
    private final static String TRELLO_TOKEN = System.getProperty("trello.token");
    private final static String TRELLO_BOARD_ID = System.getProperty("trello.board_id");
    private final static String TRELLO_AUTH_QUERY = "key=" + TRELLO_CLIENT_KEY + "&token=" + TRELLO_TOKEN;

    private Authenticator authenticator;

    public PullRequestController() {
        this.authenticator = new Authenticator();
    }

    @RequestMapping(value = ROUTE_PULL_REQUEST, method = RequestMethod.POST)
    public String pullRequest(@RequestBody PullRequestEvent pullRequestEvent, @RequestParam(value = "t") String token) {
        if (!authenticator.authenticate(token))
            return "ACCESS DENIED";

        switch (pullRequestEvent.action) {
            case PullRequestEvent.ACTION_OPENED:
                onPullRequestOpened(pullRequestEvent.pull_request);
                break;
        }

        return "OK";
    }

    private void onPullRequestOpened(PullRequestEvent.PullRequest pullRequest) {
        final String trelloCardShortlink = parseTrelloCardShortLink(pullRequest.body);

        if (trelloCardShortlink.isEmpty()) {
            System.out.println("No Trello link");
            return;
        }

        postCommentToTrelloCard(trelloCardShortlink, pullRequest.html_url);
        System.out.println(pullRequest.html_url + " posted to Trello card " + trelloCardShortlink);
    }

    private String parseTrelloCardShortLink(String s) {
        String shortLink = "";
        Pattern pattern = Pattern.compile("(card #([0-9]+))|(https\\:\\/\\/trello.com\\/c\\/([0-9a-z]*))", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            shortLink = matcher.group(4) == null ? findTrelloCardShortLinkByShortId(matcher.group(2)) : matcher.group(4);
        }
        return shortLink;
    }

    private String findTrelloCardShortLinkByShortId(String trelloCardShortId) {
        ObjectMapper mapper = new ObjectMapper();
        String shortLink = "";
        try {
            URL url = new URL("https://api.trello.com/1/boards/" + TRELLO_BOARD_ID + "/cards/open?" + TRELLO_AUTH_QUERY);
            List<Map<String, Object>> cardList = mapper.readValue(url, new TypeReference<List<Map<String, Object>>>() {
            });
            for (Map card : cardList) {
                if (trelloCardShortId.equals(card.get("idShort").toString())) {
                    shortLink = String.valueOf(card.get("shortLink"));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shortLink;
    }

    private void postCommentToTrelloCard(String trelloCardShortlink, String comment) {
        byte[] postData = ("text=" + comment).getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            URL url = new URL("https://api.trello.com/1/cards/" + trelloCardShortlink + "/actions/comments?" + TRELLO_AUTH_QUERY);
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

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}