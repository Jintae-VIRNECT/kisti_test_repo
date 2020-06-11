<template>
  <li class="sharing-image">
    <button
      class="sharing-image__item"
      :class="{ active: shareFile.id === imgInfo.id }"
      @dblclick="show"
    >
      <img :src="imgInfo.img" />
      <div class="sharing-image__item-active">공유중</div>
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
    },
    deleteImage() {
      if (this.shareFile.id === this.imgInfo.id) return
      this.confirmCancel(
        '정말로 삭제하시겠습니까?',
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
  },

  /* Lifecycles */
  mounted() {},
}
</script>
