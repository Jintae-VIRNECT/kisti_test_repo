<template>
  <el-table-column :width="width" :align="align">
    <template slot-scope="scope">
      <el-dropdown trigger="click">
        <span class="column-dropdown el-dropdown-link" @click.stop>
          <img src="~assets/images/icon/ic-more-horiz.svg" />
        </span>
        <el-dropdown-menu slot="dropdown">
          <slot v-bind:row="scope.row" />
        </el-dropdown-menu>
      </el-dropdown>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    width: Number,
    align: String,
  },
}
</script>
