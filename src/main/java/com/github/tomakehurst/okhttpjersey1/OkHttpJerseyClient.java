package com.github.tomakehurst.okhttpjersey1;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public class OkHttpJerseyClient extends Client {

    public OkHttpJerseyClient() {
        this(new DefaultClientConfig());
    }

    public OkHttpJerseyClient(ClientConfig config) {
        this(config, null);
    }

    public OkHttpJerseyClient(ClientConfig config, IoCComponentProviderFactory provider) {
        super(createDefaultHandler(), config, provider);
    }

    private static OkHttpJerseyClientHandler createDefaultHandler() {
        return new OkHttpJerseyClientHandler();
    }

}
