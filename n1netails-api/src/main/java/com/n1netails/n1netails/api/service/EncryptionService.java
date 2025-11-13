package com.n1netails.n1netails.api.service;

public interface EncryptionService {

    String encrypt(String data) throws Exception;
    String decrypt(String encryptedData) throws Exception;
}
