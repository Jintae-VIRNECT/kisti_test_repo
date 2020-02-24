<template lang="pug">
  el-dropdown(trigger="click")
    span.el-dropdown-link
      i.el-icon-more.el-icon--right
    el-dropdown-menu(slot='dropdown')
      el-dropdown-item 
        div(@click="editProcess") 세부공정 편집
    sub-process-control-dropdown-modal(
      :target="target"
      :modalType="modalType"
      :processId="processId"
      @onToggleProcessModal="onToggleProcessModal"   
      :toggleProcessModal="toggleProcessModal" 
    )
</template>
<script>
import SubProcessControlDropdownModal from '@/components/process/SubProcessControlDropdownModal'

export default {
  components: {
    SubProcessControlDropdownModal,
  },
  props: {
    target: Object,
    targetId: String,
    processId: String,
  },
  data() {
    return {
      modalType: null,
      toggleProcessModal: false,
    }
  },
  methods: {
    onToggleProcessModal(boolean, type) {
      this.toggleProcessModal = boolean
      this.modalType = type
    },
    editProcess() {
      this.onToggleProcessModal(true, 'edit')
    },
  },
}
</script>
