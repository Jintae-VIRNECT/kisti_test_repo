<template>
  <modal
    :visible.sync="visibleFlag"
    :dimClose="false"
    width="420px"
    class="plan-over"
  >
    <div class="plan-over__layout">
      <div class="plan-over__header">
        <p>{{ $t('workspace.plan_exceeded') }}</p>
      </div>
      <div class="plan-over__body">
        <p
          class="plan-over__description"
          v-html="$t('workspace.plan_exceeded_desctiption')"
        ></p>
        <button class="btn plan-over__link" @click="link">
          {{ $t('workspace.go_workspace_member') }}
        </button>
      </div>
      <div class="plan-over__footer">
        <button class="btn" @click="workspaceClear">
          {{ $t('button.confirm') }}
        </button>
      </div>
    </div>
  </modal>
</template>

<script>
import { mapActions } from 'vuex'
import Modal from 'Modal'
import { URLS } from 'configs/env.config'

export default {
  name: 'PlanOverflowModal',
  components: {
    Modal,
  },
  data() {
    return {
      visibleFlag: false,
    }
  },
  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
  },

  watch: {
    visible(flag) {
      this.visibleFlag = !!flag
    },
  },
  methods: {
    ...mapActions(['clearWorkspace']),
    link() {
      window.open(
        `${URLS['workstation']}/members?workspace=${this.workspace.uuid}`,
      )
    },
    workspaceClear() {
      this.clearWorkspace()
      this.$emit('update:visible', false)
    },
  },
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-planoverflow.scss"
></style>
