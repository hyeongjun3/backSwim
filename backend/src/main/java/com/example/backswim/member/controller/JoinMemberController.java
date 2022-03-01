package com.example.backswim.member.controller;


import com.example.backswim.common.api.APIResult;
import com.example.backswim.common.api.enums.StatusEnum;
import com.example.backswim.common.controller.CommonController;
import com.example.backswim.member.params.ChangePasswordParam;
import com.example.backswim.member.params.CheckDuplicateID;
import com.example.backswim.member.params.JoinMemberParam;
import com.example.backswim.member.params.ResetPasswordParam;
import com.example.backswim.member.params.UserEmailParam;
import com.example.backswim.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value="api/joinmember")
@RequiredArgsConstructor
public class JoinMemberController extends CommonController {

    private final UserService userService;

    @PostMapping("/addmember")
    public APIResult<Boolean> joinMember(HttpServletRequest request, JoinMemberParam param){
        if(!param.checkStatus()){
            PrintLog(request);
            return new APIResult<>(400,null, StatusEnum.BAD_REQUEST);
        }
        try{
            if(!userService.joinMember(param)){
                return new APIResult<>(200,false,StatusEnum.OK);
            }
            PrintLog(request);
        }catch(Exception e){
            PrintErrorLog(request);
            e.printStackTrace();
            return new APIResult<>(500, null, StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,true,StatusEnum.OK);
    }

    @PostMapping("/duplicateid")
    public APIResult<Boolean> checkduplicate(HttpServletRequest request,CheckDuplicateID param){
        if(!param.checkStatus()){
            return new APIResult<>(400,null, StatusEnum.BAD_REQUEST);
        }
        try{
            if(!userService.duplicateEmail(param)){ // 중복된 이메일 입니다.
                return new APIResult<>(200,false,StatusEnum.OK);
            }
        }catch(Exception e){
            PrintErrorLog(request);
            return new APIResult<>(500,null,StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,true,StatusEnum.OK);
    }

    @GetMapping("/email-auth")
    public APIResult<Boolean> emailAuth(HttpServletRequest request,String uuid){
        if(uuid == null){
            return new APIResult<>(400,null,StatusEnum.BAD_REQUEST);
        }
        try{
            if(!userService.emailAuth(uuid)){
                return new APIResult<>(200,false,StatusEnum.OK);
            }
            PrintLog(request);
        }catch(Exception e){
            PrintErrorLog(request);
            return new APIResult<>(500,null,StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,true,StatusEnum.OK);
    }

    @GetMapping("/resetpassword")
    public APIResult<Boolean> resetPassword(HttpServletRequest request, ResetPasswordParam param){
        boolean result = false;
        if(!param.checkEmail()){
            return new APIResult<>(400,null,StatusEnum.BAD_REQUEST);
        }
        try{
            PrintLog(request);
            result = userService.sendResetPassword(param);
        } catch(Exception e){
            PrintErrorLog(request);
            return new APIResult<>(500,null,StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,result,StatusEnum.OK);
    }

    @PostMapping("/changepassword")
    public APIResult<Boolean> changePassword(HttpServletRequest request,@RequestBody ChangePasswordParam param){
        boolean result = false;
        if(!param.checkStatus()){
            return new APIResult<>(400,null, StatusEnum.BAD_REQUEST);
        }
        try{
            PrintLog(request);
            result = userService.changePassword(param);
        }catch(TimeoutException e){
            PrintLog(request); // 서버에서 발생한 Error가 아니라 따로 Error Log 로 찍지않음
            return new APIResult<>(400,null,StatusEnum.TIME_OUT);
        }catch(Exception e){
            PrintErrorLog(request);
            return new APIResult<>(500,null,StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,result,StatusEnum.OK);
    }

    @GetMapping("/resend-mail")
    public APIResult<Boolean> resendEmail(HttpServletRequest request , UserEmailParam param){
        boolean result = false;
        if(!param.checkStatus()){
            return new APIResult<>(400,null, StatusEnum.BAD_REQUEST);
        }
        try{
            PrintLog(request);
            result = userService.resendEmaiAuthEmail(param);
        }catch(Exception e){
            return new APIResult<>(500,null,StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,result,StatusEnum.OK);
    }


    @DeleteMapping("/tempdeleteuser")
    public APIResult<Boolean> deleteUser(HttpServletRequest request,UserEmailParam param){
        boolean result = false;
        if(!param.checkStatus()){
            return new APIResult<>(400 ,null ,StatusEnum.BAD_REQUEST);
        }
        try{
            PrintLog(request);
            result = userService.deleteUser(param);
        }catch(Exception e){
            return new APIResult<>(500,null,StatusEnum.INTERNAL_SERVER_ERROR);
        }
        return new APIResult<>(200,result,StatusEnum.OK);
    }





}
