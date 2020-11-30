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
        :rules="rules"
        :show-message="false"
      >
        <el-form-item
          class="horizon"
          prop="password"
          required
          :label="$t('members.password.newPassword')"
        >
          <el-input
            show-password
            v-model="form.password"
            :placeholder="$t('members.password.placeholder')"
          />
        </el-form-item>
        <el-form-item class="horizon" prop="password2" required>
          <el-input
            show-password
            v-model="form.password2"
            :placeholder="$t('members.password.placeholder2')"
          />
        </el-form-item>
        <p v-html="$t('members.password.caution')" />
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
        password2: '',
      },
      rules: {
        password: [
          {
            trigger: ['blur', 'change'],
            validator: (rule, value, callback) => {
              let err
              let typeCount = 0
              if (/[0-9]/.test(value)) typeCount++
              if (/[a-z]/.test(value)) typeCount++
              if (/[A-Z]/.test(value)) typeCount++
              if (/[$.$,$!$@$#$$$%]/.test(value)) typeCount++

              if (typeCount < 3) err = new Error()
              if (!/^.{8,20}$/.test(value)) err = new Error()
              if (/(.)\1\1\1/.test(value)) err = new Error()
              if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(value))
                err = new Error()
              if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(value))
                err = new Error()

              callback(err)
            },
          },
        ],
        password2: [
          {
            trigger: ['blur', 'change'],
            validator: (rule, value, callback) => {
              if (value === this.form.password) callback()
              else callback(new Error())
            },
          },
        ],
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
    async submit() {
      // 유효성 검사
      try {
        await this.$refs.form.validate()
      } catch (e) {
        return false
      }
      // api 요청
      try {
        await workspaceService.changeMembersPassword(
          this.data.uuid,
          this.form.password,
        )

        this.$message.success({
          message: this.$t('members.password.message.success'),
          duration: 4000,
          showClose: true,
        })
        this.showMe = false
      } catch (e) {
        // 에러
        this.$message.error({
          message:
            this.$t('members.password.message.fail') +
            ` [ERROR CODE : ${e.code}]`,
          duration: 4000,
          showClose: true,
        })
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
