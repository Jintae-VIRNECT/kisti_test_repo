<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>약관 동의</h2>
        <p>VIRNECT 이용약관에 동의하시면 가입이 진행됩니다.</p>

        <div class="terms-body">
          <el-checkbox @click="allTerms()">전체 약관 동의</el-checkbox>
          <el-checkbox v-model="serviceAgree">[서비스 이용약관 동의 내용]</el-checkbox>
          <div class="terms-contents terms-wrap">
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
            <p>서비스 이용약관</p>
          </div>

          <el-checkbox v-model="privacyAgree">[개인정보처리방침 동의 내용]</el-checkbox>
          {{privacyAgree}}
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

          <el-checkbox v-model="privacyAgree">[이벤트 마케팅 수신 동의 내용(선택)]</el-checkbox>
          
        </div>


        <el-button type="primary" @click="$router.push({ name: 'register' })"
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
      serviceAgree: false,
      privacyAgree: false,
    }
  },
  methods: {
    allTerms() {
      console.log(allTerms)
      this.serviceAgree = !this.serviceAgree
      this.privacyAgree = !this.privacyAgree
    }
  },
}
</script>

<style lang="scss" scoped>
  .terms-body {
    text-align: left;
    margin-top: 55px;
    .el-checkbox {
      display: block;
    }
  }
  .terms-wrap {
    height: 132px;
    padding: 12px;
    border: 1px solid #ecf0f5;
    overflow-y: scroll;
  }
</style>