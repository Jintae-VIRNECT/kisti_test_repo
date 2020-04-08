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
    NAME_ASC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareNameAsc());
        return memberInfoDTOList;
    }),
    NAME_DESC(memberInfoDTOList -> {
        Collections.sort(memberInfoDTOList, new CompareNameDesc());
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
    static class CompareNameDesc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o2.getName().compareTo(o1.getName());
        }
    }

    /**
     * name으로 오름차순(Asc) 정렬
     *
     * @author Administrator
     */
    static class CompareNameAsc implements Comparator<MemberInfoDTO> {

        @Override
        public int compare(MemberInfoDTO o1, MemberInfoDTO o2) {
            // TODO Auto-generated method stub
            return o1.getName().compareTo(o2.getName());
        }
    }

}

