package br.ufpb.dcx.apps4society.qtarolando.api.service;

import br.ufpb.dcx.apps4society.qtarolando.api.dto.CredentialsDTO;
import br.ufpb.dcx.apps4society.qtarolando.api.model.UserAccount;
import br.ufpb.dcx.apps4society.qtarolando.api.repository.UserAccountRepository;
import br.ufpb.dcx.apps4society.qtarolando.api.response.LoginResponse;
import br.ufpb.dcx.apps4society.qtarolando.api.security.TokenFilter;
import br.ufpb.dcx.apps4society.qtarolando.api.service.exceptions.ObjectNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Log4j2
public class JWTService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String TOKEN_KEY = "qtarolando-token";

    public LoginResponse authenticate(CredentialsDTO credentialsDTO) throws ObjectNotFoundException {
        UserAccount user = userAccountRepository.findByEmail(credentialsDTO.getEmail());

        if (user == null){
            throw new ObjectNotFoundException("object not found");
        }

        boolean passwordsMatches = passwordEncoder.matches(credentialsDTO.getPassword(), user.getPassword());

        if (passwordsMatches) {
            return new LoginResponse(generateToken(credentialsDTO));
        }
        throw new ObjectNotFoundException("object not found");
    }

    private String generateToken(CredentialsDTO credentialsDTO) {
        return Jwts.builder()
                .setSubject(credentialsDTO.getEmail())
                .signWith(SignatureAlgorithm.HS512, TOKEN_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)).compact();
    }

    public Optional<String> recoverUser(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new SecurityException();
        }

        String token = header.substring(TokenFilter.TOKEN_INDEX);
        String subject;

        try {
            subject = Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody().getSubject();
        } catch (SignatureException error) {
            throw new SecurityException("Token invalid or expired!");
        }

        return Optional.of(subject);
    }
}