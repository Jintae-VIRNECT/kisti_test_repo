<template lang="pug">
  el-dropdown(trigger="click")
    span.el-dropdown-link
      i.el-icon-more.el-icon--right
    el-dropdown-menu(slot='dropdown')
      el-dropdown-item 
        div(@click="endProcess") 공정 마감
      el-dropdown-item 
        div(@click="addProcess") 공정 추가 생성
      el-dropdown-item 
        div(@click="editProcess") 공정 편집
      el-dropdown-item
        .color-red(@click.prevent="deleteProcess") 삭제
    process-control-dropdown-modal(
      :target="target"
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
  props: ['data', 'targetId'],
  data() {
    return {
      modalType: null,
      toggleProcessModal: false,
      target: this.$props.data,
    }
  },
  methods: {
    endProcess() {
      this.$confirm(
        '선택한 공정을 마감 하시겠습니까? 공정 마감 시 관련 데이터를 더이상 수집하지 않습니다.',
        '공정 마감',
        {
          confirmButtonText: '확인',
          cancelButtonText: '취소',
        },
      )
        .then(() => {
          this.emitEndProcessData()
        })
        .catch(() => {})
    },
    // emitEndProcessData() {
    //   const updatedTableData = this.$props.data.map(row => {
    //     if (row.id === this.$props.targetId) {
    //       row.issue = false
    //     }
    //     return row
    //   })
    //   this.$emit('onChangeData', updatedTableData)
    // },
    emitEndProcessData() {
      this.target.issue = false
      this.$emit('onChangeData', this.target)
    },
    addProcess() {
      this.$confirm(
        `선택한 공정을 추가 생성 하시겠습니까? \n
        추가 생성 시 기존 공정 항목에 데이터를 더이상 수집하지 않고 새로운 공정을 추가하여 새로운 데이터를 수집합니다.`,
        '공정 추가 생성',
        {
          confirmButtonText: '확인',
          cancelButtonText: '취소',
        },
      )
        .then(() => {
          this.onToggleProcessModal(true, 'create')
        })
        .catch(() => {})
    },
    onCreateData(data) {
      this.$emit('onCreateData', data)
    },
    // onChangeData(data) {
    //   const updatedTableData = this.$props.data.map(row => {
    //     if (row.id === data.id) {
    //       row = data
    //     }
    //     return row
    //   })
    //   this.$emit('onChangeData', updatedTableData)
    // },
    onChangeData(data) {
      this.$emit('onChangeData', data)
    },
    onToggleProcessModal(boolean, type) {
      this.toggleProcessModal = boolean
      this.modalType = type
    },
    editProcess() {
      this.$confirm(
        '등록 지연으로 입력하신 공정 일정과 등록될 공정 일정이 달라집니다. 설정된 일정을 확인해 주세요.',
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
        `선택한 공정을 삭제 하시겠습니까? \n
        삭제된 공정 관련 데이터를 더이상 수집하지 않습니다. \n
        공정 삭제 시 관련 공정 데이터 기록은 모두 사라지며 복구되지 않습니다. `,
        '공정 삭제',
        {
          confirmButtonText: 'OK',
          cancelButtonText: '취소',
        },
      )
        .then(() => {
          // const updatedTableData = this.$props.data.filter(
          //   row => row.id !== this.$props.targetId,
          // )
          // this.$emit('onChangeData', updatedTableData)
          this.$emit('onDeleteData', this.target)
        })
        .catch(() => {})
    },
  },
}
</script>
