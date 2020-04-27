<template>
  <div class="navbar__sort">
    <el-select v-model="myValue" @change="change">
      <el-option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
        :label="$t(option.label)"
      />
    </el-select>
  </div>
</template>

<script>
export default {
  props: {
    value: String,
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
