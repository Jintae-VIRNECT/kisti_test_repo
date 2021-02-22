<template>
  <button @click="stopScreenSharing" class="screen-tools tools stop-button">
    화면 공유 종료
  </button>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { VIEW } from 'configs/view.config'
export default {
  name: 'ScreenStopButton',
  computed: {
    ...mapGetters(['myInfo']),
  },
  methods: {
    ...mapActions(['setView']),
    stopScreenSharing() {
      this.$call.sendScreenSharing(false)

      this.$call.restoreMyStream(this.myInfo.video)
      this.setView(VIEW.STREAM)
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
.stop-button {
  display: block;
  width: 8.8571rem;
  height: 2.4286rem;
  margin: 1.5714rem auto;
  color: #fff;
  font-weight: 400;
  font-size: 1rem;
  background: #3c3c3f;
  border: 1px solid rgba(#4c5259, 0.42);
  border-radius: 1.2143rem;
  box-shadow: 0px 6px 8px 0px rgba(#000, 0.24);
  transition: all 0.3s;
  &:hover {
    background-color: #38383a;
  }
  &:active {
    background-color: #242427;
  }
}
</style>
