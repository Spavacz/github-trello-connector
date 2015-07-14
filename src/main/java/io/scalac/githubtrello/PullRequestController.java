package io.scalac.githubtrello;

import io.scalac.githubtrello.model.PullRequestEvent;
import io.scalac.githubtrello.model.TrelloCard;
import org.springframework.web.bind.annotation.*;

@RestController
public class PullRequestController {

    private final static String ROUTE_PULL_REQUEST = "/pr";

    private Authenticator authenticator = new Authenticator();
    private TrelloApi trelloApi = new TrelloApi();
    private PullRequestParser pullRequestParser = new PullRequestParser();

    @RequestMapping(value = ROUTE_PULL_REQUEST, method = RequestMethod.POST)
    public String pullRequest(@RequestBody PullRequestEvent pullRequestEvent, @RequestParam(value = "t") String token) {
        if (!authenticator.authenticate(token))
            return "ACCESS DENIED";

        switch (pullRequestEvent.getAction()) {
            case PullRequestEvent.ACTION_OPENED:
                onPullRequestOpened(pullRequestEvent.getPullRequest());
                break;
        }

        return "OK";
    }

    private void onPullRequestOpened(PullRequestEvent.PullRequest pullRequest) {
        final TrelloCard[] trelloCards = trelloApi.getCards();
        final String trelloCardShortLink = pullRequestParser.parseTrelloCardShortLink(pullRequest.getHead().getRef(), trelloCards);

        if (trelloCardShortLink.isEmpty()) {
            System.out.println("No Trello link");
        } else {
            trelloApi.postCommentToCard(trelloCardShortLink, pullRequest.getHtmlUrl());
            System.out.println(pullRequest.getHtmlUrl() + " posted to Trello card " + trelloCardShortLink);
        }
    }
}