package org.jenkins.plugins;

public class NotificationEvent {
    public NotificationEvent(String projectName, String buildName, String buildUrl, int buildNumber, String buildVars,
            String upstreamBuilds, String rootBuildName, int rootBuildNumber, int previousSuccessfulBuild,
            long timestamp, long duration, String event) {
        this.projectName = projectName;
        this.buildName = buildName;
        this.buildUrl = buildUrl;
        this.buildNumber = buildNumber;
        this.buildVars = buildVars;
        this.upstreamBuilds = upstreamBuilds;
        this.rootBuildName = rootBuildName;
        this.rootBuildNumber = rootBuildNumber;
        this.event = event;
        this.timestamp = timestamp;
        this.duration = duration;
        this.previousSuccessfulBuild = previousSuccessfulBuild;
    }

    public String projectName;
    public String buildName;
    public String buildUrl;
    public int buildNumber;
    public String buildVars;
    public String event;
    public String upstreamBuilds;
    public String rootBuildName;
    public int rootBuildNumber;
    public long timestamp;
    public long duration;
    public int previousSuccessfulBuild;
}
