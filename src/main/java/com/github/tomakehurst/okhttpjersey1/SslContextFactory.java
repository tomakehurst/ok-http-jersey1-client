package com.github.tomakehurst.okhttpjersey1;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import static com.github.tomakehurst.okhttpjersey1.Exceptions.throwUnchecked;

public final class SslContextFactory {

    public static SSLContext trustKeystore(URL keystoreLocation, char[] password) {
        KeyStore keyStore = loadKeyStore(keystoreLocation, password);

        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(keyStore, password);
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            return throwUnchecked(e, SSLContext.class);
        }
    }

    private static KeyStore loadKeyStore(URL location, char[] password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = location.openStream();
            keyStore.load(in, password);
            return keyStore;
        } catch (Exception e) {
            return throwUnchecked(e, KeyStore.class);
        }
    }
}

