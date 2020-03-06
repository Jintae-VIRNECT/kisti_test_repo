<template lang="pug">
  el-dropdown(trigger="click")
    span.el-dropdown-link
      i.el-icon-more.el-icon--right
    el-dropdown-menu(slot='dropdown')
      el-dropdown-item 
        div(@click="endProcess") 공정 종료
      el-dropdown-item 
        div(@click="addProcess") 공정 추가 생성
      el-dropdown-item 
        div(@click="editProcess") 공정 편집
      el-dropdown-item
        .color-red(@click.prevent="deleteProcess") 삭제
    process-control-dropdown-modal(
      :target="modalTarget"
      :modalType="modalType"
      @onToggleProcessModal="onToggleProcessModal"   
      :toggleProcessModal="toggleProcessModal" 
      @onCreateData="onCreateData"
      @onChangeData="onChangeData")
</template>
<script>
import ProcessControlDropdownModal from '@/components/process/ProcessControlDropdownModal'

export default {
  components: {
    ProcessControlDropdownModal,
  },
  props: {
    target: Object,
  },
  data() {
    return {
      modalTarget: this.target,
      modalType: null,
      toggleProcessModal: false,
    }
  },
  methods: {
    async endProcess() {
      try {
        await this.$confirm(
          '선택한 공정을 종료 하시겠습니까? 공정 종료 시 관련 데이터를 더이상 수집하지 않습니다.',
          '공정 종료',
          {
            confirmButtonText: '확인',
            cancelButtonText: '취소',
          },
        )
      } catch (e) {
        return false
      }
      this.$store.dispatch('closeProcess', this.target.id)
    },
    async addProcess() {
      try {
        await this.$confirm(
          `선택한 공정을 추가 생성 하시겠습니까?<br>
        추가 생성 시 기존 공정 항목에 데이터를 더이상 수집하지 않고 새로운 공정을 추가하여 새로운 데이터를 수집합니다.`,
          '공정 추가 생성',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '확인',
            cancelButtonText: '취소',
          },
        )
      } catch (e) {
        return false
      }
      const { state } = await this.$store.dispatch(
        'closeProcess',
        this.target.id,
      )
      if (state !== 'CLOSED') return false
      await this.$store.dispatch('getContentsDetail', this.target.contentUUID)
      this.modalTarget = this.$store.getters.contentDetail
      this.onToggleProcessModal(true, 'replace')
    },
    onCreateData(data) {
      this.$emit('onCreateData', data)
    },
    onChangeData(data) {
      this.$emit('onChangeData', data)
    },
    onToggleProcessModal(boolean, type) {
      this.toggleProcessModal = boolean
      this.modalType = type
    },
    editProcess() {
      this.$confirm(
        '공정 정보를 편집하시겠습니까? 변경된 정보로 공정 보고를 받습니다.',
        '공정 편집',
        {
          confirmButtonText: '확인',
          cancelButtonText: '취소',
        },
      )
        .then(() => {
          this.onToggleProcessModal(true, 'edit')
        })
        .catch(() => {})
    },
    deleteProcess() {
      this.$confirm(
        `선택한 공정을 삭제 하시겠습니까?<br>
        삭제된 공정 관련 데이터를 더이상 수집하지 않습니다.<br>
        공정 삭제 시 관련 공정 데이터 기록은 모두 사라지며 복구되지 않습니다. `,
        '공정 삭제',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: 'OK',
          cancelButtonText: '취소',
        },
      )
        .then(() => {
          this.$emit('onDeleteData', this.$props.target)
        })
        .catch(() => {})
    },
  },
}
</script>
