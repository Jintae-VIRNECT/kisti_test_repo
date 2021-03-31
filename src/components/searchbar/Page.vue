<template>
  <el-row class="searchbar__page" type="flex" justify="center">
    <el-pagination
      layout="prev, pager, next"
      :current-page.sync="myPage"
      :total="myTotal"
      @current-change="change"
    />
  </el-row>
</template>

<script>
export default {
  props: {
    value: Number,
    total: Number,
  },
  data() {
    return {
      myPage: this.value || 1,
      myTotal: this.total,
    }
  },
  watch: {
    value(val) {
      this.myPage = val
    },
    total(val) {
      this.myTotal = val
    },
    myPage(val) {
      this.$emit('update:value', val)
    },
  },
  methods: {
    change(page) {
      this.$emit('change', page)
    },
  },
}
</script>
