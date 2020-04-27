package com.virnect.workspace.global.constant;

import com.virnect.workspace.dto.MemberInfoDTO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum Sort {
    //member Sort
    EMAIL_ASC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareEmailAsc());
        return memberInfoDTOList;
    }),
    EMAIL_DESC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareEmailDesc());
        return memberInfoDTOList;
    }),
    NICKNAME_ASC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareNickNamAsc());
        return memberInfoDTOList;
    }),
    NICKNAME_DESC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareNickNameDesc());
        return memberInfoDTOList;
    }),
    ROLE_ASC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareRoleAsc());
        return memberInfoDTOList;
    }),
    ROLE_DESC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareRoleDesc());
        return memberInfoDTOList;
    }),
    JOINDATE_ASC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareJoinDateAsc());
        return memberInfoDTOList;
    }),
    JOINDATE_DESC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareJoinDateDesc());
        return memberInfoDTOList;
    });


    private Function<List<MemberInfoDTO>, List<MemberInfoDTO>> expression;

    Sort(Function<List<MemberInfoDTO>, List<MemberInfoDTO>> expression) {
        this.expression = expression;
    }

    public List<MemberInfoDTO> sorting(List<MemberInfoDTO> BeforeSortList) {
        return expression.apply(BeforeSortList);
    }


    /**
     * email으로 MemberInfoDto 내림차순(Desc) 정렬
     *
     * @author Administrator
     */
    static class CompareEmailDesc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o2.getEmail().compareTo(o1.getEmail());
        }
    }

    /**
     * email으로 오름차순(Asc) 정렬
     *
     * @author Administrator
     */
    static class CompareEmailAsc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o1.getEmail().compareTo(o2.getEmail());
        }
    }

    /**
     * name으로 MemberInfoDto 내림차순(Desc) 정렬
     *
     * @author Administrator
     */
    static class CompareNickNameDesc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o2.getNickName().compareTo(o1.getNickName());
        }
    }

    /**
     * name으로 오름차순(Asc) 정렬
     *
     * @author Administrator
     */
    static class CompareNickNamAsc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o1.getNickName().compareTo(o2.getNickName());
        }
    }
    /**
     * workspace user role으로 MemberInfoDto 내림차순(Desc) 정렬
     *
     * @author Administrator
     */
    static class CompareRoleDesc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o2.getRole().compareTo(o1.getRole());
        }
    }

    /**
     * workspace user role으로 오름차순(Asc) 정렬
     *
     * @author Administrator
     */
    static class CompareRoleAsc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o1.getRole().compareTo(o2.getRole());
        }
    }
    /**
     * joinDate 으로 MemberInfoDto 내림차순(Desc) 정렬
     *
     * @author Administrator
     */
    static class CompareJoinDateDesc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o2.getJoinDate().compareTo(o1.getJoinDate());
        }
    }

    /**
     * joinDate 으로 오름차순(Asc) 정렬
     *
     * @author Administrator
     */
    static class CompareJoinDateAsc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o1.getJoinDate().compareTo(o2.getJoinDate());
        }
    }
}

