<!-- onpremise page -->

<template>
  <div id="members-create">
    <div class="container">
      <div class="title">
        <el-breadcrumb>
          <el-breadcrumb-item to="/members">
            <img src="~assets/images/icon/ic-arrow-back.svg" />
            <span>{{ $t('common.back') }}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('members.create.title') }}</h2>
      </div>

      <el-row class="searchbar">
        <el-col class="left">
          <h4>{{ $t('members.create.form') }}</h4>
        </el-col>
        <el-col class="right">
          <el-button @click="addMember">
            {{ $t('members.create.addMember') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!userInfoList.length"
            @click="submit"
          >
            {{ $t('members.create.createMember') }}
            <span class="number">{{ userInfoList.length }}</span>
          </el-button>
        </el-col>
      </el-row>

      <el-row>
        <el-card
          class="user-info"
          v-for="(form, index) in userInfoList"
          :key="index"
        >
          <div slot="header">
            <h3>
              <img src="~assets/images/icon/ic-person.svg" />
              <span>{{ $t('members.create.info') }} {{ index + 1 }}</span>
            </h3>
            <button @click.prevent="clearMember(index)">
              <i class="el-icon-close" />
            </button>
          </div>
          <el-form
            ref="form"
            class="virnect-workstation-form"
            :model="form"
            :show-message="false"
          >
            <el-row>
              <el-col :span="9">
                <label class="required">
                  {{ $t('members.create.id') }}
                </label>
                <el-form-item prop="id" required>
                  <el-input
                    v-model="form.id"
                    :placeholder="$t('members.create.idPlaceholder')"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="15">
                <label>{{ $t('members.setting.role') }}</label>
                <el-select v-model="form.role" prop="role">
                  <el-option
                    class="column-role"
                    v-for="role in roles"
                    :key="role.value"
                    :value="role.value"
                  >
                    <el-tag :class="role.value">{{ $t(role.label) }}</el-tag>
                  </el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="9">
                <label class="required">
                  {{ $t('members.create.password') }}
                </label>
                <el-form-item prop="password" required>
                  <el-input
                    v-model="form.password"
                    :placeholder="$t('members.create.passwordPlaceholder')"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="15">
                <label class="required">
                  {{ $t('members.setting.givePlans') }}
                </label>
                <div class="plans">
                  <el-form-item :label="plans.remote.label">
                    <el-select v-model="form.planRemote" @change="choosePlan">
                      <el-option
                        :value="false"
                        :label="$t('members.setting.givePlansEmpty')"
                      />
                      <el-option
                        :value="true"
                        :label="plans.remote.label"
                        :disabled="!availablePlans.remote"
                      >
                        <span>{{ plans.remote.label }}</span>
                        <span class="right">
                          {{ availablePlans.remote }}
                        </span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item class="horizon" :label="plans.make.label">
                    <el-select v-model="form.planMake" @change="choosePlan">
                      <el-option
                        :value="false"
                        :label="$t('members.setting.givePlansEmpty')"
                      />
                      <el-option
                        :value="true"
                        :label="plans.make.label"
                        :disabled="!availablePlans.make"
                      >
                        <span>{{ plans.make.label }}</span>
                        <span class="right">
                          {{ availablePlans.make }}
                        </span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item class="horizon" :label="plans.view.label">
                    <el-select v-model="form.planView" @change="choosePlan">
                      <el-option
                        :value="false"
                        :label="$t('members.setting.givePlansEmpty')"
                      />
                      <el-option
                        :value="true"
                        :label="plans.view.label"
                        :disabled="!availablePlans.view"
                      >
                        <span>{{ plans.view.label }}</span>
                        <span class="right">
                          {{ availablePlans.view }}
                        </span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </el-row>
      <!-- 주의사항 -->
      <el-row class="caution">
        <p v-html="$t('members.create.caution.createId')" />
        <ul>
          <li v-html="$t('members.create.caution.validId')" />
          <li v-html="$t('members.create.caution.validPassword')" />
        </ul>
        <p v-html="$t('members.create.caution.canModify')" />
      </el-row>
    </div>
  </div>
</template>

<script>
import { role } from '@/models/workspace/Member'
import CreateMember from '@/models/workspace/CreateMember'
import workspaceService from '@/services/workspace'
import plans from '@/models/workspace/plans'
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      plans,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      userInfoList: [new CreateMember()],
      availablePlans: { remote: 0, make: 0, view: 0 },
    }
  },
  computed: {
    ...mapGetters({
      plansInfo: 'plan/plansInfo',
    }),
  },
  methods: {
    addMember() {
      this.userInfoList.push(new CreateMember())
    },
    clearMember(index) {
      this.userInfoList.splice(index, 1)
    },
    initAvailablePlans() {
      this.plansInfo.products.forEach(product => {
        this.availablePlans[product.value.toLowerCase()] = product.unUsedAmount
      })
    },
    choosePlan() {
      this.initAvailablePlans()
      this.userInfoList.forEach(user => {
        if (user.planRemote) this.availablePlans.remote -= 1
        if (user.planMake) this.availablePlans.make -= 1
        if (user.planView) this.availablePlans.view -= 1
      })
    },
    async submit() {
      // 유효성 검사
      try {
        await Promise.all(this.$refs.form.map(form => form.validate()))
      } catch (e) {
        return false
      }
      // api 요청
      try {
        // throw new Error('test error')
        this.$alert(
          this.$t('members.create.message.successContent'),
          this.$t('members.create.message.successTitle'),
          {
            confirmButtonText: this.$t('common.confirm'),
            callback: () => this.$router.push('/members'),
          },
        )
      } catch (e) {
        // 에러
        this.$message.error({
          message: e,
          duration: 4000,
          showClose: true,
        })
      }
    },
  },
  async beforeMount() {
    if (!this.plansInfo.planStatus) {
      await this.$store.dispatch('plan/getPlansInfo')
    }
    this.initAvailablePlans()
  },
}
</script>

<style lang="scss">
#__nuxt #members-create {
  .el-breadcrumb__inner > * {
    margin-right: 4px;
    font-size: 13px;
    vertical-align: middle;
    cursor: pointer;
    opacity: 0.8;
  }
  .searchbar {
    margin-top: 35px;
    .left {
      font-size: 16px;
    }
  }
  // 고객 정보
  .user-info {
    margin-bottom: 20px;

    .el-card__header {
      height: 44px;
      padding: 12px 20px;
      & > div > h3 > img {
        margin: 0 4px;
      }
      button i {
        color: $font-color-content;
        font-weight: bold;
        font-size: 18px;
      }
    }
    .el-card__body {
      padding: 32px;
    }
    label.required:after {
      color: $danger;
      content: '*';
    }
    .el-form-item {
      display: inline-block;
      margin-bottom: 24px;
    }
    .el-row:last-child .el-form-item {
      margin-bottom: 0;
    }
    .el-col:first-child {
      label {
        width: 108px;
      }
      .el-form-item {
        width: 420px;
      }
    }
    .el-col:last-child {
      label {
        width: 150px;
      }
    }
    .el-col:last-child .plans {
      display: inline-block;
      label {
        width: auto;
        margin-right: 10px;
        line-height: 38px;
        text-align: left;
      }
      .el-form-item__content {
        width: 274px;
      }
    }
  }
  // 주의사항
  .caution {
    margin-top: 20px;
    padding: 20px;
    font-size: 13px;
    background: rgba(217, 225, 236, 0.25);
    border-radius: 3px;
    li {
      margin: 10px;
      color: #7a869a;
    }
  }
}
</style>
