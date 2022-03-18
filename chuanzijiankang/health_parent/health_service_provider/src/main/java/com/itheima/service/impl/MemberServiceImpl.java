package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.DateUtils;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    //根据手机号查询会员
    public Member findByTelephone(String mail) {
        return memberDao.findByTelephone(mail);
    }

    //保存会员信息
    public void add(Member member) {
        String password = member.getPassword();
        if (password !=null) {
            //使用md5将明文密码进行加密
            password= MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findCountMemberByMonth(List<String> month) {
        List<Integer> MemberCount = new ArrayList<>();
        for (String s : month) {
            String[] split = s.split("\\.");
            String s1 = split[1];
            int i = Integer.parseInt(s1);
            String s2 = split[0];
            String date=null;
            if (i==12){
                int i1 = Integer.parseInt(s2);
                date=(i1+1)+"."+1+".1";
            }else {
                date=s2+"."+(i+1)+".1";
            }
            Integer memberCountBeforeDate = memberDao.findMemberCountBeforeDate(date);
            MemberCount.add(memberCountBeforeDate);
        }
        return MemberCount;
    }
}
