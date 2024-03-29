package com.example.backswim.member.params;

import com.example.backswim.common.params.CheckInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailParam implements CheckInterface {


    private String userEmail;

    @Override
    public boolean checkStatus() {

        if(userEmail != null && checkEmail()) return true;
        return false;
    }

    public boolean checkEmail(){
        String regex = "[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(userEmail);
        if(matcher.matches()){
            return true;
        }
        return false;
    }
}
