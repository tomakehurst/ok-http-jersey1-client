package com.github.tomakehurst.okhttpjersey1;

import com.squareup.okhttp.OkHttpClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public class OkHttpJerseyClient extends Client {

    public OkHttpJerseyClient() {
        this(new DefaultClientConfig());
    }

    public OkHttpJerseyClient(OkHttpClient okHttpClient) {
        this(new DefaultClientConfig(), okHttpClient);
    }

    public OkHttpJerseyClient(ClientConfig config) {
        this(config, (IoCComponentProviderFactory) null);
    }

    public OkHttpJerseyClient(ClientConfig config, OkHttpClient okHttpClient) {
        this(config, null, okHttpClient);
    }

    public OkHttpJerseyClient(ClientConfig config, IoCComponentProviderFactory provider) {
        super(createDefaultHandler(), config, provider);
    }

    public OkHttpJerseyClient(ClientConfig config, IoCComponentProviderFactory provider, OkHttpClient okHttpClient) {
        super(createDefaultHandler(okHttpClient), config, provider);
    }

    private static OkHttpJerseyClientHandler createDefaultHandler(OkHttpClient okHttpClient) {
        return new OkHttpJerseyClientHandler(okHttpClient);
    }

    private static OkHttpJerseyClientHandler createDefaultHandler() {
        return new OkHttpJerseyClientHandler();
    }

}
