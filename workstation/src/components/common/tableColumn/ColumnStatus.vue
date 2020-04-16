<template>
  <el-table-column :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-status">
        <el-tag :class="statusColor(scope.row[prop])">
          {{ $t(statusLabel(scope.row[prop])) }}
        </el-tag>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    label: String,
    prop: String,
    width: Number,
    /**
     * [{ value, label, color }]
     */
    statusList: Array,
  },
  methods: {
    statusLabel(val) {
      return this.statusList.find(status => status.value === val).label
    },
    statusColor(val) {
      return this.statusList.find(status => status.value === val).color
    },
  },
}
</script>

<style lang="scss">
.column-status {
  .el-tag {
    display: inline-block;
    color: #fff;
    border: none;
    border-radius: 4px;

    &.silver {
      background-color: $color-silver;
    }
    &.gray {
      background-color: $color-gray;
    }
    &.dark-gray {
      background-color: $color-dark-gray;
    }
    &.blue {
      background-color: $color-blue;
    }
    &.dark-blue {
      background-color: $color-dark-blue;
    }
    &.dark-red {
      background-color: $color-dark-red;
    }
    &.orange {
      background-color: $color-orange;
    }
    &.green {
      background-color: $color-green;
    }
  }
}
</style>
