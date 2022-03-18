package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.ArrayList;
import java.util.List;

public interface MemberService {
    public Member findByTelephone(String mail);
    public void add(Member member);

    List<Integer> findCountMemberByMonth(List<String> month);
}
