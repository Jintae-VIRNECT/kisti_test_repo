<template>
  <div class="chat-down">
    <div class="chat-down__bar">
      <button class="chat-down__bar--cancel" @click="release"></button>
      <label v-if="selectedList.length > 0" class="chat-down__bar--count">{{
        selectedList.length + ' 개 선택'
      }}</label>
      <button class="chat-down__bar--button" @click="download">
        다운로드
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatFileDownload',
  components: {},
  data() {
    return {
      selectedList: [],
    }
  },
  props: {},
  computed: {},
  watch: {},
  methods: {
    download() {
      console.log('download :: selectedList', this.selectedList)
    },
    release() {
      console.log('셀렉트 해! 제!')
      this.selectedList = []
      this.$eventBus.$emit('chatfile::release')
    },
    add(id) {
      this.selectedList.push(id)
    },
    del(id) {
      const idx = this.selectedList.findIndex(fileId => {
        return id === fileId
      })
      if (idx >= 0) {
        this.selectedList.splice(idx, 1)
      }
    },
  },
  mounted() {
    this.$eventBus.$on('chatfile::selected', this.add)
    this.$eventBus.$on('chatfile::unselected', this.del)
  },
  beforeDestroy() {
    this.$eventBus.$off('chatfile::selected')
    this.$eventBus.$off('chatfile::unselected')
  },
}
</script>
