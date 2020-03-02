<template lang="pug">
  el-row(type="flex" justify="center")
    el-pagination(
      layout="prev, pager, next"
      :total="totalElements"
      :page-size="params.size"
      @current-change="onPageChange"
    )
</template>

<script>
export default {
  props: {
    target: String,
    params: Object,
  },
  computed: {
    totalElements() {
      return this.$store.getters[`${this.target}Total`]
    },
  },
  methods: {
    async onPageChange(val) {
      const upperStart = this.target[0].toUpperCase() + this.target.slice(1)
      await this.$store.dispatch(`get${upperStart}List`, {
        ...this.params,
        page: val - 1,
      })
    },
  },
}
</script>
