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
function check(bool) {
  if (bool > 0) return true
  else return bool && bool.toLowerCase() !== 'no'
}

export default {
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
      return check(bool) ? this.trueText : this.falseText
    },
    booleanClass(bool) {
      return check(bool) ? 'true' : 'false'
    },
  },
}
</script>
