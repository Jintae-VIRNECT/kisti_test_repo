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
      <div class="sharing-image__item-active"><p>공유중</p></div>
    </button>
    <button class="sharing-image__remove" @click="deleteImage">
      이미지 삭제
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
        '선택한 저작된 이미지를 삭제하시겠습니까?​',
        {
          text: '확인',
          action: this.remove,
        },
        {
          text: '취소',
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
