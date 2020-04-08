<template>
  <modal
    title="원격 협업 생성하기"
    :width="1098"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
  >
    <div class="createroom">
      <section class="createroom-info">
        <div>프로필</div>
        <div>협업 이름</div>
        <div>협업 설명</div>
        <div>선택한 멤버</div>
        <button>시작하기</button>
      </section>
      <section class="createroom-user">
        <div>
          <p>선택 가능한 멤버 리스트</p>
          <button>새로고침</button>
        </div>
      </section>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProfileImage from 'ProfileImage'
import Scroller from 'Scroller'
export default {
  name: 'WorkspaceCreateRoom',
  components: {
    Modal,
    ProfileImage,
    Scroller,
  },
  data() {
    return {
      visibleFlag: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      console.log(flag)
      this.visibleFlag = flag
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-modal"></style>
