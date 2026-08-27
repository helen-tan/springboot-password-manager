package com.personal.springboot_password_manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "credentials")
public class Credential {
    @Id
    private String id;

    private String credentialId;

    private String userId;

    private String userName;

    private String encryptedPassword;

    private String iv;

    private String siteUrl;

    private String siteName;

    private Long createdAt;

    private Long updatedAt;

    public Credential() {
    };

    public Credential(String credentialId, String userId, String userName, String encryptedPassword,
            String iv, String siteUrl, String siteName, Long createdAt, Long updatedAt) {
        this.credentialId = credentialId;
        this.userId = userId;
        this.userName = userName;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.siteUrl = siteUrl;
        this.siteName = siteName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
