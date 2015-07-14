package io.scalac.githubtrello;

public class PullRequestEvent {

    public final static String ACTION_OPENED = "opened";

    public String action;
    public PullRequest pull_request;

    public static class PullRequest {
        public String html_url;
        public String body;
        public Head head;
    }

    private static class Head {
        public String ref;
    }
}
