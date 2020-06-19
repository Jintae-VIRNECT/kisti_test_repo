<template>
  <div>
    <menu-button
      text="로컬 녹화 목록"
      :active="showList"
      :src="require('assets/image/ic_local_record_list.svg')"
      @click="list"
    ></menu-button>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'LocalRecordListMenu',
  mixins: [toolMixin],
  data() {
    return {
      showList: false,
    }
  },
  computed: {},
  watch: {},
  methods: {
    list() {
      this.showList = !this.showList
      this.$eventBus.$emit('lcRecList:show')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
