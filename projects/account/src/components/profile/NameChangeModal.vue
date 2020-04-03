<template>
  <el-dialog
    class="name-change-modal"
    :title="$t('profile.nameChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile.nameChangeModal.desc')"></p>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="confirm" @click="submit">
        {{ $t('profile.nameChangeModal.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'

export default {
  mixins: [dialogMixin],
  props: {},
  data() {
    return {}
  },
  watch: {
    visible() {},
  },
  methods: {
    submit() {},
  },
}
</script>

<style lang="scss"></style>
