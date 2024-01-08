package com.yayal.YaYa.Wallet.controller;

import com.yayal.YaYa.Wallet.data.WebhookPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.StringJoiner;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private static final String SECRET_KEY = "test_key";

    @PostMapping
    public ResponseEntity<String> handleWebhookEvent(@RequestBody WebhookPayload payload) {
        if (!verifySignature(payload)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        if (!verifyTimestamp(payload)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid timestamp");
        }

        /**
         Process the webhook event
         Implement your logic here
         */

        return ResponseEntity.ok("Webhook event received and processed successfully");
    }

    private boolean verifySignature(WebhookPayload payload) {
        String signedPayload = createSignedPayload(payload);
        String expectedSignature = generateSignature(signedPayload, SECRET_KEY);
        String receivedSignature = payload.getSignature();
        return receivedSignature.equals(expectedSignature);
    }


    private String createSignedPayload(WebhookPayload payload) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new StringJoiner("").add(payload.getId()).add(String.valueOf(payload.getAmount())).add(payload.getCurrency()).add(dateFormat.format(new Date(payload.getCreatedAtTime() * 1000))).add(dateFormat.format(new Date(payload.getTimestamp() * 1000))).add(payload.getCause()).add(payload.getFullName()).add(payload.getAccountName()).add(payload.getInvoiceUrl()).toString();
    }

    private boolean verifyTimestamp(WebhookPayload payload) {
        long currentTimestamp = System.currentTimeMillis() / 1000;
        long receivedTimestamp = payload.getTimestamp();

        // 5 minutes to tolerance

        long tolerance = 300;
        return (currentTimestamp - receivedTimestamp) <= tolerance;

    }

    private String generateSignature(String signedPayload, String secretKey) {
        Mac sha256Hmac;
        try {
            sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] signatureBytes = sha256Hmac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
