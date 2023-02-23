package com.touchbiz.chatgpt.infrastructure.constants;

public final class CacheConstant {

    private CacheConstant() {
    }

    public final static String CHAT_SESSION_KEY = "chat:session:";

    public final static int CHAT_SESSION_EXPIRE_SECONDS = 1 * 24 * 60 * 60;

    public final static String CHAT_SESSION_SEQUENCE_KEY = "chat:session:sequence:";

    public final static int CHAT_SESSION_SEQUENCE_EXPIRE_SECONDS = 1 * 24 * 60 * 60;

    public final static String CHAT_SESSION_CONTEXT_KEY = "chat:session:context:";

    public final static int CHAT_SESSION_INFO_EXPIRE_SECONDS =  30 * 60;
}
