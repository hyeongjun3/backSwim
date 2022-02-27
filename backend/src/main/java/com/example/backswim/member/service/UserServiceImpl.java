package com.example.backswim.member.service;

import com.example.backswim.component.JwtComponent;
import com.example.backswim.component.MailComponents;
import com.example.backswim.configuration.UserSecurityAdapter;
import com.example.backswim.member.entity.EmailEntity;
import com.example.backswim.member.entity.UserEntity;
import com.example.backswim.member.exception.MemberNotEmailAuthException;
import com.example.backswim.member.params.ChangePasswordParam;
import com.example.backswim.member.params.CheckDuplicateID;
import com.example.backswim.member.params.JoinMemberParam;
import com.example.backswim.member.params.ResetPasswordParam;
import com.example.backswim.member.params.login.LoginRequestParam;
import com.example.backswim.member.repository.EmailRepository;
import com.example.backswim.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeoutException;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final MailComponents mailComponents;

    private final EmailRepository emailRepository;

    private final JwtComponent jwtComponent;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;



    @Override
    public boolean joinMember(JoinMemberParam param) {
        Optional<UserEntity> optionalUser = userRepository.findByUserEmail(param.getUserEmail());

        if(optionalUser.isPresent()){
            return false;
        }

        String encPassword = BCrypt.hashpw(param.getPassword(),BCrypt.gensalt());

        String uuid = UUID.randomUUID().toString();

        UserEntity userEntity = UserEntity.builder()
                .userEmail(param.getUserEmail())
                .emailAuthYn(false)
                .password(encPassword)
                .emailAuthKey(uuid)
                .regdate(LocalDateTime.now())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        userRepository.save(userEntity);

        UserEntity tempEntity = userRepository.findByUserEmail(param.getUserEmail()).get();

        EmailEntity emailEntity = EmailEntity.builder()
                .userEmail(userEntity.getUserEmail())
                .EmailTitle("BackSwim 가입 인증 이메일")
                .userId(userEntity.getSeq())
                .EmailContent("<p>"+" BackSwim 사이트 가입을 축하드립니다.</p>\n<p>아래 링크를 클릭하셔서 가입을 완료하세요.</p>\n"
                        + "<div><a target='_blank' href='http://localhost:3000/emailauth?uuid="+uuid+"'>가입완료</a></div>").build();

        emailRepository.save(emailEntity);

        mailComponents.sendMail(emailEntity.getSeq());

        return true;
    }

    @Override
    public boolean duplicateEmail(CheckDuplicateID param) {

        Optional<UserEntity> optionalUser = userRepository.findByUserEmail(param.getUserEmail());

        if(optionalUser.isPresent()){
            return false;
        }

        return true;
    }

    @Override
    public boolean emailAuth(String uuid) {
        Optional<UserEntity> optionalUser = userRepository.findByEmailAuthKey(uuid);

        if(optionalUser.isEmpty()){
            return false;
        }
        UserEntity userEntity = optionalUser.get();

        userEntity.setEmailAuthYn(true);
        userEntity.setEmailAuthKey(null);
        userEntity.setEmailAuthDate(LocalDateTime.now());

        userRepository.save(userEntity);

        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordParam param) {
        Optional<UserEntity> optionalUser = userRepository.findByUserEmail(param.getUserEmail());

        if(optionalUser.isEmpty()){
            return false;
        }

        UserEntity userEntity = optionalUser.get();

        String uuid = UUID.randomUUID().toString();

        userEntity.setResetPasswordKey(uuid);
        userEntity.setResetPasswordLimitDt(LocalDateTime.now().plusMinutes(30)); //회원 인증 만료 시간 30분

        userRepository.save(userEntity);

        EmailEntity emailEntity = EmailEntity.builder()
                .EmailTitle(userEntity.getUserEmail()+"님 BackSwim 비밀번호 초기화 이메일입니다. ")
                .userEmail(userEntity.getUserEmail())
                .userId(userEntity.getSeq())
                .EmailContent("<p>"+" BackSwim 사이트 비밀번호 초기화 이메일입니다. </p>\n<p>아래 링크를 클릭하셔서 비밀번호를 변경해주세요</p>\n"
                        + "<div><a target='_blank' href='http://localhost:8080/api/joinmember/resetpassword?uuid="+uuid+"'>비밀번호 변경</a></div>").build();

        emailEntity = emailRepository.save(emailEntity);

        mailComponents.sendMail(emailEntity.getSeq());

        return true;
    }

    @Override
    public boolean changePassword(ChangePasswordParam param) throws TimeoutException {
        Optional<UserEntity> optionalUser = userRepository.findByResetPasswordKey(param.getUuid());

        if(optionalUser.isEmpty()){
            return false;
        }

        UserEntity userEntity = optionalUser.get();

        if(userEntity.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new TimeoutException("시간 만료");
        }else if(userEntity.getResetPasswordLimitDt() == null){
            return false;
        }

        String encPassword = BCrypt.hashpw(param.getPassword(),BCrypt.gensalt());

        userEntity.setResetPasswordLimitDt(null);
        userEntity.setResetPasswordKey(null);
        userEntity.setPassword(encPassword);

        userRepository.save(userEntity);

        return true;
    }

    @Override
    public String userLogin(LoginRequestParam param) {
        Optional<UserEntity> optionalUser = userRepository.findByUserEmail(param.getUserEmail());
        if(optionalUser.isEmpty()){
            return null;
        }
        UserEntity userEntity = optionalUser.get();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(!passwordEncoder.matches(param.getPassword(),userEntity.getPassword())){
            return null;
        }

        getAuthentication(param.getUserEmail(),param.getPassword());

        return jwtComponent.generateJwtToken(userEntity);
    }

    private void getAuthentication(String email , String password){
        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //password = passwordEncoder.encode(password);
        //UsernamePasswordAuthenticationToken authenticationToken =
        //        new UsernamePasswordAuthenticationToken(email, password);
        //authenticationManager.authenticate(authenticationToken);
        userDetailsService.loadUserByUsername(email);
    }

}
