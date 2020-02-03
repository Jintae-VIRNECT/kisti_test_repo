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
    search: String,
    placeholder: String,
    filter: Object,
    sort: Object,
  },
  data() {
    return {
      searchInput: this.search,
    }
  },
  methods: {
    onChangeSearch(searchInput, filterValue, sortValue) {
      this.$emit('change', { searchInput, filterValue, sortValue })
    },
  },
}
</script>
