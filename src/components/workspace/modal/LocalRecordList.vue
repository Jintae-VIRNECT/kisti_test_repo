<template>
  <modal
    :visible.sync="visibleFlag"
    :title="'로컬 녹화 파일'"
    :showClose="true"
    :width="898"
    :beforeClose="beforeClose"
  >
    <div style="height: 300px;">
      <div class="">
        <p>
          ᛫ 완료된 로컬 녹화 파일은 사용하시는 PC의 시스템에 귀속되어 있습니다.
        </p>
        <p>
          ᛫ 공용 PC의 경우, 다른 계정에 의해서 녹화된 파일도 보일 수 있습니다.
        </p>
        <p>
          ᛫ 중요한 녹화 파일의 경우, [다운로드]를 하신 후 파일 리스트를 삭제하여
          주시기 바랍니다.
        </p>
      </div>
      <div class=""></div>
      <div class=""></div>
    </div>
  </modal>
</template>
<script>
import Modal from 'Modal'

export default {
  data() {
    return {
      visibleFlag: false,
    }
  },
  components: {
    Modal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },
}
</script>

<style lang="scss"></style>
