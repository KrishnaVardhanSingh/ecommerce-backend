package krishna.ecommerce.services;

import krishna.ecommerce.dto.user.UserRequest;
import krishna.ecommerce.dto.user.UserResponse;
import krishna.ecommerce.entity.User;
import krishna.ecommerce.exception.user.EmailAlreadyExist;
import krishna.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static krishna.ecommerce.entity.Role.ADMIN;
import static krishna.ecommerce.entity.Role.CUSTOMER;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // adding the user
    public UserResponse createUser(UserRequest request){
        // check email because we need unique email
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExist("Email already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setRole(CUSTOMER);
        user.setEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return new UserResponse(user.getName() + " registered Successfully");
    }
}
