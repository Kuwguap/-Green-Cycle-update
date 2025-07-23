package com.greencycle.Backend.bin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

@Service
public class IpfsService {
    @Value("${pinata.api.key}")
    private String pinataApiKey;

    @Value("${pinata.api.secret}")
    private String pinataApiSecret;

    private static final String PINATA_URL = "https://api.pinata.cloud/pinning/pinFileToIPFS";

    public String uploadImage(MultipartFile file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("pinata_api_key", pinataApiKey);
        headers.set("pinata_secret_api_key", pinataApiSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(PINATA_URL, requestEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            // Extract IPFS hash from response JSON
            String respBody = response.getBody();
            String hash = respBody != null && respBody.contains("IpfsHash") ?
                respBody.split("\"IpfsHash\":\"")[1].split("\"")[0] : null;
            if (hash != null) {
                return "https://gateway.pinata.cloud/ipfs/" + hash;
            }
        }
        throw new IOException("Failed to upload to IPFS: " + response.getBody());
    }
} 