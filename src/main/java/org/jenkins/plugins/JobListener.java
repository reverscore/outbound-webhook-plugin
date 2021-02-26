package org.jenkins.plugins;

import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import com.alibaba.fastjson.JSON;
import jenkins.model.Jenkins;
import okhttp3.*;
import hudson.model.Cause.UpstreamCause;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class JobListener extends RunListener<AbstractBuild> {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    private static final Logger log = LoggerFactory.getLogger(JobListener.class);

    public JobListener() {
        super(AbstractBuild.class);
        client = new OkHttpClient();
    }


    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {
        WebHookPublisher publisher = GetWebHookPublisher(build);

        if (publisher == null || !publisher.onStart) {
            return;
        }

        String buildName = build.getProject().getDisplayName();

        Cause.UpstreamCause upCause = (Cause.UpstreamCause) build.getCause(UpstreamCause.class);
        String upstreamBuildName = null;
        int upstreamBuildNumber = 0;

        if (Jenkins.get().getPlugin("promoted-builds") != null && upCause != null) {
            log.info("Promoted builds is installed, sending upstream build on webhook");
            upstreamBuildName = upCause.getUpstreamProject();
            upstreamBuildNumber = upCause.getUpstreamBuild();
        }

        String webHookUrl = publisher.webHookUrl;
        String buildUrl = build.getAbsoluteUrl();

        int buildNumber = build.number;
        String buildVars = build.getBuildVariables().toString();

        long timestamp = build.getTimeInMillis();
        long duration = build.getDuration();
        int previousSuccessfulBuild = build.getPreviousSuccessfulBuild().getNumber();

        NotificationEvent event = new NotificationEvent(buildName, buildUrl, buildNumber, buildVars, upstreamBuildName, upstreamBuildNumber,
                previousSuccessfulBuild, timestamp, duration, "start");
        httpPost(webHookUrl, event);
    }

    @Override
    public void onCompleted(AbstractBuild build, @Nonnull TaskListener listener) {
        WebHookPublisher publisher = GetWebHookPublisher(build);
        if (publisher == null) {
            return;
        }
        Result result = build.getResult();
        if (result == null) {
            return;
        }
        String webHookUrl = publisher.webHookUrl;
        String buildUrl = build.getAbsoluteUrl();
        String buildName = build.getProject().getDisplayName();
        int buildNumber = build.number;
        String buildVars = build.getBuildVariables().toString();

        Cause.UpstreamCause upCause = (Cause.UpstreamCause) build.getCause(UpstreamCause.class);
        String upstreamBuildName = null;
        int upstreamBuildNumber = 0;

        if (Jenkins.get().getPlugin("promoted-builds") != null && upCause != null) {
            log.info("Promoted builds is installed, sending upstream build on webhook");
            upstreamBuildName = upCause.getUpstreamProject();
            upstreamBuildNumber = upCause.getUpstreamBuild();
        }

        long timestamp = build.getTimeInMillis();
        long duration = build.getDuration();
        int previousSuccessfulBuild = build.getPreviousSuccessfulBuild().getNumber();

        NotificationEvent event = new NotificationEvent(buildName, buildUrl, buildNumber, buildVars, upstreamBuildName, upstreamBuildNumber,
            previousSuccessfulBuild, timestamp, duration, "");
        if (publisher.onSuccess && result.equals(Result.SUCCESS)) {
            event.event = "success";
            httpPost(webHookUrl, event);
        }
        if (publisher.onFailure && result.equals(Result.FAILURE)) {
            event.event = "failure";
            httpPost(webHookUrl, event);
        }
        if (publisher.onUnstable && result.equals(Result.UNSTABLE)) {
            event.event = "unstable";
            httpPost(webHookUrl, event);
        }
    }

    private WebHookPublisher GetWebHookPublisher(AbstractBuild build) {
        for (Object publisher : build.getProject().getPublishersList().toMap().values()) {
            if (publisher instanceof WebHookPublisher) {
                return (WebHookPublisher) publisher;
            }
        }
        return null;
    }

    private void httpPost(String url, Object object) {
        String jsonString = JSON.toJSONString(object);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, jsonString);
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            log.debug("Invocation of webhook {} successful", url);
        } catch (Exception e) {
            log.info("Invocation of webhook {} failed", url, e);
        }
    }
}
