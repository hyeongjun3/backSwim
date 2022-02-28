package com.example.backswim.component;

import com.example.backswim.member.entity.UserEntity;
import com.example.backswim.member.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtComponent {

    @Value("${security.jwt.token.secret-key")
    private String securityKey;// 민감 정보 숨기는 방법

    private final Long expiredTime = 1000 * 60L * 60L * 3L; //3시간

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init(){
        securityKey = Base64.getEncoder().encodeToString(securityKey.getBytes());
    }

    public String generateJwtToken(UserEntity userEntity){
        Date now = new Date();
        return Jwts.builder().setSubject(userEntity.getUserEmail())
                .setHeader(createHeader())
                .setClaims(createClaims(userEntity))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiredTime))
                .signWith(SignatureAlgorithm.HS256,securityKey)
                .compact();
    }

    private Map<String,Object> createHeader(){
        Map<String,Object> header = new HashMap<>();

        header.put("typ","JWT");
        header.put("alg","HS256");
        header.put("regDate",System.currentTimeMillis());

        return header;
    }

    private Map<String,Object> createClaims(UserEntity userEntity){
        Map<String, Object> claims = new HashMap<>();

        claims.put("username",userEntity.getUserEmail());
        return claims;
    }

    //claim 얻기
    private Claims getClaims(String token){
        return Jwts.parser().setSigningKey(securityKey).parseClaimsJws(token).getBody();
    }

    //토큰에서 회원정보 추출
    public String getUsernameFromToken(String token){
        return (String)getClaims(token).get("username");
    }

    //인증 정보 확인
    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);
        String username = (String)claims.get("username");
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request){
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String token){
        Jws<Claims> claimsJws;
        try{
            claimsJws = Jwts.parser().setSigningKey(securityKey).parseClaimsJws(token);

        }catch(Exception e){
            return false;
        }
        return !claimsJws.getBody().getExpiration().before(new Date());
    }






}
