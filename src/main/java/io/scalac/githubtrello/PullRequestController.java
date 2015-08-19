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
        final TrelloCard trelloCard = pullRequestParser.parseTrelloCard(pullRequest, trelloCards);

        if (trelloCard == null) {
            System.out.println("No Trello card found");
        } else if (trelloCard.hasChecklist()) {
            trelloApi.postChecklistToCard(trelloCard.getIdChecklist(), pullRequest.getHtmlUrl());
            System.out.println(pullRequest.getHtmlUrl() + " posted to Trello card checklist " + trelloCard);
        } else {
            trelloApi.postCommentToCard(trelloCard.getShortLink(), pullRequest.getHtmlUrl());
            System.out.println(pullRequest.getHtmlUrl() + " posted to Trello card " + trelloCard);
        }
}
}