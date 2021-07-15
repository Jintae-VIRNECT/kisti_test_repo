<template>
  <el-card
    v-if="type === 'A'"
    class="banner banner--A"
    @click.native="moveTo"
    :style="{ 'background-image': `url(${bg})` }"
  >
    <span>{{ subTxt }}</span>
    <h4>{{ headerTxt }}</h4>
    <a>
      <span>{{ linkTxt }}</span>
      <img class="icon" src="~assets/images/icon/ic-arrow-forward.svg" />
    </a>
  </el-card>
  <!-- 다른 디자인 -->
  <el-card
    v-else-if="type === 'B'"
    class="banner banner--B"
    @click.native="moveTo"
    :style="{ 'background-image': `url(${bg})` }"
  >
    <h4>{{ headerTxt }}</h4>
    <span>{{ subTxt }}</span>
    <a>
      <span>{{ linkTxt }}</span>
      <img class="icon" src="~assets/images/icon/ic-arrow-forward.svg" />
    </a>
  </el-card>
</template>

<script>
export default {
  props: {
    type: {
      type: String,
      default: () => 'A',
    },
    headerTxt: String,
    subTxt: String,
    linkTxt: String,
    to: String,
    bg: String,
  },
  methods: {
    moveTo() {
      window.open(this.to)
    },
  },
}
</script>

<style lang="scss">
.banner .el-card__body {
  position: relative;
  color: #fff;
  background-size: cover;
  cursor: pointer;

  & > span {
    font-weight: normal;
    font-size: 14px;
  }
  & > h4 {
    font-weight: bold;
    font-size: 20px;
  }
  & > a {
    display: inline-block;
    margin-top: 12px;
    color: #fff;
    font-weight: normal;
    font-size: 12px;
    & > * {
      display: inline-block;
      vertical-align: middle;
    }
  }

  // effect
  &:after {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: linear-gradient(140deg, #fff, transparent);
    opacity: 0;
    content: '';
  }
  &:after,
  img {
    transition: 0.3s;
  }
  &:hover {
    &:after {
      opacity: 0.3;
    }
    img {
      margin-left: 4px;
    }
  }
}

.banner--A .el-card__body {
  background-image: url('~assets/images/bg_banner.jpg');
}
</style>
