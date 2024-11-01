package spring.apitest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import spring.apitest.domain.member.Member;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name="loginMember", required = false) Member loginMember, Model model){

        //세션에 회원 데이터가 없으면 home
        if(loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }


}
