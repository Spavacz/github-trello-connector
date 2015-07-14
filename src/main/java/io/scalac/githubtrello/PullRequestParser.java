package io.scalac.githubtrello;

import io.scalac.githubtrello.model.TrelloCard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PullRequestParser {

    public String parseTrelloCardShortLink(String s, TrelloCard[] trelloCards) {
        String shortLink = "";
        String cardNumber = parseTrelloCardNumber(s);
        if (!cardNumber.isEmpty()) {
            for (TrelloCard card : trelloCards) {
                if (cardNumber.equals(card.getIdShort())) {
                    shortLink = card.getShortLink();
                    break;
                }
            }
        }
        return shortLink;
    }

    private String parseTrelloCardNumber(String s) {
        Pattern pattern = Pattern.compile("^(\\d+)-[-_0-9a-z]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }
}
