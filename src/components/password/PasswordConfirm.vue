<template>
  <div>
    <p class="input-title must-check">
      {{ $t('signup.password.pass') }}
    </p>
    <el-input
      class="password-input"
      :placeholder="setString.password"
      v-model="password"
      type="password"
      name="password"
      clearable
      @input="e => watchInput(e, 'password')"
      :class="{ 'input-danger': !passValid && password !== '' }"
    >
    </el-input>
    <el-input
      class="passwordconfirm-input"
      :placeholder="setString.passWordConfirm"
      v-model="passwordConfirm"
      type="password"
      name="passwordConfirm"
      clearable
      :class="{
        'input-danger':
          password !== passwordConfirm ||
          (!passValid && passwordConfirm !== ''),
      }"
    >
    </el-input>
    <p class="restriction-text">
      {{ $t('signup.password.notice') }}
    </p>
  </div>
</template>

<script>
import { ref, computed } from '@vue/composition-api'
import { passValidate } from 'mixins/validate'
export default {
  props: {
    pass: Object,
    setString: Object,
  },
  setup(props, { emit }) {
    const passValid = computed(() => {
      return passValidate(props.pass.password)
    })
    const password = ref(props.pass.password)
    const passwordConfirm = ref('')
    const watchInput = (val, key) => {
      emit('watchInput', val, key)
    }
    return {
      passValid,
      password,
      passwordConfirm,
      watchInput,
    }
  },
}
</script>
