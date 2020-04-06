<template>
  <el-dialog
    class="nickname-change-modal"
    :title="$t('profile.nicknameChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile.nicknameChangeModal.desc')"></p>
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item :label="$t('')">
          <el-input v-model="form.name" />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="confirm" @click="submit">
        {{ $t('profile.nicknameChangeModal.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import profileService from '@/services/profile'

export default {
  mixins: [dialogMixin],
  props: {
    me: Object,
  },
  data() {
    return {
      form: {},
    }
  },
  watch: {
    visible() {},
  },
  methods: {
    async submit() {},
  },
}
</script>

<style lang="scss"></style>
