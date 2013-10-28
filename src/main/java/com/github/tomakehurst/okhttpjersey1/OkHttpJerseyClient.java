package com.github.tomakehurst.okhttpjersey1;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public class OkHttpJerseyClient extends Client {

    private OkHttpJerseyClientHandler handler;

    public OkHttpJerseyClient() {
        this(createDefaultHandler());
    }

    public OkHttpJerseyClient(OkHttpJerseyClientHandler root) {
        this(root, new DefaultClientConfig());
    }

    public OkHttpJerseyClient(OkHttpJerseyClientHandler root, ClientConfig config) {
        this(root, config, null);
    }

    public OkHttpJerseyClient(OkHttpJerseyClientHandler root, ClientConfig config, IoCComponentProviderFactory provider) {
        super(root, config, provider);
        this.handler = root;
    }

    private static OkHttpJerseyClientHandler createDefaultHandler() {
        return new OkHttpJerseyClientHandler();
    }

}
