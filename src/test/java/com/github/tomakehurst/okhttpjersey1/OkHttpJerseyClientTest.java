package com.github.tomakehurst.okhttpjersey1;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.RequestMethod.*;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OkHttpJerseyClientTest {

    @Rule
    public WireMockClassRule wm = new WireMockClassRule();

    private Client client;

    @Before
    public void init() {
        client = new OkHttpJerseyClient();
    }

    @Test
    public void supportsBasicGet() {
        stubGetWillReturn(
                aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "text/plain")
                        .withBody("Hello world"));

        ClientResponse response = resource().get(ClientResponse.class);

        assertThat(response.getStatus(), is(200));
        assertThat(response.getEntity(String.class), is("Hello world"));
    }

    @Test
    public void supportsArbitraryResponseHeaders() {
        stubGetWillReturn(aResponse().withStatus(200).withHeader("X-Some-Header", "hello-header"));

        ClientResponse response = resource().get(ClientResponse.class);

        assertThat(response.getHeaders().getFirst("X-Some-Header"), is("hello-header"));
    }

    @Test
    public void supportsSingleValuedRequestHeaders() {
        stubGetWillReturn(aResponse().withStatus(200));

        resource().header("X-Request-Header", "request-value").get(ClientResponse.class);

        verify(getRequestedFor(urlEqualTo("/something")).withHeader("X-Request-Header", equalTo("request-value")));
    }

    @Test
    public void supportsMultiValuedRequestHeaders() {
        stubGetWillReturn(aResponse().withStatus(200));

        resource().header("X-Multi-Request-Header", "request-value-1")
                .header("X-Multi-Request-Header", "request-value-2")
                .header("X-Multi-Request-Header", "request-value-3")
                .get(ClientResponse.class);

        List<LoggedRequest> requests = findAll(getRequestedFor(urlEqualTo("/something")));
        HttpHeader header = requests.get(0).getHeaders().getHeader("X-Multi-Request-Header");
        assertThat(header.values(), hasItems("request-value-1", "request-value-2", "request-value-3"));
    }

    @Test
    public void supportsHead() {
        testSupportFor(HEAD);
    }

    @Test
    public void supportsDelete() {
        testSupportFor(DELETE);
    }

    @Test
    public void supportsOptions() {
        testSupportFor(OPTIONS);
    }

    @Test
    public void supportsPost() {
        testSupportFor(POST, "Post body");
    }

    @Test
    public void supportsPut() {
        testSupportFor(PUT, "Put body");
    }

    private void testSupportFor(RequestMethod method) {
        stubWillReturn(method, aResponse()
                .withStatus(200)
                .withHeader("X-Request-Header", "req-header-value"));

        ClientResponse response = handle(method);

        assertThat(response.getStatus(), is(200));
        assertThat(response.getHeaders().getFirst("X-Request-Header"), is("req-header-value"));
    }

    private void testSupportFor(RequestMethod method, String body) {
        stubWillReturn(method, aResponse()
                .withStatus(200)
                .withHeader("X-Request-Header", "req-header-value"));

        ClientResponse response = handleWithBody(method, body);

        assertThat(response.getStatus(), is(200));
        assertThat(response.getHeaders().getFirst("X-Request-Header"), is("req-header-value"));

        List<LoggedRequest> requests = findAll(new RequestPatternBuilder(ANY, urlEqualTo("/something")));
        assertThat(requests.get(0).getBodyAsString(), is(body));
    }

    private ClientResponse handle(RequestMethod method) {
        switch (method) {
            case GET:
                return resource().get(ClientResponse.class);
            case POST:
                return resource().post(ClientResponse.class);
            case PUT:
                return resource().put(ClientResponse.class);
            case DELETE:
                return resource().delete(ClientResponse.class);
            case HEAD:
                return resource().head();
            case OPTIONS:
                return resource().options(ClientResponse.class);
            default:
                throw new IllegalArgumentException("Unable to execute " + method);
        }
    }

    private ClientResponse handleWithBody(RequestMethod method, String body) {
        switch (method) {
            case POST:
                return resource().post(ClientResponse.class, body);
            case PUT:
                return resource().put(ClientResponse.class, body);
            default:
                throw new IllegalArgumentException("Unable to execute " + method);
        }
    }

    private void stubGetWillReturn(ResponseDefinitionBuilder response) {
        stubWillReturn(GET, response);
    }

    private WebResource resource() {
        return client.resource("http://localhost:8080/something");
    }

    private void stubWillReturn(RequestMethod method, ResponseDefinitionBuilder response) {
        stubFor(new MappingBuilder(method, urlEqualTo("/something")).willReturn(response));
    }

}
