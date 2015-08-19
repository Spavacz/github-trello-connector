package io.scalac.githubtrello.model;

public class PullRequestEvent {

    public final static String ACTION_OPENED = "opened";
    public String action;
    public PullRequest pull_request;

    public String getAction() {
        return action;
    }

    public PullRequest getPullRequest() {
        return pull_request;
    }

    public static class PullRequest {
        public String html_url;
        public String body;
        public Head head;

        public String getHtmlUrl() {
            return html_url;
        }

        public String getBody() {
            return body;
        }

        public String getBranchName() {
            return head.getRef();
        }
    }

    public static class Head {
        public String ref;

        public String getRef() {
            return ref;
        }
    }
}
