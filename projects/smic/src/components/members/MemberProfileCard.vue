<template lang="pug">

  el-card
    .profile-card
      .profile-card--profile.label 프로필
      .profile-card--top
        .profile-card--top-left
          .profile-card--workspace-name {{profileData.name}}
          .profile-card--email {{profileData.email}}
          .profile-card--role.capitalize(v-bind:class="profileData.role | lowserCase")
            span {{profileData.role | upperCases}}
        .profile-card--top-right
          .img-wrapper
            img(:src='profileData.profile' )
      .profile-card--bottom
        .label 공정관리
        .percent-label 참여중인 세부공정 / 할당된 세부공정
        el-progress(:percentage='setWorkPercent(profileData)' :show-text="false")
        .stats.text-right
          span.done 4&nbsp;
          span / 5
        router-link.direct-link(to="/process") 바로가기
        .tools
          .vn-label
            router-link(:to="`/process?search=${profileData.name}`") 
              img(src="~@/assets/image/ic-process-dark.svg")
              span 공정
          .vn-label
            router-link(:to="`/issue?search=${profileData.name}`") 
              img(src="~@/assets/image/ic-issue-dark.svg")
              span 이슈
          .vn-label
            router-link(:to="`/contents?search=${profileData.name}`") 
              img(src="~@/assets/image/ic-content-dark.svg")
              span 콘텐츠

</template>
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
