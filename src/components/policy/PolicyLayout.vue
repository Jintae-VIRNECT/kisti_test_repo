<template>
  <div class="policy-wrapper" id="virnectWrap">
    <!-- <policy-header></policy-header> -->

    <div id="virnectContent">
      <div id="mArticle">
        <div class="head_virnectcorp">
          <h2 id="virnectBody" class="screen_out">버넥트 서비스 약관</h2>
          <ul class="list_opt">
            <li
              v-for="(value, index) in routeMap"
              :key="index"
              :class="{ on: value[0] === tab }"
            >
              <a
                href="#"
                class="link_option"
                data-wml-style=".overlay"
                :data-wml-speech-command="value[1]"
                @click="route(`/policy/${value[0]}`)"
              >
                <span class="txt_line">{{ value[1] }}</span>
              </a>
            </li>
          </ul>
        </div>

        <router-view></router-view>
      </div>
    </div>

    <footer id="kakaoFoot" class="d_foot" aria-hidden="false">
      <div class="wrap_copyright">
        <div class="info_copyright">
          <div class="info_policy">
            <div
              v-for="(value, index) in routeMap"
              :key="index"
              class="info_svc"
            >
              <anchor :link="{ name: value[0] }" class="dlnk_info">{{
                value[1]
              }}</anchor>
            </div>
          </div>
          <small class="txt_copyright"
            >Copyright ©
            <a href="https://virnect.com/" class="link_virnect"
              >VIRNECT CO., LTD.</a
            >
            All rights reserved.</small
          >
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
// import PolicyHeader from './header/PolicyHeader'
import WearML from 'plugins/wearML/index'
import Anchor from 'UIAnchor'

const RouteMap = new Map([
  ['terms', '이용약관'],
  ['privacy', '개인정보 처리방침'],
  // ['cookie', '쿠키 취급 방침'],
])
export default {
  name: 'PolicyLayout',
  components: {
    Anchor,
    // PolicyHeader,
  },
  data() {
    return {
      tab: '',
      routeMap: RouteMap,
    }
  },
  methods: {
    route(url) {
      location.href = url
    },
  },

  /* Lifecycles */
  mounted() {
    if ('name' in this.$route) {
      this.tab = this.$route.name
    }
    WearML.getCommands()
  },
}
</script>
<style lang="scss" src="assets/style/policy.scss"></style>
