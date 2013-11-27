package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class OkHttpJerseyClientBuilder {

    protected OkHttpClient okHttpClient = new OkHttpClient();
    protected SSLContext sslContext;
    protected Proxy proxy;

    protected IoCComponentProviderFactory ioCComponentProviderFactory = null;
    protected ExecutorService executorService;

    protected List<Object> singletons = new ArrayList<Object>();
    protected List<Class<?>> providers = new ArrayList<Class<?>>();
    protected Map<String, Boolean> features = new LinkedHashMap<String, Boolean>();
    protected Map<String, Object> properties = new LinkedHashMap<String, Object>();

    public static OkHttpJerseyClientBuilder okHttpBackedJerseyClient() {
        return new OkHttpJerseyClientBuilder();
    }

    public OkHttpJerseyClientBuilder okHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public OkHttpJerseyClientBuilder sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public OkHttpJerseyClientBuilder sslTrustKeystore(URL keystoreLocation, char[] password) {
        this.sslContext = SslContextFactory.trustKeystore(keystoreLocation, password);
        return this;
    }

    public OkHttpJerseyClientBuilder withProvider(Object provider) {
        singletons.add(provider);
        return this;
    }

    public OkHttpJerseyClientBuilder withProvider(Class<?> klass) {
        providers.add(klass);
        return this;
    }

    public OkHttpJerseyClientBuilder withFeature(String featureName, boolean featureState) {
        features.put(featureName, featureState);
        return this;
    }

    public OkHttpJerseyClientBuilder withProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return this;
    }

    public OkHttpJerseyClientBuilder ioCComponentProviderFactory(IoCComponentProviderFactory ioCComponentProviderFactory) {
        this.ioCComponentProviderFactory = ioCComponentProviderFactory;
        return this;
    }
    
    public OkHttpJerseyClientBuilder executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public OkHttpJerseyClientBuilder proxy(String host, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        return this;
    }

    public OkHttpJerseyClientBuilder proxy(InetAddress host, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        return this;
    }

    public Client build() {
        if (sslContext != null) {
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        }

        URLConnectionClientHandler clientHandler =
                new URLConnectionClientHandler(new OkHttpURLConnectionFactory(okHttpClient));
        Client client = new Client(clientHandler, buildClientConfig(), ioCComponentProviderFactory);
        if (executorService != null) {
            client.setExecutorService(executorService);
        }
        
        return client;
    }

    protected ClientConfig buildClientConfig() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().addAll(singletons);
        clientConfig.getClasses().addAll(providers);
        clientConfig.getFeatures().putAll(features);
        clientConfig.getProperties().putAll(properties);
        return clientConfig;
    }
}
