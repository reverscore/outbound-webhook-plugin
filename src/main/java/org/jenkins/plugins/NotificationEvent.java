package org.jenkins.plugins;

public class NotificationEvent {
    public NotificationEvent(String projectName, String buildName, String buildUrl, String externalizableId, String buildVars, String event) {
        this.projectName = projectName;
        this.buildName = buildName;
        this.buildUrl = buildUrl;
        this.buildVars = buildVars;
        this.event = event;
    }

    public String projectName;
    public String buildName;
    public String buildUrl;
    public String externalizableId;
    public String buildVars;
    public String event;
}
