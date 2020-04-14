<template>
  <tab-view
    title="협업 가능 멤버"
    description="협업 가능한 회원을 선택하고 메세지를 보내보세요."
    placeholder="멤버 검색"
    ><scroller> <workspace-user-list></workspace-user-list></scroller>
    ></tab-view
  >
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceUserList from '../section/WorkspaceUserList'
import Scroller from 'Scroller'
export default {
  name: 'WorkspaceUser',
  components: { TabView, WorkspaceUserList, Scroller },
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
