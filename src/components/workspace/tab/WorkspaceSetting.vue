<template>
  <tab-view title="사용자 환경설정">
    <scrollbar>
      <div style="height: 1000px;border: solid 1px #d8d8d8;"></div>
    </scrollbar>
  </tab-view>
</template>

<script>
import TabView from '../partials/workspaceTabView'
import Scrollbar from 'Scroller'
export default {
  name: 'WorkspaceSetting',
  components: { TabView, Scrollbar },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
