package com.github.tomakehurst.okhttpjersey1;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OkHttpJerseyClientTest {

    @ClassRule
    public static WireMockClassRule wm = new WireMockClassRule();

    private Client client;

    @Before
    public void init() {
        client = new OkHttpJerseyClient();
    }

    @Test
    public void supportsBasicGet() {
        stubFor(get(urlEqualTo("/something")).willReturn(
                aResponse().withStatus(200)
                        .withBody("Hello world")
                        .withHeader("X-Some-Header", "hello-header")));

        ClientResponse response = client.resource("http://localhost:8080/something").get(ClientResponse.class);

        assertThat(response.getStatus(), is(200));
        assertThat(response.getEntity(String.class), is("Hello world"));
        assertThat(response.getHeaders().getFirst("X-Some-Headers"), is("hello-header"));
    }

}
