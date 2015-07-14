package io.scalac.githubtrello;

public class Authenticator {
    private final static String SERVER_TOKEN = System.getProperty("server.token");

    public boolean authenticate(String token) {
        return token.equals(SERVER_TOKEN);
    }
}