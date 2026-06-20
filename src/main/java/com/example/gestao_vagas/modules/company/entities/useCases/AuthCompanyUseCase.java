package com.example.gestao_vagas.modules.company.entities.useCases;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.gestao_vagas.modules.company.entities.dto.AuthCompanyDTO;
import com.example.gestao_vagas.modules.company.entities.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.Duration;
import java.time.Instant;

@Service
public class AuthCompanyUseCase {

    @Value("${secutiry.token.secret}")
    private String secretKey;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String execute(AuthCompanyDTO authCompanyDTO){
        var company = this.companyRepository.findByUsername(authCompanyDTO.getUsername()).orElseThrow(
                () -> {
                    throw new UsernameNotFoundException("Company not found");
                });
        var passwordMatchers = this.passwordEncoder.matches(authCompanyDTO.getPassword(), company.getPassword());

        if(!passwordMatchers){
            throw new BadCredentialsException("Username ou senhas invalidas");
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        var token = JWT.create().withIssuer("javagas")
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .withSubject(company.getId()
                    .toString())
                    .sign(algorithm);

        return token;
    }
}
