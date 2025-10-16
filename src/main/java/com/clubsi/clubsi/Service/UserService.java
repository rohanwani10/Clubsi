package com.clubsi.clubsi.Service;


import com.clubsi.clubsi.Entity.User;
import com.clubsi.clubsi.Repository.UserRepository;
import com.clubsi.clubsi.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwt;

    //    Register
    public ResponseEntity<String> registerUser(User user) {

        if (user.getEmail() == null || user.getPassword() == null || user.getUsername() == null)
            return ResponseEntity.status(400).body("Invalid User");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        user.getRoles().add(user.getRoles().toString());

        User resp = userRepository.save(user);
        String s = "{\"message\":\"" + resp.getId() + "\"}";
        return ResponseEntity.status(200).body(s);
    }

    //    Login
    public ResponseEntity<String> signin(User user) {

        try {
            authenticationManager.authenticate((new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())));
            User userByName = userRepository.findByUsername(user.getUsername());
            String s = jwt.generateJwtToken(userByName.getUsername(),userByName.getRoles());

            String jwt = "{\"token\":\"" + s + "\"}";

            return ResponseEntity.status(200).body(jwt);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

    }

}


