<template>
  <li class="sharing-image">
    <button class="sharing-image__item">
      <img :src="imageData" />
    </button>
    <p class="sharing-image__name">{{ fileData.name }}</p>
    <button class="sharing-image__remove">이미지 삭제</button>
  </li>
</template>

<script>
export default {
  name: 'SharingImage',
  components: {},
  data() {
    return {
      imageData: '',
    }
  },
  props: {
    isPdf: {
      type: Boolean,
      default: false,
    },
    fileInfo: {
      type: Object,
    },
  },
  computed: {
    fileData() {
      if (this.fileInfo && this.fileInfo.filedata) {
        return this.fileInfo.filedata
      } else {
        return {}
      }
    },
  },
  methods: {
    init() {
      const fileReader = new FileReader()
      fileReader.onload = e => {
        this.imageData = e.target.result
      }
      fileReader.readAsDataURL(this.fileData)
    },
  },

  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>
