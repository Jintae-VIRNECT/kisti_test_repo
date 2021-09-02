<template>
  <section class="remote-layout">
    <header-section></header-section>
    <!-- <vue2-scrollbar classes="remote-wrapper" ref="wrapperScroller"> -->
    <div class="qr-wrapper">
      <div class="qr">
        <article class="qr__main">
          <p class="qr__main--title">QR 로그인</p>
          <p class="qr__main--sub-title">
            스마트 글라스에서 QR코드로 간편하게 로그인 하세요.
          </p>
          <canvas class="qr__main--code" ref="qr-code"></canvas>
        </article>
        <figcaption class="qr__desc">
          <p class="qr__desc--title">QR 로그인 방법</p>
          <div class="qr__explain">
            <ol class="qr__explain--list">
              <li>
                1. 사용하실 스마트글라스 또는 스마트폰에서 VIRNECT Remote를
                실행하세요.
              </li>
              <li>2. 로그인 화면 하단의 [QR 스캐너]를 선택하세요.</li>
              <li>
                3. 현재 화면 상의 QR코드를 비춰주세요. QR코드 인식 후 기기가
                로그인 됩니다.
              </li>
            </ol>
            <p class="qr__explain--caution">*QR코드 지원 기기만 가능</p>
          </div>
        </figcaption>
      </div>
      <footer class="qr-footer">
        ©VIRNECT CO., LTD. All rights reserved.
      </footer>
    </div>
    <!-- </vue2-scrollbar> -->
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'

import confirmMixin from 'mixins/confirm'
import langMixin from 'mixins/language'
import toastMixin from 'mixins/toast'

import errorMsgMixin from 'mixins/errorMsg'

import QRCode from 'qrcode'

export default {
  name: 'QrLayout',
  // async beforeRouteEnter(to, from, next) {
  //   console.log(to)
  //   console.log(from)
  //   console.log(next)
  //   next()
  // },
  mixins: [confirmMixin, langMixin, toastMixin, errorMsgMixin],
  components: {
    HeaderSection,
  },
  data() {
    return {
      url: '',
      expireTime: 30000,
    }
  },

  async mounted() {
    const workspaceId = this.$route.query.workspaceId
    const sessionId = this.$route.query.sessionId

    const qr_canvas = this.$refs['qr-code']

    const generateQR = async url => {
      try {
        QRCode.toCanvas(
          qr_canvas,
          JSON.stringify({ workspaceId: workspaceId, sessionId: sessionId }),

          function(error) {
            if (error) console.error(error)
          },
        )
      } catch (err) {
        console.error(err)
      }
    }
    await generateQR()

    this.$nextTick(() => {
      qr_canvas.style.height = null
      qr_canvas.style.width = null
    })
  },
  beforeDestroy() {},
}
</script>

<style lang="scss" src="assets/style/qr.scss"></style>
