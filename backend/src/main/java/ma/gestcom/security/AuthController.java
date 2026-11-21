package ma.gestcom.security;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.utilisateur.dto.UtilisateurResponse;
import ma.gestcom.modules.utilisateur.service.UtilisateurService;
import ma.gestcom.security.dto.ChangeOwnPasswordRequest;
import ma.gestcom.security.dto.LoginRequest;
import ma.gestcom.security.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurService utilisateurService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UtilisateurResponse user = utilisateurService.getByUsername(auth.getName());
        JwtService.IssuedToken token = jwtService.generate(user.username(), user.role().name());
        return new LoginResponse(token.token(), token.expiresAt(), user);
    }

    @GetMapping("/me")
    public UtilisateurResponse me(Authentication authentication) {
        return utilisateurService.getByUsername(authentication.getName());
    }

    @PutMapping("/mot-de-passe")
    public ResponseEntity<Void> changeOwnPassword(Authentication authentication,
                                                  @Valid @RequestBody ChangeOwnPasswordRequest request) {
        utilisateurService.changeOwnPassword(authentication.getName(), request.ancienMotDePasse(),
                request.nouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }
}
