package com.example.backswim.member.params;


import com.example.backswim.common.params.CheckInterface;
import lombok.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinMemberParam extends UserEmailParam {


    private String password;

    @Override
    public boolean checkStatus() {

        if(this.getUserEmail() == null || !checkEmail() || password == null) return false;
        return true;
    }

    public JoinMemberParam(String email ,String password){
        super(email);
        this.password = password;
    }

}
