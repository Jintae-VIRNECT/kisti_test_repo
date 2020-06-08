<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-price">
        <span>
          {{ scope.row[prop].toLocaleString() }}
          {{ $t('payment.monetaryUnit') }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
