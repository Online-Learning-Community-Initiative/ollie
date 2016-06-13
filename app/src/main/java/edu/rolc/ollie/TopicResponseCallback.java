package edu.rolc.ollie;

import java.util.List;

public interface TopicResponseCallback {
    public abstract void onReceivingResponse(List<String> topics);
}
