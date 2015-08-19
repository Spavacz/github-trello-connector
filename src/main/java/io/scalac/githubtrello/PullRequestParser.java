package io.scalac.githubtrello;

import io.scalac.githubtrello.model.PullRequestEvent;
import io.scalac.githubtrello.model.TrelloCard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PullRequestParser {

    public TrelloCard parseTrelloCard(PullRequestEvent.PullRequest pullRequest, TrelloCard[] trelloCards) {
        String cardNumber = parseTrelloCardNumber(pullRequest.getBranchName());
        if (!cardNumber.isEmpty()) {
            for (TrelloCard card : trelloCards) {
                if (cardNumber.equals(card.getIdShort())) {
                    return card;
                }
            }
        }
        return null;
    }

    private String parseTrelloCardNumber(String s) {
        Pattern pattern = Pattern.compile("^[a-z]+/(\\d+)-[-_0-9a-z]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find())
            return matcher.group(1);

        pattern = Pattern.compile("^(\\d+)-[-_0-9a-z]+$", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(s);
        if (matcher.find())
            return matcher.group(1);

        return "";
    }
}
