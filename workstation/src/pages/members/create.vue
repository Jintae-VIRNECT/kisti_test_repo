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
          <el-button type="primary" :disabled="!userInfoList.length">
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
            :rules="rules"
            :show-message="false"
          >
            <el-row>
              <el-col :span="12">
                <label>{{ $t('members.create.id') }}</label>
                <el-form-item prop="id" required>
                  <el-input
                    v-model="form.id"
                    :placeholder="$t('members.create.idPlaceholder')"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12"> </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <label>{{ $t('members.create.password') }}</label>
                <el-form-item prop="password" required>
                  <el-input
                    v-model="form.password"
                    :placeholder="$t('members.create.passwordPlaceholder')"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12"> </el-col>
            </el-row>
          </el-form>
        </el-card>
      </el-row>
      <!-- 주의사항 -->
      <el-row class="caution">
        <div
          v-for="(caution, index) in $t('members.create.caution')"
          :key="index"
        >
          <p v-html="caution.main" />
          <ul v-if="caution.sub">
            <li v-for="(sub, index) in caution.sub" :key="index">
              <p v-html="sub" />
            </li>
          </ul>
        </div>
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
      rules: {
        email: [{ required: true, trigger: 'blur' }],
      },
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
  .user-info {
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
    .el-form-item {
      display: inline-block;
    }
    .el-col:first-child {
      label {
        width: 108px;
      }
      .el-form-item {
        width: 420px;
      }
    }
  }
  .caution {
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
