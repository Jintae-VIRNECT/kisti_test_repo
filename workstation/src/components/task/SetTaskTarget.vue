<template>
  <el-dialog
    id="set-task-target-modal"
    :visible.sync="showMe"
    :title="$t('task.new.title')"
    width="580px"
    top="11vh"
  >
    <p>{{ $t('task.target.targetSetting') }}</p>
    <!-- 복제 -->
    <el-row class="box" type="flex" @click.native="checkDuplicate">
      <el-col>
        <el-checkbox v-model="checkedDuplicate" />
      </el-col>
      <el-col>
        <h6>{{ $t('task.target.duplicate') }}</h6>
        <p v-html="$t('task.target.duplicateDesc')" />
      </el-col>
      <el-col>
        <h6>{{ $t('task.target.contentTarget') }}</h6>
        <i class="circle" />
      </el-col>
      <el-col>
        <h6>{{ $t('task.target.taskTarget') }}</h6>
        <i class="circle" />
      </el-col>
    </el-row>
    <!-- 전환 -->
    <el-row class="box" type="flex" @click.native="checkTransform">
      <el-col>
        <el-checkbox v-model="checkedTransform" />
      </el-col>
      <el-col>
        <h6>{{ $t('task.target.transform') }}</h6>
        <p v-html="$t('task.target.transformDesc')" />
      </el-col>
      <el-col>
        <h6>{{ $t('task.target.contentTarget') }}</h6>
        <i class="el-icon-close" />
      </el-col>
      <el-col>
        <h6>{{ $t('task.target.taskTarget') }}</h6>
        <i class="circle" />
      </el-col>
    </el-row>
    <p>{{ $t('task.target.registerComment') }}</p>
    <template slot="footer">
      <el-button @click="$emit('prev')">
        {{ $t('common.prev') }}
      </el-button>
      <el-button
        @click="submit"
        type="primary"
        :disabled="!checkedDuplicate && !checkedTransform"
      >
        {{ $t('task.target.register') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import taskService from '@/services/task'

export default {
  mixins: [modalMixin],
  props: {
    form: Object,
  },
  data() {
    return {
      checkedDuplicate: false,
      checkedTransform: false,
    }
  },
  methods: {
    checkDuplicate() {
      this.checkedDuplicate = !this.checkedDuplicate
      this.checkedTransform = !this.checkedDuplicate
    },
    checkTransform() {
      this.checkedTransform = !this.checkedTransform
      this.checkedDuplicate = !this.checkedTransform
    },
    async submit() {
      const form = this.form
      form.targetType = 'QR' // this.checkedDuplicate ? 'duplicate' : 'transform'
      try {
        const data = await taskService.createTask(form)
        this.$message.success({
          message: this.$t('task.target.message.registerSuccess'),
          showClose: true,
        })
        this.$router.push(`/tasks/${data.id}`)
      } catch (e) {
        this.$message.error({
          message: this.$t('task.target.message.registerFail'),
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#set-task-target-modal {
  .el-dialog__body > p {
    color: #445168;
    &:last-child {
      margin-bottom: 48px;
      font-size: 12.6px;
    }
  }
  .box {
    margin: 16px 0;
    padding: 22px;
    background-color: rgba(244, 246, 250, 0.8);
    border: solid 1px #e0e5ef;
    border-radius: 3px;
    cursor: pointer;

    .el-checkbox__inner {
      width: 16px;
      height: 16px;
      &:after {
        left: 5px;
        height: 8px;
      }
    }
    .el-checkbox.is-checked .el-checkbox__inner {
      background: $color-primary;
      border-color: $color-primary;
    }

    .el-col:nth-child(-n + 2) {
      width: auto;
      margin-right: 12px;
    }
    .el-col:nth-child(2) {
      flex-basis: 70%;
      padding-right: 20px;
      p {
        margin-top: 6px;
        color: $font-color-desc;
        font-size: 12px;
        line-height: 20px;
      }
    }
    .el-col:nth-child(n + 3) {
      flex-basis: 22%;
      align-self: center;
      text-align: center;
      .el-icon-close {
        margin-top: 6px;
        color: #e74141;
        font-weight: bold;
        font-size: 30px;
      }
      .circle {
        display: inline-block;
        width: 24px;
        height: 24px;
        margin-top: 8px;
        border: solid 3px #1468e2;
        border-radius: 50%;
      }
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;
    .el-button:first-child {
      float: left;
    }
  }
}
</style>
