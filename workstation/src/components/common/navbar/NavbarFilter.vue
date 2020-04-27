<template>
  <div class="navbar__filter">
    <el-select v-model="myValue" multiple @change="change">
      <el-option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
      >
        {{ option.label }}
      </el-option>
    </el-select>
  </div>
</template>

<script>
export default {
  props: {
    value: Array,
    options: Array,
  },
  watch: {
    value(val) {
      this.myValue = val
    },
    myValue(val) {
      this.$emit('update:value', val)
    },
  },
  data() {
    return {
      myValue: this.value,
    }
  },
  methods: {
    change() {
      this.$emit('change', this.myValue)
    },
  },
}
</script>
