package com.edu.services;

import com.edu.models.entities.Account;
import com.edu.models.entities.Role;
import com.edu.models.entities.User;
import com.edu.repositories.AccountRepository;
import com.edu.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<Account> findByLogin(String login) {
        return accountRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь %s не найден", username)
        ));

        return new org.springframework.security.core.userdetails.User(
                account.getLogin(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority(account.getUser().getRole().toString()))
        );
    }

    public void createNewAccount(Map<String, Object> request) {
        User user = User.builder()
                .firstName(request.get("firstName").toString())
                .lastName(request.get("lastName").toString())
                .patronymic(request.get("patronymic").toString())
                .phoneNumber(request.get("phoneNumber").toString())
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        Account account = Account.builder()
                .login(request.get("login").toString())
                .password(passwordEncoder.encode(request.get("password").toString()))
                .user(user)
                .build();

        accountRepository.save(account);
    }
}
