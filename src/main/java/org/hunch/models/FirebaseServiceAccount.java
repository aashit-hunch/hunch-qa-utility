package org.hunch.models;


import lombok.Data;
import lombok.Setter;
import org.hunch.constants.Config;
import org.hunch.utils.Common;
import org.hunch.utils.CryptoUtility;

@Data
public class FirebaseServiceAccount {

    private String type;
    private String project_id;
    private String private_key_id;
    private String private_key;
    private String client_email;
    private String client_id;
    private String auth_uri;
    private String token_uri;
    private String auth_provider_x509_cert_url;
    private String client_x509_cert_url;
    private String apiKey;

    public FirebaseServiceAccount setFirebaseAccount() {
        this.apiKey = CryptoUtility.decrypt(Config.FIREBASE_API_KEY);
        this.type = Config.FIREBASE_TYPE;
        this.project_id = CryptoUtility.decrypt(Config.FIREBASE_PROJECT_ID);
        this.private_key_id = CryptoUtility.decrypt(Config.FIREBASE_PRIVATE_KEY_ID);
        this.private_key = CryptoUtility.decrypt(Config.FIREBASE_PRIVATE_KEY);
        this.client_email = CryptoUtility.decrypt(Config.FIREBASE_CLIENT_EMAIL);
        this.client_id = CryptoUtility.decrypt(Config.FIREBASE_CLIENT_ID);
        this.auth_uri = Config.FIREBASE_AUTH_URI;
        this.token_uri = Config.FIREBASE_TOKEN_URI;
        this.auth_provider_x509_cert_url = Config.FIREBASE_AUTH_PROVIDER_X509_CERT_URL;
        this.client_x509_cert_url = CryptoUtility.encrypt(Config.FIREBASE_CLIENT_X509_CERT_URL);
        return this;
    }

    public FirebaseServiceAccount setFirebaseAccountProd() {
        this.apiKey = CryptoUtility.decrypt(Config.FIREBASE_API_KEY_PROD);
        this.type = Config.FIREBASE_TYPE;
        this.project_id = CryptoUtility.decrypt(Config.FIREBASE_PROJECT_ID_PROD);
        this.private_key_id = CryptoUtility.decrypt(Config.FIREBASE_PRIVATE_KEY_ID_PROD);
        this.private_key = CryptoUtility.decrypt(Config.FIREBASE_PRIVATE_KEY_PROD);
        this.client_email = CryptoUtility.decrypt(Config.FIREBASE_CLIENT_EMAIL_PROD);
        this.client_id = CryptoUtility.decrypt(Config.FIREBASE_CLIENT_ID_PROD);
        this.auth_uri = Config.FIREBASE_AUTH_URI;
        this.token_uri = Config.FIREBASE_TOKEN_URI;
        this.auth_provider_x509_cert_url = Config.FIREBASE_AUTH_PROVIDER_X509_CERT_URL;
        this.client_x509_cert_url = CryptoUtility.encrypt(Config.FIREBASE_CLIENT_X509_CERT_URL_PROD);
        return this;
    }

    @Override
    public String toString() {
        return  Common.mapper.writeValueAsString(this);
    }
}
