package com.yayal.YaYa.Wallet.data;

import lombok.Data;
@Data
public class WebhookPayload {
    private String id;
    private int amount;
    private String currency;
    private long createdAtTime;
    private long timestamp;
    private String cause;
    private String fullName;
    private String accountName;
    private String invoiceUrl;
    private String signature;
}