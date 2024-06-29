package ru.gb.SpringTesting.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.SpringTesting.entity.UserEntity;
import ru.gb.SpringTesting.repository.UserRepository;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/register")
    public String register(@RequestBody RegistrationRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAuthority(request.authority);

        userRepository.save(user);

        return "New user successfully registered";
    }

    record RegistrationRequest(String username, String password, String authority) {

    }
}
