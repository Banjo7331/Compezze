//package com.cmze.external.identity;
//
//import com.cmze.configuration.FeignConfig;
//import com.cmze.spi.identity.UserDto;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@FeignClient(
//        name = "auth-service",
//        path = "/users",
//        configuration = FeignConfig.class
//)
//public interface InternalIdentityApi {
//    @GetMapping("/{usernameOrEmail}")
//    ResponseEntity<UserDto> fetchUserByUsername(@PathVariable("usernameOrEmail") String username);
//
//}
