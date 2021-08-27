<template>
  <float-button
    class="openroom-float-btn"
    :img="require('assets/image/ic_float_openroom.svg')"
    @onClick="onClick"
  ></float-button>
</template>

<script>
import FloatButton from 'FloatButton'
import roomMixin from 'mixins/room'

export default {
  components: {
    FloatButton,
  },
  mixins: [roomMixin],
  computed: {
    shortName() {
      if (this.account.nickname.length > 10) {
        return this.account.nickname.substr(0, 10)
      } else {
        return this.account.nickname
      }
    },
  },
  methods: {
    onClick() {
      //this.$eventBus.$emit('open:modal:createOpen')
      this.startRemote({
        title: `${this.shortName}'s Room`,
        description: '',
        imageUrl: '',
        imageFile: null,
        open: true,
      })
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';
.openroom-float-btn {
  display: none;
}
@include responsive-mobile {
  .openroom-float-btn {
    position: fixed;
    right: 10px;
    bottom: 88px;
    display: flex;
    background-color: $new_color_bg_button_subprimary;
  }
}
</style>
