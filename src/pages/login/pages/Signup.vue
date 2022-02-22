<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('signup.signupTitle') }}</h2>
        <p class="disc">{{ $t('signup.signupDesk') }}</p>
        <p class="input-title must-check">{{ $t('signup.account') }}</p>
        <el-input
          class="email-input"
          :placeholder="$t('signup.mailPlaceholder')"
          v-model="signup.email"
          type="email"
          name="email"
          clearable
          :disabled="authLoading"
          :class="{
            'input-danger': !emailValid && signup.email !== '',
          }"
        >
        </el-input>
        <el-button
          class="block-btn verification-btn"
          type="info"
          :disabled="!emailValid"
          v-if="!authLoading"
          @click="sendEmail()"
        >
          <span>{{ $t('signup.authentication.mail') }}</span>
        </el-button>

        <el-input
          class="verification-input"
          :placeholder="$t('signup.authentication.number')"
          v-if="isVerification"
          v-model="verificationCode"
          type="text"
          name="verificationCode"
          clearable
          maxlength="6"
          :class="{
            'input-danger': verificationCode.length < 6,
          }"
        >
        </el-input>
        <el-button
          class="block-btn"
          type="info"
          :disabled="verificationCode.length !== 6 || !isVerification"
          v-if="authLoading"
          @click="checkVerificationCode()"
        >
          <span>{{ $t(verificationText) }}</span>
        </el-button>

        <button
          class="resend-btn"
          v-if="isVerification == true"
          :class="{ disabled: !setCount }"
          @click="resendEmail()"
        >
          {{ $t('signup.authentication.reSubmit') }}
        </button>

        <PasswordConfirm
          :pass="signup"
          :setString="setString"
          @watchInput="watchInput"
        />

        <p class="input-title must-check">{{ $t('signup.name.name') }}</p>
        <el-input
          class="lastname-input"
          :placeholder="$t('signup.name.last')"
          clearable
          name="lastname"
          v-validate="'required'"
          v-model="signup.lastName"
          :class="{ 'input-danger': /(\s)/g.test(signup.lastName) }"
        ></el-input>
        <el-input
          class="firstname-input"
          :placeholder="$t('signup.name.first')"
          clearable
          name="firstname"
          v-validate="'required'"
          v-model="signup.firstName"
          :class="{ 'input-danger': /(\s)/g.test(signup.firstName) }"
        ></el-input>

        <p class="input-title must-check">{{ $t('signup.birth.birth') }}</p>
        <el-date-picker
          class="birth-input year-input"
          popper-class="year"
          v-model="birth.year"
          type="year"
          name="birtnY"
          maxlength="4"
          :data-placeholder="$t('signup.birth.year')"
          v-validate="'required'"
          :clearable="false"
          :picker-options="pickerOptions"
        ></el-date-picker>

        <el-date-picker
          class="birth-input month-input"
          popper-class="month"
          v-model="birth.month"
          type="month"
          name="birthM"
          format="MM"
          maxlength="2"
          :data-placeholder="$t('signup.birth.month')"
          v-validate="'required'"
          :clearable="false"
          :picker-options="pickerOptions"
        ></el-date-picker>

        <el-date-picker
          class="birth-input date-input"
          popper-class="day"
          v-model="birth.date"
          type="date"
          name="birthD"
          format="dd"
          maxlength="2"
          :data-placeholder="$t('signup.birth.date')"
          :clearable="false"
          :picker-options="pickerOptions"
        ></el-date-picker>

        <p class="input-title must-check">{{ $t('signup.route.title') }}</p>
        <el-select
          v-model="joinInfo"
          :placeholder="$t('signup.route.select')"
          name="joinInfo"
          @change="resetJoinInfo"
        >
          <el-option
            v-for="(item, index) in subscriptionPath"
            :key="index"
            :label="$t(item.label)"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-input
          :placeholder="$t('signup.route.placeholder')"
          v-if="joinInfo === subscriptionPath.length - 1"
          v-model="signup.joinInfo"
          type="text"
          name="email"
          clearable
        >
        </el-input>

        <p class="input-title must-check">
          {{ $t('signup.serviceInfo.title') }}
        </p>
        <el-select
          v-model="serviceInfo"
          :placeholder="$t('signup.serviceInfo.select')"
          name="serviceInfo"
          @change="resetServiceInfo"
        >
          <el-option
            v-for="(item, index) in serviceInfoLists"
            :key="index"
            :label="$t(item.label)"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-input
          :placeholder="$t('signup.serviceInfo.placeholder')"
          v-if="serviceInfo === serviceInfoLists.length - 1"
          v-model="signup.serviceInfo"
          type="text"
          name="email"
          clearable
        >
        </el-input>
        <el-button
          class="next-btn block-btn"
          type="info"
          :disabled="!nextBtn"
          @click="checkAge()"
          >{{ $t('signup.next') }}</el-button
        >
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref } from '@vue/composition-api'
import Signup from 'model/signup'
import sighupSlice from 'service/slice/signup.slice'
import PasswordConfirm from 'components/password/PasswordConfirm'
export default {
  name: 'signup',
  props: {
    marketInfoReceive: Boolean,
    policyAgree: Boolean,
  },
  emits: ['watchInput'],
  components: { PasswordConfirm },
  setup(props, { root }) {
    const signup = ref(new Signup())
    const pickerOptions = ref({
      disabledDate(time) {
        return time.getTime() > Date.now()
      },
    })
    const setString = {
      password: root.$t('signup.password.comfirm'),
      passWordConfirm: root.$t('signup.password.reComfirm'),
    }
    const SIGHUP_SLICE = sighupSlice(props, root, signup)
    return {
      ...SIGHUP_SLICE,
      pickerOptions,
      setString,
    }
  },
}
</script>

<style lang="scss" scoped>
.el-button.next-btn {
  margin-top: 60px;
}
</style>
<style lang="scss">
.el-popper {
  &.month,
  &.day {
    .el-date-picker__header-label {
      pointer-events: none;
    }
    .el-picker-panel__icon-btn {
      display: none;
    }
  }
}
</style>
