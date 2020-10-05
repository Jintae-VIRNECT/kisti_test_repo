<template>
  <el-dialog
    class="member-password-modal"
    :visible.sync="showMe"
    width="440px"
    top="11vh"
  >
    <div slot="title">
      <img
        src="~assets/images/icon/ic-arrow-back.svg"
        @click="showMe = false"
      />
      <span class="el-dialog__title">{{ $t('members.password.title') }}</span>
    </div>
    <div>
      <p>{{ $t('members.password.desc') }}</p>
      <el-form
        ref="form"
        class="virnect-workstation-form"
        :model="form"
        :show-message="false"
      >
        <el-form-item
          class="horizon"
          prop="password"
          required
          :label="$t('members.password.newPassword')"
        >
          <el-input
            v-model="form.password"
            :placeholder="$t('members.password.placeholder')"
          />
        </el-form-item>
        <el-form-item class="horizon" prop="password2" required>
          <el-input
            v-model="form.password2"
            :placeholder="$t('members.password.placeholder2')"
          />
        </el-form-item>
        <p>{{ $t('members.password.caution') }}</p>
      </el-form>
    </div>
    <div slot="footer">
      <el-button type="primary" @click="submit">
        {{ $t('common.update') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'

export default {
  mixins: [modalMixin],
  props: {
    data: Object,
  },
  data() {
    return {
      form: {
        password: '',
        password2: '',
      },
    }
  },
  methods: {
    opened() {
      this.form = {
        password: '',
        password2: '',
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-password-modal {
  .el-dialog__header {
    div > * {
      display: inline-block;
      vertical-align: middle;
    }
    img {
      position: relative;
      left: -6px;
      margin-right: 2px;
      cursor: pointer;
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;
  }
  .el-form {
    margin: 24px 0;

    .el-form-item {
      margin-bottom: 8px;
    }
    p {
      color: $font-color-desc;
      font-size: 12px;
      line-height: 1.5;
      opacity: 0.8;
    }
  }
}
</style>
