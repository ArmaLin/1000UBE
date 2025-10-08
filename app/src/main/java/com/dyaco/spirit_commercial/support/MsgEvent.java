package com.dyaco.spirit_commercial.support;

public class MsgEvent {
    private int eventType = 0;
    private final Object obj;

    public MsgEvent(int eventType, Object obj) {
        this.eventType = eventType;
        this.obj = obj;
    }

    public MsgEvent(Object obj) {
        this.obj = obj;
    }



    public int getType(){
        return eventType;
    }

    public Object getObj() {
        return obj;
    }
}