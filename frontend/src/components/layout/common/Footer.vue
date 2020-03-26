<template>
  <el-row id="footer">
    <el-col>
    <h2>footer</h2>
    <div class="privacy-wrap">
      <router-link to="/find">이용약관</router-link>
      <router-link to="/find">개인정보처리방침</router-link>
      <router-link to="/register">도움말</router-link>
    </div>
    <p>Copyright© <strong>VIRNECT INC.</strong> All Rights Reserved.</p>
    </el-col>
  </el-row>
</template>

<script>
export default {
  
}
</script>

<style lang="scss" scoped>
  #footer {
    position: fixed;
    bottom: 40px;
    left: 0;
    width: 100%;
    text-align: center;
    color: #7d828b;
    font-size: 13px;
    h2 {
      display: none;
    }
    a {
      position: relative;
      color: #81858c;
      font-weight: normal;
      padding: 0 11px 0 7px;
      &:after {
        position: absolute;
        right: 0;
        top: 50%;
        margin-top: -5px;
        height: 12px;
        width: 1px;
        background: #ccc;
        content: "";
      }
      &:last-child::after {
        display: none;
      }
    }
    p {
      margin-top: 10px;
      opacity: 0.5;
      font-family: "Roboto";
    }
    strong {
      font-weight: bold;
    }
  }
</style>