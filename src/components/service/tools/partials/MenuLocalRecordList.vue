<template>
  <div>
    <menu-button
      text="로컬 녹화 목록"
      :active="action === 'localRecordList'"
      :src="require('assets/image/ic_local_record_list.svg')"
      @click="list"
    ></menu-button>
    <service-local-record-list
      :visible.sync="showList"
    ></service-local-record-list>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import ServiceLocalRecordList from 'components/workspace/modal/ServiceLocalRecordList'
export default {
  name: 'LocalRecordListMenu',
  mixins: [toolMixin],
  data() {
    return { showList: false }
  },
  components: {
    ServiceLocalRecordList,
  },
  computed: {},
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
