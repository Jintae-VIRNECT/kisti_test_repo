<template>
  <modal
    title="원격협업 상세보기"
    :width="900"
    :height="843"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
  >
    <div class="roominfo">
      <section class="roominfo-nav">
        <img class="roominfo-nav__image" />
        <button class="roominfo-nav__menu">
          협업 정보
        </button>
        <button class="roominfo-nav__menu">
          참가자 정보
        </button>
      </section>
      <section class="roominfo-view">
        <scroller v-if="users.length > 0">
          <div>
            <p class="roominfo-view__title">
              선택 가능한 멤버 리스트
            </p>
            <div class="roominfo-view__body">
              <input-row type="text"></input-row>
              <input-row type="textarea"></input-row>
              <input-row type="buttons"></input-row>
            </div>
            <div class="roominfo-view__footer">
              <div>
                <span>협업 진행일</span>
                <span>협업 진행일</span>
              </div>
              <div>
                <span>협업 진행일</span>
                <span>협업 진행일</span>
              </div>
              <button class="btn large">저장하기</button>
            </div>
          </div>
        </scroller>
      </section>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import InputRow from 'InputRow'
import Scroller from 'Scroller'

import mixinImage from 'mixins/uploadImage'
export default {
  name: 'WorkspaceRoomInfo',
  mixins: [mixinImage],
  components: {
    Modal,
    InputRow,
    Scroller,
  },
  data() {
    return {
      selection: [],
      visibleFlag: false,
      name: '',
      description: '',
      image: null,
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
      if (flag) {
        this.reset()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    reset() {
      this.selection = []
      this.name = ''
      this.description = ''
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.id === select.id)
      if (idx < 0) {
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-roominfo.scss"
></style>
