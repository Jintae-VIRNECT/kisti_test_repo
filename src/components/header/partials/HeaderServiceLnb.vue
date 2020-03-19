<template>
  <nav class="header-lnb">
    <ul>
      <lnb-button
        text="실시간 공유"
        keyvalue="stream"
        :image="require('assets/image/call/gnb_ic_shareframe.svg')"
      ></lnb-button>
      <lnb-button
        text="협업 보드"
        keyvalue="drawing"
        :image="require('assets/image/call/gnb_ic_creat_basic.svg')"
      ></lnb-button>
      <lnb-button
        text="AR 기능"
        keyvalue="ar"
        :image="require('assets/image/call/gnb_ic_creat_ar.svg')"
      ></lnb-button>
    </ul>
  </nav>
</template>

<script>
import LnbButton from '../tools/LnbButton'
export default {
  name: 'HeaderServiceLnb',
  components: {
    LnbButton,
  },
  data() {
    return {}
  },

  /* Lifecycles */
  mounted() {},
}
</script>
