package com.cmze.external.identity;

import com.cmze.configuration.FeignConfig;
import com.cmze.spi.identity.IdentityServiceClient;
import com.cmze.spi.identity.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class IdentityServiceClientImpl implements IdentityServiceClient {

    // 1. Wstrzykujemy "magiczny" interfejs Feign
    private final InternalIdentityApi internalApi;

    public IdentityServiceClientImpl(InternalIdentityApi internalApi) {
        this.internalApi = internalApi;
    }

    // 2. Definiujemy, co robi nasz interfejs portu
    @Override
    public UserDto getUserByUsername(String username) {
        try {
            // Wywołujemy Feign, pobieramy ciało odpowiedzi
            ResponseEntity<UserDto> response = internalApi.fetchUserByUsername(username);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch user data for: " + username, e);
        }
    }

    // 3. Wewnętrzny interfejs Feign (Spring stworzy jego implementację)
    @FeignClient(
            name = "auth-service", // Nazwa serwisu w Eurece (poprawna, z myślnikiem)
            path = "/users",        // Prefix kontrolera w identity-service
            configuration = FeignConfig.class // Konfiguracja do przekazywania JWT
    )
    interface InternalIdentityApi {
        @GetMapping("/by-username/{username}")
        ResponseEntity<UserDto> fetchUserByUsername(@PathVariable("username") String username);

    }
}
