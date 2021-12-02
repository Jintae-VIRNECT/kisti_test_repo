<template>
  <div class="find-body">
    <p class="info-text" v-html="mailFindText"></p>

    <div v-if="isFindEmail === null">
      <el-radio v-model="tabCategory" :label="1">{{
        $t('find.number')
      }}</el-radio>
      <div v-show="tabCategory === 1">
        <p class="input-title">{{ $t('find.name.title') }}</p>
        <el-input
          class="lastname-input"
          :placeholder="$t('find.name.lastName')"
          clearable
          v-model="fullName.lastName"
          :class="{
            'input-danger':
              /\w/g.test(fullName.lastName) && fullName.lastName !== '',
          }"
        ></el-input>
        <el-input
          class="firstname-input"
          :placeholder="$t('find.name.firstName')"
          clearable
          v-model="fullName.firstName"
          :class="{
            'input-danger':
              /\w/g.test(fullName.firstName) && fullName.firstName !== '',
          }"
        ></el-input>
        <p class="input-title">{{ $t('find.mobile.title') }}</p>
        <el-select
          v-model="findEmail.countryCode"
          placeholder="+82"
          class="countrycode-input"
          name="countryCode"
        >
          <el-option
            v-for="item in countryCodeLists"
            :key="item.value"
            :label="item.value"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-input
          :placeholder="$t('find.mobile.placeHolder')"
          class="phonenumber-input"
          clearable
          name="phoneNumber"
          v-model="findEmail.phoneNumber"
          :class="{
            'input-danger': !phoneNumberValid && findEmail.phoneNumber !== '',
          }"
        ></el-input>
      </div>

      <el-radio v-model="tabCategory" :label="2">{{
        $t('find.recoveryMail')
      }}</el-radio>

      <div v-show="tabCategory === 2">
        <p class="input-title">{{ $t('find.name.title') }}</p>
        <el-input
          class="lastname-input"
          :placeholder="$t('find.name.lastName')"
          clearable
          v-model="fullName.lastName"
          :class="{
            'input-danger': errors.has('tab-category-2-lastname'),
          }"
        ></el-input>
        <el-input
          class="firstname-input"
          :placeholder="$t('find.name.firstName')"
          clearable
          v-model="fullName.firstName"
          :class="{
            'input-danger': errors.has('tab-category-2-firstname'),
          }"
        ></el-input>
        <p class="input-title">
          {{ $t('user.recoveryEmail.title') }}
        </p>
        <el-input
          type="email"
          :placeholder="$t('user.recoveryEmail.placeHolder')"
          clearable
          name="recoveryEmail"
          v-model="findEmail.recoveryEmail"
          :class="{
            'input-danger': !emailValid && findEmail.recoveryEmail !== '',
          }"
        ></el-input>
      </div>
      <el-button
        class="next-btn block-btn"
        type="primary"
        :disabled="!emailFindActive"
        @click="mailAccountFind"
        >{{ $t('find.findBtn') }}</el-button
      >
    </div>
    <div v-else class="mailfind-before">
      <div
        class="user-email-holder"
        v-for="(user, idx) of findUserData"
        :key="idx"
      >
        <p>
          <i>{{ $t('login.email') }}:</i>
          <span>{{ user.email }}</span>
        </p>
        <p>
          <i>{{ $t('find.signupDate') }}:</i>
          <span>{{ user.signUpDate }}</span>
        </p>
      </div>
      <el-button
        class="next-btn block-btn"
        type="primary"
        @click="$router.push({ name: 'login' })"
        >{{ $t('login.title') }}</el-button
      >
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from '@vue/composition-api'
import { countryCode } from 'model/countryCode'
import UserService from 'service/user-service'
import { alertMessage } from 'mixins/alert'
import { emailValidate, mobileNumberValidate } from 'mixins/validate'
export default {
  setup(props, { root }) {
    const tabCategory = ref(1)
    const findEmail = ref({
      countryCode: '+82',
      phoneNumber: '',
      recoveryEmail: '',
    })
    const fullName = ref({
      firstName: '',
      lastName: '',
    })
    const isFindEmail = ref(null)
    const countryCodeLists = countryCode

    const phoneNumberValid = computed(() => {
      return mobileNumberValidate(findEmail.value.phoneNumber)
    })
    const emailValid = computed(() => {
      return emailValidate(findEmail.value.recoveryEmail)
    })

    const emailFindActive = computed(() => {
      if (fullName.value.firstName === '') return false
      if (fullName.value.lastName === '') return false
      if (tabCategory.value === 1) {
        if (phoneNumberValid.value) return true
        return false
      } else {
        if (emailValid.value) return true
        return false
      }
    })

    const mobileSet = computed(() => {
      if (
        findEmail.value.countryCode === '' ||
        findEmail.value.phoneNumber === ''
      )
        return ''
      else
        return `${
          findEmail.value.countryCode
        }-${findEmail.value.phoneNumber.replace(/[^0-9+]/g, '')}`
    })

    const mailFindText = computed(() => {
      if (isFindEmail.value) return root.$t('find.EmailFindText.after')
      else return root.$t('find.EmailFindText.before')
    })

    const findUserData = ref([])

    const mailAccountFind = async () => {
      try {
        const res = await UserService.userFindEmail({
          params: {
            firstName: fullName.value.firstName,
            lastName: fullName.value.lastName,
            mobile: mobileSet.value,
            recoveryEmail: findEmail.value.recoveryEmail,
          },
        })
        if (res.code === 200) {
          isFindEmail.value = true
          findUserData.value = res.data.emailFindInfoList
        } else throw res
      } catch (e) {
        // console.log(e)
        if (e.code === 4002)
          return alertMessage(
            root,
            root.$t('find.accountError.notSync.title'),
            root.$t('find.accountError.notSync.message'),
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.accountError.etc.title'),
            root.$t('find.accountError.etc.message'),
            'error',
          )
      }
    }

    watch(
      () => tabCategory.value,
      () => {
        findEmail.value.phoneNumber = ''
        findEmail.value.recoveryEmail = ''
      },
    )

    return {
      tabCategory,
      findEmail,
      fullName,
      countryCodeLists,
      phoneNumberValid,
      emailFindActive,
      mailFindText,
      isFindEmail,
      findUserData,
      emailValid,
      mailAccountFind,
    }
  },
}
</script>
