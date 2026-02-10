package com.n1netails.n1netails.api.service;

/**
 * Service responsible for encrypting and decrypting sensitive data.
 *
 * <p>
 * Offers symmetric encryption to secure sensitive values during storage or transfer.
 * </p>
 *
 * <p>
 * Implementations must ensure that encryption and decryption are
 * reversible and use a consistent secret key configuration.
 * </p>
 */
public interface EncryptionService {

    /**
     * Encrypts the given plain text data.
     *
     * <p>
     * The returned value is a Base64-encoded representation of the
     * encrypted data and can be safely stored or transmitted.
     * </p>
     *
     * @param data the plain text data to encrypt
     * @return the encrypted representation of the data
     * @throws Exception if the data cannot be encrypted due to
     *                   configuration or cryptographic errors
     */
    String encrypt(String data) throws Exception;


    /**
     * Decrypts previously encrypted data.
     *
     * <p>
     * The input value must be a valid encrypted representation
     * produced by the {@link #encrypt(String)} operation.
     * </p>
     *
     * @param encryptedData the encrypted data to decrypt
     * @return the decrypted plain text value
     * @throws Exception if the data cannot be decrypted due to
     *                   invalid input or cryptographic errors
     */
    String decrypt(String encryptedData) throws Exception;
}
