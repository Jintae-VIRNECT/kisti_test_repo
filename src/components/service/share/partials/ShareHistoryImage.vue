<template>
  <li class="sharing-image">
    <button
      class="sharing-image__item"
      :class="{
        active: shareFile.id === imgInfo.id,
        selected: selected,
      }"
      @dblclick="show"
      @click="select"
    >
      <img :src="imgInfo.img" />
      <div class="sharing-image__item-active">
        <p>{{ $t('service.share_current') }}</p>
      </div>
    </button>
    <button class="sharing-image__remove" @click="deleteImage">
      {{ $t('service.share_delete') }}
    </button>
    <p class="sharing-image__name">{{ imgInfo.fileName }}</p>
  </li>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'ShareHistoryImage',
  mixins: [confirmMixin],
  components: {},
  data() {
    return {
      imageData: '',
      clicking: false,
    }
  },
  props: {
    selected: {
      type: Boolean,
      default: false,
    },
    imgInfo: {
      type: Object,
    },
  },
  computed: {
    ...mapGetters(['shareFile']),
  },
  methods: {
    ...mapActions(['showImage', 'removeHistory']),
    show() {
      this.clicking = false
      if (this.shareFile.id === this.imgInfo.id) return
      this.showImage(this.imgInfo)
      // this.$call.shareImage(this.imgInfo)
    },
    deleteImage() {
      if (this.shareFile.id === this.imgInfo.id) return
      this.confirmCancel(
        this.$t('service.share_delete_confirm'),
        {
          text: this.$t('button.confirm'),
          action: this.remove,
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
    remove() {
      this.removeHistory(this.imgInfo.id)
    },
    select() {
      if (!this.clicking) {
        this.clicking = true
        setTimeout(() => {
          if (!this.clicking) return
          this.clicking = false
          if (!this.selected) {
            this.$emit('selected', this.imgInfo.id)
          } else {
            this.$emit('unSelected', this.imgInfo.id)
          }
        }, 300)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
