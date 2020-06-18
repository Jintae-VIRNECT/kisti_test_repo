<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-boolean">
        <span :class="booleanClass(scope.row[prop])">
          {{ booleanText(scope.row[prop]) }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  props: {
    label: String,
    prop: String,
    width: Number,
    trueText: String,
    falseText: String,
    sortable: [Boolean, String],
  },
  methods: {
    booleanText(bool) {
      return this.boolCheck(bool) ? this.trueText : this.falseText
    },
    booleanClass(bool) {
      return this.boolCheck(bool) ? 'true' : 'false'
    },
  },
}
</script>
