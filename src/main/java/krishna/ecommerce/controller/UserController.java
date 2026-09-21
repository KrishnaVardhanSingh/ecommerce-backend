package krishna.ecommerce.controller;

import jakarta.validation.Valid;
import krishna.ecommerce.dto.user.UserRequest;
import krishna.ecommerce.dto.user.UserResponse;
import krishna.ecommerce.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid UserRequest request
            ){
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(201).body(response);
    }
}
