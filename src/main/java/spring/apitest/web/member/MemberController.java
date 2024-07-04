package spring.apitest.web.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import spring.apitest.Repository.MySqlMemberRepository;
import spring.apitest.domain.member.Member;
import spring.apitest.domain.member.MemberRepository;

@Controller
@RequiredArgsConstructor
public class MemberController {

    //private final MemberRepository memberRepository;
    private final MySqlMemberRepository memberRepository;
    @GetMapping("/members/add")
    public String addMemberForm(@ModelAttribute("member")Member member){
        return "addMemberForm";
    }

    @PostMapping("/members/add")
    public String addMember(@Validated  @ModelAttribute("member") Member member, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "addMemberForm";
        }

        memberRepository.save(member);
        return "redirect:/";
    }
}
