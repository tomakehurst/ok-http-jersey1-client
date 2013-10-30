package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

import javax.net.ssl.SSLContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class JerseyClientBuilder {

    private OkHttpClient okHttpClient = new OkHttpClient();
    private SSLContext sslContext;
    private IoCComponentProviderFactory ioCComponentProviderFactory = null;
    private ExecutorService executorService;
    
    private ClientConfig clientConfig = new DefaultClientConfig();
    private List<Object> singletons = new ArrayList<Object>();
    private List<Class<?>> providers = new ArrayList<Class<?>>();
    private Map<String, Boolean> features = new LinkedHashMap<String, Boolean>();
    private Map<String, Object> properties = new LinkedHashMap<String, Object>();

    public static JerseyClientBuilder okHttpBackedJerseyClient() {
        return new JerseyClientBuilder();
    }

    public JerseyClientBuilder okHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public JerseyClientBuilder sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public JerseyClientBuilder sslTrustKeystore(URL keystoreLocation, char[] password) {
        this.sslContext = SslContextFactory.trustKeystore(keystoreLocation, password);
        return this;
    }

    public JerseyClientBuilder withProvider(Object provider) {
        singletons.add(provider);
        return this;
    }

    public JerseyClientBuilder withProvider(Class<?> klass) {
        providers.add(klass);
        return this;
    }

    public JerseyClientBuilder withFeature(String featureName, boolean featureState) {
        features.put(featureName, featureState);
        return this;
    }

    public JerseyClientBuilder withProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return this;
    }

    public JerseyClientBuilder ioCComponentProviderFactory(IoCComponentProviderFactory ioCComponentProviderFactory) {
        this.ioCComponentProviderFactory = ioCComponentProviderFactory;
        return this;
    }
    
    public JerseyClientBuilder executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public Client build() {
        clientConfig.getSingletons().addAll(singletons);
        clientConfig.getClasses().addAll(providers);
        clientConfig.getFeatures().putAll(features);
        clientConfig.getProperties().putAll(properties);

        if (sslContext != null) {
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        }

        URLConnectionClientHandler clientHandler =
                new URLConnectionClientHandler(new OkHttpURLConnectionFactory(okHttpClient));
        Client client = new Client(clientHandler, clientConfig, ioCComponentProviderFactory);
        if (executorService != null) {
            client.setExecutorService(executorService);
        }
        
        return client;
    }
}
