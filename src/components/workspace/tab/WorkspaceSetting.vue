<template>
  <tab-view title="사용자 환경설정">
    <scrollbar>
      환경설정
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
