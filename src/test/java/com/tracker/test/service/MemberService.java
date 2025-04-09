package com.tracker.test.service;

import com.tracker.test.domain.Member;
import com.tracker.test.domain.Team;
import com.tracker.test.repository.MemberRepository;
import com.tracker.test.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public MemberService(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * N + 1 테스트
     */
    public void findAll() {
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            for (Member member : team.getMembers()) {
                System.out.println(member.getName());
            }
        }
    }
}
