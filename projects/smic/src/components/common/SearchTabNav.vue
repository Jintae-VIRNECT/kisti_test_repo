<template lang="pug">
  div
    el-input.tool.search(:placeholder='placeholder' v-model='searchInput' @change="onChangeSearch(searchInput, filter.value, sort.value)")
      el-button(slot='append' icon='el-icon-search')
    span 필터 : 
    el-select(v-model='filter.value' placeholder='Select' @change="onChangeSearch(searchInput, filter.value, sort.value)")
      el-option(v-for='item in filter.options' :key='item.value' :label='item.label' :value='item.value')
    span 정렬 : 
    el-select(v-model='sort.value' placeholder='Select' @change="onChangeSearch(searchInput, filter.value, sort.value)")
      el-option(v-for='item in sort.options' :key='item.value' :label='item.label' :value='item.value')
</template>

<script>
export default {
  props: {
    placeholder: String,
    filter: Object,
    sort: Object,
  },
  data() {
    return {
      searchInput: null,
    }
  },
  methods: {
    async onChangeSearch(searchInput, filterValue, sortValue) {
      this.$emit('onChangeSearch')
      let tmpTableData = this.$store.getters.currentReportedDetailProcess
      tmpTableData = await this.onChangeSearchText(tmpTableData, searchInput)
      tmpTableData = await this.onChangeFilter(tmpTableData, filterValue)
      tmpTableData = await this.onChangeSort(tmpTableData, sortValue)
      this.tableData = tmpTableData
    },
    onChangeSearchText(tableData, searchInput) {
      return tableData.filter(row => {
        return (
          row.processName.includes(searchInput) ||
          row.auths.some(a => a.includes(searchInput))
        )
      })
    },
    onChangeFilter(tableData, filterValue) {
      if (!filterValue) return tableData
      return tableData.filter(row => row.status === filterValue)
    },
    onChangeSort(tableData, sortValue) {
      if (!sortValue) return tableData
      if (sortValue === 'alphabetDesc')
        return tableData.sort((a, b) =>
          a.processName - b.processName ? 1 : -1,
        )
      else if (sortValue === 'alphabetAsc')
        return tableData.sort((a, b) =>
          a.processName - b.processName ? -1 : 1,
        )
      else if (sortValue === 'createdAtDesc')
        return tableData.sort((a, b) => (a.createdAt - b.createdAt ? 1 : -1))
      else if (sortValue === 'createdAtAsc')
        return tableData.sort((a, b) => (a.createdAt - b.createdAt ? -1 : 1))
    },
  },
}
</script>
