<template>
  <div class="mobile-header-service">
    <div class="mobile-header-info">
      <h1>{{ roomInfo.title }}</h1>
      <h1>{{ callTime | timeFilter }}</h1>
    </div>

    <div class="mobile-header-tools">
      <chat :tooltipActive="false"></chat>
      <button class="mobile-header-tools__leave" @click="leave"></button>
      <notice v-show="false"></notice>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Chat from './tools/Chat'
import Notice from './tools/Notice'
import { ROLE } from 'configs/remote.config'

export default {
  components: {
    Chat,
    Notice,
  },
  props: {
    callTime: {
      type: Number,
    },
  },
  computed: {
    ...mapGetters(['roomInfo']),
  },
  methods: {
    //pc인 경우 HeaderServiceTools에서 처리된다(동일함수)
    // this.$call.leave() 는 ServiceLayout.vue의 beforeRouteLeave에서 처리된다
    leave() {
      try {
        // this.$call.leave()

        if (this.account.roleType === ROLE.GUEST) {
          window.history.back()
        } else {
          this.$router.push({ name: 'workspace', params: 'leave' })
        }
      } catch (err) {
        if (this.account.roleType === ROLE.GUEST) {
          window.history.back()
        } else {
          this.$router.push({ name: 'workspace' })
        }
      }
    },
  },
  mounted() {
    this.$eventBus.$on('call:logout', this.leave)
  },
  beforeDestroy() {
    this.$eventBus.$off('call:logout', this.leave)
  },
}
</script>

<style></style>
