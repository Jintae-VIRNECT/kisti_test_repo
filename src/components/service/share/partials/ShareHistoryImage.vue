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
      selected: false,
    }
  },
  props: {
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
      this.selected = !this.selected
      if (this.selected) {
        this.$eventBus.$emit('drawingImg::selected', this.imgInfo.id)
      } else {
        this.$eventBus.$emit('drawingImg::unSelected', this.imgInfo.id)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
