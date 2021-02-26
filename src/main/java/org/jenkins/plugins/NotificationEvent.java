package org.jenkins.plugins;

public class NotificationEvent {
    public NotificationEvent(String buildName, String buildUrl, int buildNumber, String buildVars,
            String upstreamBuildName, int upstreamBuildNumber, int previousSuccessfulBuild,
            long timestamp, long duration, String event) {
        this.buildName = buildName;
        this.buildUrl = buildUrl;
        this.buildNumber = buildNumber;
        this.buildVars = buildVars;
        this.upstreamBuildName = upstreamBuildName;
        this.upstreamBuildNumber = upstreamBuildNumber;
        this.event = event;
        this.timestamp = timestamp;
        this.duration = duration;
        this.previousSuccessfulBuild = previousSuccessfulBuild;
    }

    public String buildName;
    public String buildUrl;
    public int buildNumber;
    public String buildVars;
    public String event;
    public String upstreamBuildName;
    public int upstreamBuildNumber;
    public long timestamp;
    public long duration;
    public int previousSuccessfulBuild;
}
