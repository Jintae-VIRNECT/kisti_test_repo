<template>
  <el-table-column
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-status">
        <span :class="scope.row[prop]">{{ codeToWord(scope.row[prop]) }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    label: String,
    prop: String,
    /**
     * i18n object name
     */
    statusList: String,
    width: Number,
    align: String,
    sortable: Boolean,
  },
  methods: {
    codeToWord(code) {
      return this.$t(`${this.statusList}.${code}`)
    },
  },
}
</script>

<style lang="scss">
.column-status {
  & > span {
    display: inline-block;
    box-sizing: border-box;
    min-width: 74px;
    margin: -10px 0;
    padding: 4px 10px;
    font-size: 12.6px;
    text-align: center;
    border: solid 1px;
    border-radius: 100px;
  }
}
</style>
