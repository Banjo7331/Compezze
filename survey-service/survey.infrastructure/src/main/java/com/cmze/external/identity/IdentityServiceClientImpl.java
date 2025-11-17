//package com.cmze.external.identity;
//
//import com.cmze.spi.identity.IdentityServiceClient;
//import com.cmze.spi.identity.UserDto;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//@Component
//public class IdentityServiceClientImpl implements IdentityServiceClient {
//
//    private final InternalIdentityApi internalApi;
//
//    public IdentityServiceClientImpl(InternalIdentityApi internalApi) {
//        this.internalApi = internalApi;
//    }
//
//    @Override
//    public UserDto getUserByUsername(String username) {
//        try {
//            ResponseEntity<UserDto> response = internalApi.fetchUserByUsername(username);
//            return response.getBody();
//        } catch (Exception e) {
//            throw new RuntimeException("Could not fetch user data for: " + username, e);
//        }
//    }
//
//}
