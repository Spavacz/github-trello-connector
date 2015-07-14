package io.scalac.githubtrello.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloCard {
    public String idShort;
    public String shortLink;

    public String getIdShort() {
        return idShort;
    }

    public String getShortLink() {
        return shortLink;
    }
}
