<template>
  <div>
    <div class="container">
      <el-row type="flex" justify="center" align="middle" class="row-bg">
        <el-col>
          <h2>로그인</h2>
          <p class="input-title">이메일</p>
          <el-input
            placeholder="이메일을 입력해 주세요"
            v-model="login.email"
            name="email"
            :class="{ 'input-danger': message }"
            clearable
            v-validate="'required'"
          >
          </el-input>

          <p class="input-title">비밀번호</p>
          <el-input
            placeholder="비밀번호를 입력해 주세요"
            v-model="login.password"
            show-password
            name="password"
            :class="{ 'input-danger': message }"
            v-validate="'required'"
          ></el-input>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      login: {
        email: 'test@test.com',
      },
      message: '테스트 메세지',
    }
  },
}
</script>
