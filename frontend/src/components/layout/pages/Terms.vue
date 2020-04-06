<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>약관 동의</h2>
        <p>VIRNECT 이용약관에 동의하시면 가입이 진행됩니다.</p>

        <div class="terms-body">
          <el-checkbox v-model="allTerms" class="all-terms">전체 약관 동의</el-checkbox>
          <el-checkbox v-model="serviceAgree" class="must-check">[서비스 이용약관 동의 내용]</el-checkbox>
          <div class="terms-contents terms-wrap">
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
          </div>

          <el-checkbox v-model="privacyAgree" class="must-check">[개인정보처리방침 동의 내용]</el-checkbox>
          <div class="policy-contents terms-wrap">
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
          </div>
          <el-checkbox v-model="marketingAgree">[이벤트 마케팅 수신 동의 내용(선택)]</el-checkbox>
        </div>

        <el-button class="next-btn block-btn" type="primary" 
        @click="$router.push({ name: 'register', params: { marketInfoReceive : marketingAgree } })"
        :disabled="!privacyAgree || !serviceAgree "
					>다음</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
export default {
  data() {
    return {
      allTerms: null,
      serviceAgree: false,
      privacyAgree: false,
      marketingAgree: false
    }
  },
  watch: {
    allTerms() {
      if ( this.allTerms ) {
        this.serviceAgree = true
        this.privacyAgree = true
        this.marketingAgree = true
      } else {
        this.serviceAgree = false
        this.privacyAgree = false
        this.marketingAgree = false
      }
    }
  },
  methods: {
    // allTerms() {
    //   console.log(allTerms)
    //   this.serviceAgree = !this.serviceAgree
    //   this.privacyAgree = !this.privacyAgree
    // }
  },
}
</script>

<style lang="scss" scoped>
  p {
    font-family: "NotoSansKR", "Noto Sans";
  }

  .el-button.next-btn {
    margin-top: 60px;
  }


  .all-terms {
    font-size: 16px;
    .el-checkbox__label {
      font-size: 16px;
    }
  }
  .terms-body {
    text-align: left;
    margin-top: 55px;
    .el-checkbox {
      display: block;
      margin-top: 26px;
      margin-bottom: 12px;
    }
  }
  .terms-wrap {
    height: 132px;
    padding: 14px;
    border: 2px solid #ecf0f5;
    overflow-y: scroll;
    font-size: 13px;
    margin-top: 11px;
  }
</style>