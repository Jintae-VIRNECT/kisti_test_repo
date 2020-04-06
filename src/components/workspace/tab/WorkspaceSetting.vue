<template>
  <tab-view title="사용자 환경설정">
    <scrollbar>
      <div>비디오 및 오디오 설정</div>
      <workspace-set-audio></workspace-set-audio>
      <workspace-set-video></workspace-set-video>
      <workspace-set-language></workspace-set-language>
      <workspace-set-notification></workspace-set-notification>
    </scrollbar>
  </tab-view>
</template>

<script>
import TabView from '../partials/workspaceTabView'
import Scrollbar from 'Scroller'
import WorkspaceSetAudio from '../section/WorkspaceSetAudio'
import WorkspaceSetVideo from '../section/WorkspaceSetVideo'
import WorkspaceSetLanguage from '../section/WorkspaceSetLanguage'
import WorkspaceSetNotification from '../section/WorkspaceSetNotification'
export default {
  name: 'WorkspaceSetting',
  components: {
    TabView,
    Scrollbar,
    WorkspaceSetAudio,
    WorkspaceSetVideo,
    WorkspaceSetLanguage,
    WorkspaceSetNotification,
  },
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
