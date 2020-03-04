<template>
  <el-card>
    <div class="profile-card">
      <div class="profile-card--profile label">프로필</div>
      <div class="profile-card--top">
        <div class="profile-card--top-left">
          <div class="profile-card--workspace-name">{{ profileData.name }}</div>
          <div class="profile-card--email">{{ profileData.email }}</div>
          <div
            class="profile-card--role capitalize"
            v-bind:class="profileData.role | lowserCase"
          >
            <span>{{ profileData.role | upperCases }}</span>
          </div>
        </div>
        <div class="profile-card--top-right">
          <div class="img-wrapper"><img :src="profileData.profile" /></div>
        </div>
      </div>
      <div class="profile-card--bottom">
        <div class="label">공정관리</div>
        <div class="percent-label">진행 중인 업무 / 할당된 업무</div>
        <el-progress
          :percentage="setWorkPercent(profileData)"
          :show-text="false"
        ></el-progress>
        <div class="stats text-right">
          <span class="done">4&nbsp;</span><span>/ 5</span>
        </div>
        <router-link class="direct-link" to="/process">바로가기</router-link>
        <div class="tools">
          <div class="vn-label">
            <router-link to="/process">
              <img src="~@/assets/image/ic-process-dark.svg" /><span
                >공정</span
              ></router-link
            >
          </div>
          <div class="vn-label">
            <router-link to="/issue">
              <img src="~@/assets/image/ic-issue-dark.svg" /><span
                >이슈</span
              ></router-link
            >
          </div>
          <div class="vn-label">
            <router-link to="/contents">
              <img src="~@/assets/image/ic-content-dark.svg" /><span
                >콘텐츠</span
              ></router-link
            >
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
export default {
  props: ['profileData'],
  methods: {
    getImgUrl(src) {
      var images = require.context('@/', false, /\.png$/)
      return images(src + '.png')
    },
    setWorkPercent() {
      // return (done / all) * 100
      return 80
    },
  },
  filters: {
    upperCases(str) {
      return str.toUpperCase()
    },
    lowserCase(str) {
      return str.toLowerCase()
    },
  },
}
</script>

<style lang="scss">
.profile-card {
  padding: 22px 24px;

  .profile-card--top {
    margin-bottom: 8px;
    padding-bottom: 32px;
    border-bottom: 1px solid #e6e9ee;
    &-right {
      float: right;
    }
    &-left {
      display: inline-block;
      max-width: 60%;
      vertical-align: middle;
    }
  }
  &--profile {
    margin-bottom: 10px;
  }
  &--workspace-name {
    margin-bottom: 2px;
    color: #0d2a58;
    font-weight: 500;
    font-size: 18px;
    line-height: 1.33;
  }
  &--email {
    margin-bottom: 12px;
    color: #6d798b;
    font-weight: 500;
    font-size: 13px;
    line-height: 1.54;
  }
  &--role {
    display: inline-block;
    padding: 2px 8px;
    color: #186ae2;
    font-weight: 500;
    font-size: 11px;
    line-height: 1.54;
    border: solid 1px #186ae2;
    border-radius: 11px;

    &.master {
      color: #4f42c0;
      border-color: #4f42c0;
    }
  }
  .img-wrapper {
    float: right;
    width: 80px;
    height: 80px;
    border-radius: 30%;
    img {
      width: 100%;
      padding: 5px;
    }
  }
  .stats {
    margin-top: 4px;
    margin-bottom: 12px;
  }
  .direct-link {
    margin-bottom: 10px;
    color: #6d798b;
    font-weight: 500;
    font-size: 12px;
  }
  .label {
    color: #6d798b;
    font-size: 12px;
  }
  .percent-label {
    margin-bottom: 8px;
    color: #0d2a58;
    font-size: 13px;
    line-height: 1.54;
  }
  .profile-card--bottom {
    > .label {
      margin-bottom: 10px;
    }
    .done {
      color: #186ae2;
    }
    &-tool {
      a {
        margin-right: 10px;
        padding: 2px 8px 2px 6px;
        background-color: #f2f5f9;
        border-radius: 4px;
      }
      img,
      span {
        vertical-align: middle;
      }
      span {
        color: #0d2a58;
        font-size: 13px;
      }
      img {
        width: 24px;
        height: 24px;
        margin-right: 4px;
      }
    }
  }
}
</style>
