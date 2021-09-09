<template>
  <section class="remote-layout">
    <header-section></header-section>
    <!-- <vue2-scrollbar classes="remote-wrapper" ref="wrapperScroller"> -->
    <div class="qr-wrapper">
      <div class="qr">
        <article class="qr__main">
          <p class="qr__main--title">{{ $t('qr.qr_login') }}</p>
          <p class="qr__main--sub-title">
            {{ $t('qr.qr_login_description') }}
          </p>
          <canvas class="qr__main--code" ref="qr-code"></canvas>
        </article>
        <figcaption class="qr__desc">
          <p class="qr__desc--title">{{ $t('qr.qr_login_how_to') }}</p>
          <div class="qr__explain">
            <ol class="qr__explain--list">
              <li>
                {{ $t('qr.qr_login_how_to_description_1') }}
              </li>
              <li>{{ $t('qr.qr_login_how_to_description_2') }}</li>
              <li>
                {{ $t('qr.qr_login_how_to_description_3') }}
              </li>
            </ol>
            <p class="qr__explain--caution">
              {{ $t('qr.qr_login_how_to_description_4') }}
            </p>
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
