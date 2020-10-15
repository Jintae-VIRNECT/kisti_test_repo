<template>
  <el-dialog
    class="member-delete-modal"
    :visible.sync="showMe"
    width="440px"
    top="11vh"
  >
    <div slot="title">
      <img
        src="~assets/images/icon/ic-arrow-back.svg"
        @click="showMe = false"
      />
      <span class="el-dialog__title">{{ $t('members.delete.title') }}</span>
    </div>
    <div>
      <p>{{ $t('members.delete.desc') }}</p>
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
          :label="$t('members.delete.masterPassword')"
        >
          <el-input
            show-password
            v-model="form.password"
            :placeholder="$t('members.delete.placeholder')"
          />
        </el-form-item>
      </el-form>
    </div>
    <div slot="footer">
      <el-button type="primary" @click="submit">
        {{ $t('common.delete') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'

export default {
  mixins: [modalMixin],
  props: {
    data: Object,
  },
  data() {
    return {
      form: {
        password: '',
      },
    }
  },
  methods: {
    opened() {
      this.form = {
        password: '',
      }
    },
    async submit() {
      // 유효성 검사
      try {
        await this.$refs.form.validate()
      } catch (e) {
        return false
      }
      // api 요청
      try {
        await workspaceService.deleteMember(this.data.uuid, this.form.password)

        this.$alert(
          this.$t('members.delete.message.successContent'),
          this.$t('members.delete.message.successTitle'),
          {
            confirmButtonText: this.$t('common.confirm'),
            callback: () => this.$emit('kicked'),
          },
        )
      } catch (e) {
        const errMsg =
          {
            1003: this.$t('members.delete.message.wrongPassword'),
          }[e.code] ||
          this.$t('members.delete.message.fail') + ` [ERROR CODE : ${e.code}]`
        // 에러
        this.$message.error({
          message: errMsg,
          duration: 4000,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-delete-modal {
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
      margin-bottom: 48px;
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
