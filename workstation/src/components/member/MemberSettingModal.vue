<template>
  <el-dialog
    class="member-setting-modal"
    :visible.sync="showMe"
    :title="$t('members.setting.title')"
    width="440px"
  >
    <div>
      <h6>{{ $t('members.setting.info') }}</h6>
      <dl>
        <dt>{{ $t('members.setting.nickname') }}</dt>
        <dd class="column-user">
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${data.profile})`"
            />
          </div>
          <span>{{ data.nickname }}</span>
        </dd>
        <dt>{{ $t('members.setting.email') }}</dt>
        <dd>{{ data.email }}</dd>
      </dl>
      <el-divider />
      <h6>{{ $t('members.setting.setting') }}</h6>
      <el-form> </el-form>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: {
    data: Object,
    visible: Boolean,
  },
  data() {
    return {
      showMe: false,
    }
  },
  watch: {
    visible(val) {
      this.showMe = val
    },
    showMe(val) {
      this.$emit('update:visible', val)
    },
  },
  methods: {},
}
</script>

<style lang="scss">
.member-setting-modal {
  h6 {
    margin-bottom: 20px;
    color: #445168;
    &:first-child {
      margin-bottom: 16px;
    }
  }
  dt {
    margin-bottom: 10px;
    color: $font-color-desc;
    font-size: 12px;
  }
  dd {
    margin-bottom: 20px;
  }
  .column-user {
    .avatar {
      width: 28px;
      height: 28px;
    }
    span {
      font-size: 18px;
    }
  }
  .el-divider {
    margin: 26px 0 20px;
  }
}
</style>
