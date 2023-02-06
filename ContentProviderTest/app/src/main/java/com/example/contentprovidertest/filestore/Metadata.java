package com.example.contentprovidertest.filestore;

public class Metadata {
    //last messageId added by the app
    public int lastAddedMessageId;

    //last messageId sent successfully via DTN
    public int lastSentMessageId;

    //last messageId received via DTN
    public int lastReceivedMessageId;

    //latest messageId processed by the application
    public int lastProcessedMessageId;

    public Metadata(int lastAddedMessageId, int lastSentMessageId, int lastReceivedMessageId, int lastProcessedMessageId){
        this.lastAddedMessageId = lastAddedMessageId;
        this.lastSentMessageId = lastSentMessageId;
        this.lastReceivedMessageId = lastReceivedMessageId;
        this.lastProcessedMessageId = lastProcessedMessageId;
    }
}
