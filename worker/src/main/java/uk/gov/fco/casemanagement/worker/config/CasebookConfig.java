package uk.gov.fco.casemanagement.worker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.FeeService;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
@Slf4j
public class CasebookConfig {

    private CasebookProperties properties;

    @Autowired
    public CasebookConfig(@NonNull CasebookProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Qualifier("feeServices")
    public Map<String, FeeService> feeServices(ObjectMapper objectMapper,
                                               ResourceLoader resourceLoader) throws IOException {
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                .getResources("classpath:/fee-services/*");

        return Stream.of(resources)
                .map(resource -> {
                    try {
                        return objectMapper.readValue(resource.getInputStream(), FeeService.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error reading in template file", e);
                    }
                })
                .collect(toMap(FeeService::getName, feeService -> feeService));
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        if (isNotBlank(properties.getClientCertificate())
                && isNotBlank(properties.getClientKey())) {
            try (
                    InputStream in = new ByteArrayInputStream(properties.getClientCertificate().getBytes())
            ) {
                X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509")
                        .generateCertificate(in);

                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodePKCS8Key(properties.getClientKey()));
                PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

                char[] password = "password".toCharArray();

                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                keyStore.setKeyEntry("key", key, password, new Certificate[]{certificate});

                SSLContext sslContext = SSLContextBuilder.create()
                        .loadKeyMaterial(keyStore, password)
                        .build();

                HttpClient httpClient = HttpClients.custom()
                        .setSSLContext(sslContext)
                        .build();

                requestFactory.setHttpClient(httpClient);
            } catch (Exception e) {
                throw new RuntimeException("Unable to configure client certificate", e);
            }
        }
        return new RestTemplate(requestFactory);
    }

    private byte[] decodePKCS8Key(String key) {
        String base64Data = key.replaceFirst("-----BEGIN PRIVATE KEY-----\n", "")
                .replaceFirst("\n-----END PRIVATE KEY-----", "");
        return Base64.decodeBase64(base64Data);
    }
}
