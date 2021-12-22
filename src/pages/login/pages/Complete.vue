<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('complete.title') }}</h2>
        <p class="disc">{{ $t('complete.welcome') }}</p>

        <el-button class="next-btn block-btn" @click="payment" type="info">{{
          $t('complete.payment')
        }}</el-button>

        <p class="divide" v-html="$t('complete.info')"></p>
        <el-button class="next-btn block-btn" type="info" @click="goWorkstation"
          >VIRNECT Workstation
        </el-button>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  setup(props, { root }) {
    const goWorkstation = () => {
      location.replace(root.$urls['workstation'])
    }
    const payment = () => {
      location.replace(root.$urls['pay'])
    }
    return {
      goWorkstation,
      payment,
    }
  },
}
</script>

<style lang="scss" scoped>
.el-button.block-btn {
  font-size: 15px;
  font-family: $noto;
  & + .block-btn {
    margin-top: 12px;
  }
}
.divide {
  margin-top: 40px;
  padding-top: 40px;
  color: #0d2a58;
  font-weight: 500;
  font-size: 14px;
  border-top: 1px solid #e6e9ee;
}
</style>
