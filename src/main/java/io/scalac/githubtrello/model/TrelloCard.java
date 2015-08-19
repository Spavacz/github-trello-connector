package io.scalac.githubtrello.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloCard {
    public String idShort;
    public String shortLink;
    public List<String> idChecklists;

    public String getIdChecklist() {
        return hasChecklist() ? idChecklists.get(0) : null;
    }

    public String getIdShort() {
        return idShort;
    }

    public String getShortLink() {
        return shortLink;
    }

    public boolean hasChecklist() {
        return !idChecklists.isEmpty();
    }
}
