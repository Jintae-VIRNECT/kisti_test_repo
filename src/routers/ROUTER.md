# Active URL

## Router 구조

```
/src/routers
|-- service.js
|-- admin.js
|-- extra.js
|-- partials/
    |-- account.js
    |-- policy.js
    `-- mobile.js
```

### service

> 로그인\
`/account/login`

> 회원가입\
`/account/join`

> 비밀번호 찾기\
`/account/findPw`

> 비밀번호 변경\
`/account/changePw`

> 서비스\
`/service`

### admin

> 로그인\
`/admin/login`

> 그룹 비활성화\
`/admin/disable`

> 다운로드\
`/admin/downloads`

> 그룹 관리\
`/admin/groups/group`\
`/admin/groups/group?type=add`\
`/admin/groups/group?type=edit&groupid={groupId}`\
`/admin/groups/group?type=enable&groupid={groupId}`\
`/admin/groups/group?type=remove&groupid={groupId}`\
`/admin/groups/group?type=user&groupid={groupId}`\
`/admin/groups/group?type=add-user&groupid={groupId}`

> 사용자 관리\
`/admin/groups/user`\
`/admin/groups/user?type=edit&id={sId}`\
`/admin/groups/user?type=add-user`\
`/admin/groups/user?type=invite-user`\
`/admin/groups/user?type=full-user`\
`/admin/groups/user?type=remove&id={sId}`

> 전체 메시지\
`/admin/message/whole`\
`/admin/message/whole?id={messageId}`

> 시스템 공지\
`/admin/message/notice`\
`/admin/message/notice?id={messageId}`

> 결제 내역\
`/admin/payment/history`\
`/admin/payment/history?type=contact`

> 구매 문의\
`/admin/payment/pcontact`

> 등록 코드 등록\
`/admin/payment/coupon`

> 자주 묻는 질문\
`/admin/help/faq`\
`/admin/help/faq?id={faqId}`

> 문의 하기\
`/admin/help/contact`

> 계정 설정\
`/admin/setting/account`

> 언어 선택\
`/admin/setting/language`

#### extra

> 서비스 이용약관\
`/policy/terms`

> 개인정보 처리방침\
`/policy/privacy`

> 쿠키 취급 방침\
`/policy/cookie`

> 고객센터\
`/m`

> qr코드 로그인\
`/m/qrcode`

> 오픈소스 라이선스\
`/oss`

> 지원하지 않는 브라우저\
`/support`
