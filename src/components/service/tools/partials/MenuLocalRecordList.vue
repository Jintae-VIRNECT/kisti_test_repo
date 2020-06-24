<template>
  <div>
    <menu-button
      text="로컬 녹화 목록"
      :active="showList"
      :src="require('assets/image/ic_local_record_list.svg')"
      @click="list"
    ></menu-button>
    <record-list :visible.sync="showList"></record-list>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import RecordList from 'LocalRecordList'
export default {
  name: 'LocalRecordListMenu',
  mixins: [toolMixin],
  data() {
    return {
      showList: false,
    }
  },
  components: {
    RecordList,
  },
  watch: {},
  methods: {
    list() {
      this.showList = !this.showList
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
