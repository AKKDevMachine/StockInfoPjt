package spring.apitest.domain.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.apitest.Repository.MySqlMemberRepository;
import spring.apitest.domain.member.Member;
import spring.apitest.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    //private final MemberRepository memberRepository;
    private final MySqlMemberRepository memberRepository;

    /**
     * @return null 로그인 실패
     */

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId).filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}